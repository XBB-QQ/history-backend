package com.history.controller;

import com.history.entity.PersonEntity;
import com.history.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();
        personRepository.save(PersonEntity.builder()
            .uid("test-person-1").name("孔子").courtesyName("")
            .years(List.of(-551, -479)).yearsDisplay("前551年—前479年")
            .gender("male").roles(List.of("思想家", "教育家"))
            .tags(List.of("儒家", "春秋")).quote("学而时习之").bio("伟大的思想家")
            .build());
        personRepository.save(PersonEntity.builder()
            .uid("test-person-2").name("老子").courtesyName("")
            .years(List.of(null, null)).yearsDisplay("约公元前6世纪")
            .gender("male").roles(List.of("思想家"))
            .tags(List.of("道家")).quote("道可道非常道").bio("道家创始人")
            .build());
        personRepository.save(PersonEntity.builder()
            .uid("test-person-3").name("武则天").courtesyName("")
            .years(List.of(624, 705)).yearsDisplay("624年—705年")
            .gender("female").roles(List.of("帝王"))
            .tags(List.of("唐朝", "女皇")).quote("宁教我负天下人").bio("中国历史上唯一的女皇帝")
            .build());
    }

    @Test
    void testGetPersons() throws Exception {
        mockMvc.perform(get("/api/v1/persons").param("page", "0").param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(3)))
            .andExpect(jsonPath("$.totalElements", is(3)));
    }

    @Test
    void testSearchPersons() throws Exception {
        mockMvc.perform(get("/api/v1/persons/search").param("keyword", "孔子"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].name", is("孔子")));
    }

    @Test
    void testFindByGender() throws Exception {
        mockMvc.perform(get("/api/v1/persons").param("gender", "female"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].name", is("武则天")));
    }

    @Test
    void testFindByRole() throws Exception {
        mockMvc.perform(get("/api/v1/persons").param("role", "思想家"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    void testGetPersonById() throws Exception {
        mockMvc.perform(get("/api/v1/persons/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("孔子")));
    }

    @Test
    void testGetPersonByUid() throws Exception {
        mockMvc.perform(get("/api/v1/persons/uid/test-person-3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("武则天")));
    }
}
