package com.history.controller;

import com.history.entity.EventEntity;
import com.history.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        // 插入测试数据
        eventRepository.save(EventEntity.builder()
            .uid("test-event-1").title("测试事件一").year(-221)
            .yearDisplay("公元前221年").yearPrecision("exact")
            .category("朝代更迭").tags(java.util.List.of("秦朝", "统一"))
            .description("秦始皇统一六国").fulltext("详细全文").build());
        eventRepository.save(EventEntity.builder()
            .uid("test-event-2").title("测试事件二").year(618)
            .yearDisplay("公元618年").yearPrecision("exact")
            .category("盛世").tags(java.util.List.of("唐朝", "建国"))
            .description("唐朝建立").fulltext("详细全文").build());
        eventRepository.save(EventEntity.builder()
            .uid("test-event-3").title("测试事件三").year(-202)
            .yearDisplay("公元前202年").yearPrecision("exact")
            .category("朝代更迭").tags(java.util.List.of("汉朝", "建国"))
            .description("西汉建立").fulltext("详细全文").build());
    }

    @Test
    void testGetTimeline() throws Exception {
        mockMvc.perform(get("/api/v1/events/timeline"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].title", is("测试事件三")))  // 按年份排序
            .andExpect(jsonPath("$[1].title", is("测试事件一")))
            .andExpect(jsonPath("$[2].title", is("测试事件二")));
    }

    @Test
    void testGetEventsPaginated() throws Exception {
        mockMvc.perform(get("/api/v1/events").param("page", "0").param("size", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.totalElements", is(3)));
    }

    @Test
    void testSearchEvents() throws Exception {
        mockMvc.perform(get("/api/v1/events/search").param("keyword", "统一"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].title", is("测试事件一")));
    }

    @Test
    void testGetEventById() throws Exception {
        mockMvc.perform(get("/api/v1/events/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("测试事件一")));
    }

    @Test
    void testGetEventByUid() throws Exception {
        mockMvc.perform(get("/api/v1/events/uid/test-event-2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("测试事件二")));
    }

    @Test
    void testFindByCategory() throws Exception {
        mockMvc.perform(get("/api/v1/events").param("category", "朝代更迭"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    void testEventFieldsPresent() throws Exception {
        mockMvc.perform(get("/api/v1/events/timeline"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]", hasProperty("uid")))
            .andExpect(jsonPath("$[0]", hasProperty("title")))
            .andExpect(jsonPath("$[0]", hasProperty("year")))
            .andExpect(jsonPath("$[0]", hasProperty("yearDisplay")))
            .andExpect(jsonPath("$[0]", hasProperty("category")))
            .andExpect(jsonPath("$[0]", hasProperty("tags")));
    }
}
