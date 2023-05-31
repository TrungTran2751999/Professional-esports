package com.cg.service.esport.category;

import com.cg.domain.esport.dto.*;
import com.cg.domain.esport.entities.Category;
import com.cg.service.IGeneralService;
import org.hibernate.validator.constraints.Mod10Check;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICategoryService extends IGeneralService<Category> {
    CategoryResponseDTO create(CategoryDTO categoryDTO);
    CategoryResponseDTO updateNoAvartar(CategoryUpdateDTO categoryUpdateDTO);
    CategoryResponseDTO updateAvartar(CategoryAvartarDTO categoryAvartarDTO);
    Page<CategoryResponseDTO> filter(CategoryFilter categoryFilter, Pageable pageable);
    void deleteCategory(Long id);
}
