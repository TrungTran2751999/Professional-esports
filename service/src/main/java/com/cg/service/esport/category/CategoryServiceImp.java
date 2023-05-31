package com.cg.service.esport.category;

import com.cg.domain.esport.dto.*;
import com.cg.domain.esport.entities.Avartar;
import com.cg.domain.esport.entities.Category;
import com.cg.exception.DataInputException;
import com.cg.repository.esport.CategoryFilterRepository;
import com.cg.repository.esport.CategoryRepository;
import com.cg.service.esport.avartar.IAvartarService;
import com.cg.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Service
public class CategoryServiceImp implements ICategoryService{
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private IAvartarService avartarService;
    @Autowired
    private AppUtils appUtils;
    @Autowired
    private CategoryFilterRepository categoryFilterRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getById(Long id) {
        return categoryRepository.getById(id);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void remove(Long id) {
        categoryRepository.deleteById(id);
    }
    @Override
    public Page<CategoryResponseDTO> filter(CategoryFilter categoryFilter, Pageable pageable) {
        return categoryFilterRepository.findAllByFilters(categoryFilter, pageable).map(category -> {
            Avartar avartar = avartarService.findByCategory(category);
            return category.toCategoryDTO().setAvartarDTO(avartar.toAvartarDTO());
        });
    }

    @Override
    @Transactional
    public CategoryResponseDTO create(CategoryDTO categoryDTO) {
        List<Category> checkCategory = categoryRepository.findByName(categoryDTO.getName());
        if(checkCategory.size() > 0) throw new DataInputException("categoryExisted");
        Category category = categoryRepository.save(categoryDTO.toCategory());
        Avartar avartar = avartarService.save(new Avartar().setCategory(category));
        avartar.setFileUrl(appUtils.uploadAvartar(categoryDTO.getAvartar(), avartar.getId()));
        avartar = avartarService.save(avartar);
        return category.toCategoryDTO().setAvartarDTO(avartar.toAvartarDTO());
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateNoAvartar(CategoryUpdateDTO categoryUpdateDTO) {
        List<Category> checkCategory = categoryRepository.findByName(categoryUpdateDTO.getName());
        if(checkCategory.size() > 0 ) throw new DataInputException("categoryExisted");
        Optional<Category> categoryOptional = categoryRepository.findById(categoryUpdateDTO.getId());
        if(categoryOptional.isPresent()){
            Avartar avartar = avartarService.findByCategory(categoryOptional.get());
            return categoryRepository.save(categoryUpdateDTO.toCategory()).toCategoryDTO().setAvartarDTO(avartar.toAvartarDTO());
        }
        throw new DataInputException("notFoundCategory");
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateAvartar(CategoryAvartarDTO categoryAvartarDTO) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryAvartarDTO.getId());
        if(categoryOptional.isPresent()){
            Avartar avartar = avartarService.findByCategory(categoryOptional.get());
            String fileIdDelete = avartar.getFileUrl();
            avartar.setFileUrl(appUtils.uploadAvartar(categoryAvartarDTO.getAvartar(), avartar.getId()));
            avartar = avartarService.save(avartar);
            if(fileIdDelete != null){
                String fileId = fileIdDelete.split("&")[1].split("=")[1];
                try {
                    appUtils.deleteFile(fileId);
                } catch (Exception e) {
                    throw new DataInputException("FAIL");
                }
            }
            return categoryOptional.get().toCategoryDTO().setAvartarDTO(avartar.toAvartarDTO());
        }
        throw new DataInputException("notFoundCategory");
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if(categoryOptional.isPresent()){
            Avartar avartar = avartarService.findByCategory(categoryOptional.get());
            String fileIdDelete = avartar.getFileUrl();
            if(fileIdDelete != null){
                String fileId = fileIdDelete.split("&")[1].split("=")[1];
                try {
                    appUtils.deleteFile(fileId);
                } catch (Exception e) {
                    throw new DataInputException("FAIL");
                }
            }
            avartarService.remove(avartar.getId());
            categoryRepository.deleteById(id);
        }else{
            throw new DataInputException("notFoundCategory");
        }
    }
}
