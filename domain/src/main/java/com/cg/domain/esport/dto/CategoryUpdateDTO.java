package com.cg.domain.esport.dto;

import com.cg.domain.esport.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateDTO {
    private Long id;
    private String name;
    private String signal;
    public Category toCategory(){
        return new Category()
                .setId(id)
                .setName(name)
                .setBrief(signal);
    }
}
