package com.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 人物数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDTO {
    private Long id;
    private String uid;
    private String name;
    private String courtesyName;
    private String dynastyName;
    private List<Integer> years;
    private String yearsDisplay;
    private String gender;
    private List<String> roles;
    private String quote;
    private String bio;
    private List<String> tags;
    private List<String> relatedEvents;
    private List<String> relatedPersons;
}
