package com.history.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 人物对比数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonCompareDTO {
    private List<PersonDTO> persons;
}
