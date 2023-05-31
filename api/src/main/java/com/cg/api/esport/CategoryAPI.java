package com.cg.api.esport;

import com.cg.domain.esport.dto.*;
import com.cg.exception.DataInputException;
import com.cg.service.esport.category.ICategoryService;
import com.cg.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/api/category")
public class CategoryAPI {
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private AppUtils appUtils;
    @PostMapping("/filter")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> filterByAdmin(@Validated @RequestBody CategoryFilter categoryFilter, BindingResult bindingResult,
                                           @RequestParam(name = "sort", required = false, defaultValue = "ASC") String sort){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        int start = categoryFilter.getStart();
        int length = categoryFilter.getLength();
        int page = start / length + 1;
        Sort sortable = null;
        if (sort.equals("ASC")) {
            sortable = Sort.by("id").ascending();
        }
        if (sort.equals("DESC")) {
            sortable = Sort.by("id").descending();
        }
        Pageable pageable = PageRequest.of(page - 1, length, sortable);
        Page<CategoryResponseDTO> pageableCustomers = categoryService.filter(categoryFilter, pageable);
        List<CategoryResponseDTO> listOrganizer = pageableCustomers.getContent();
        return new ResponseEntity<>(listOrganizer, HttpStatus.OK);
    }
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> create(@Validated CategoryDTO categoryDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        return new ResponseEntity<>(categoryService.create(categoryDTO), HttpStatus.OK);
    }
    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> update(@Validated @RequestBody CategoryUpdateDTO categoryUpdateDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        return new ResponseEntity<>(categoryService.updateNoAvartar(categoryUpdateDTO), HttpStatus.OK);
    }
    @PutMapping("/avartar")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> updateAvartar(@Validated CategoryAvartarDTO categoryAvartarDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return appUtils.mapErrorToResponse(bindingResult);
        }
        String[] imageExtension = new String[]{"image/png", "image/jpg", "image/jpeg"};
        for(int i=0; i<imageExtension.length; i++){
            if(Objects.equals(categoryAvartarDTO.getAvartar().getContentType(), imageExtension[i])){
                return new ResponseEntity<>(categoryService.updateAvartar(categoryAvartarDTO), HttpStatus.OK);
            }else if(i == imageExtension.length-1){
                throw new DataInputException("Ảnh đại diện không đúng định dạng");
            }
        }
        return null;
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){
        categoryService.deleteCategory(id);
        return new ResponseEntity<>("DELETE SUCCESS", HttpStatus.OK);
    }
}
