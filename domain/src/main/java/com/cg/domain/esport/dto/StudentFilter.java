package com.cg.domain.esport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentFilter {
    private String keyWord;
    private Boolean deleted;
    @Min(value = 1, message = "Số trang phải lớn hơn không")
    private int start;
    @Min(value = 2, message = "Tổng số phải lớn hơn 2")
    private int length;
}
