package com.history.config;

import com.history.entity.AdminUserEntity;
import com.history.repository.AdminUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * API Key 认证过滤器
 * 从 X-API-Key header 中读取 API Key，验证后设置 SecurityContext
 * 由 SecurityConfig 作为 Bean 注入
 */
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final AdminUserRepository adminUserRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader("X-API-Key");

        if (StringUtils.hasText(apiKey)) {
            try {
                AdminUserEntity user = adminUserRepository.findByApiKey(apiKey).orElse(null);
                if (user != null) {
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase());
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            user.getUsername(), null, List.of(authority)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // API Key 无效则忽略，由 Spring Security 返回 403
            }
        }

        filterChain.doFilter(request, response);
    }
}
