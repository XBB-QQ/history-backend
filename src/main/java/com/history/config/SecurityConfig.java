package com.history.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           ApiKeyAuthenticationFilter apiKeyFilter,
                                           JwtAuthenticationFilter jwtFilter) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                // 公开 API
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/verify").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                // 管理 API — 登录/验证公开，其余需 ADMIN 角色
                // 安全修复：原 .authenticated() 仅校验"已登录"，普通用户也能调 admin 接口
                // 现改为 hasRole('ADMIN')，ApiKeyAuthenticationFilter 已正确设置 ROLE_ADMIN
                .requestMatchers("/api/admin/auth/login").permitAll()
                .requestMatchers("/api/admin/auth/verify").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // L1 修复：列出所有房间（调试用）泄露玩家与线索信息，限制为 ADMIN
                // 其余 /api/game/** 接口仍公开（创建/查询/加入房间需要 roomId）
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/game/rooms").hasRole("ADMIN")
                // 用户 API（JWT 认证）
                .requestMatchers("/api/user/quiz/daily").permitAll()
                .requestMatchers("/api/user/quiz/random").permitAll()
                .requestMatchers("/api/user/quiz/answer").authenticated()
                .requestMatchers("/api/user/quiz/ranking").permitAll()
                .requestMatchers("/api/user/learning/progress").authenticated()
                .requestMatchers("/api/user/learning/lists").authenticated()
                .requestMatchers("/api/user/learning/**").authenticated()
                .requestMatchers("/api/user/**").authenticated()
                .requestMatchers("/api/favorites/**").authenticated()
                // 安全修复 S4：消耗 LLM 配额或敏感操作的端点需登录，防匿名滥用刷配额
                .requestMatchers("/api/llm/**").authenticated()
                .requestMatchers("/api/v1/rag/**").authenticated()
                .requestMatchers("/api/classics/translate").authenticated()
                .requestMatchers("/api/game/**").authenticated()
                // 其他 API 公开（events/persons/dynasties/knowledge/topics/map/classics/char-evolution 等查询类）
                .requestMatchers("/api/**").permitAll()
                .anyRequest().permitAll()
            );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 安全修复 S5：统一 CORS 配置（删除 WebConfig 中的重复 CorsFilter）
        // 使用 allowedOriginPatterns 而非 allowedOrigins，以兼容 allowCredentials=true + 通配符
        configuration.setAllowCredentials(true);
        String allowedOrigin = System.getenv("FRONTEND_ORIGIN");
        if (allowedOrigin != null && !allowedOrigin.isBlank()) {
            // 生产环境：FRONTEND_ORIGIN 支持逗号分隔的多域名
            configuration.setAllowedOriginPatterns(java.util.Arrays.asList(allowedOrigin.split("\\s*,\\s*")));
        } else {
            // 开发环境兜底：允许 localhost 任意端口 + https 任意域名
            configuration.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
        }
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "X-API-Key", "X-Total-Count"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public ApiKeyAuthenticationFilter apiKeyAuthenticationFilter(
            com.history.repository.AdminUserRepository adminUserRepository) {
        return new ApiKeyAuthenticationFilter(adminUserRepository);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}
