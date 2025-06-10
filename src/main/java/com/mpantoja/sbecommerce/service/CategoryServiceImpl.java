package com.mpantoja.sbecommerce.service;

import com.mpantoja.sbecommerce.exceptions.APIException;
import com.mpantoja.sbecommerce.exceptions.ResourceNotFoundException;
import com.mpantoja.sbecommerce.model.Category;
import com.mpantoja.sbecommerce.payload.CategoryDTO;
import com.mpantoja.sbecommerce.payload.CategoryResponse;
import com.mpantoja.sbecommerce.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize) {

        Pageable pagedetails = PageRequest.of(pageNumber, pageSize);
        Page<Category> categoryPage= categoryRepository.findAll(pagedetails);
        List<Category> categories = categoryPage.getContent();
        if (categories.isEmpty()) {
            throw new APIException("No Categories Found");
        }

        List<CategoryDTO> categoryDTOS = categories.stream().map(
                category -> modelMapper.map(category, CategoryDTO.class)).toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        return categoryResponse;
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {

        Category receivedCategory = modelMapper.map(categoryDTO, Category.class);

        Category categoryInDataBase = categoryRepository.findByCategoryName(receivedCategory.
                getCategoryName());
        if (categoryInDataBase != null) {
            throw new APIException("Category with name \"" + categoryDTO.getCategoryName() + "\" Already exists");
        }
        Category savedCategory = categoryRepository.save(receivedCategory);

        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categorDtoId) {

        Category receivedCategory = modelMapper.map(categoryDTO, Category.class);
        Category categoryInDataBase = categoryRepository.findById(categorDtoId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category", "ID", categorDtoId));

        receivedCategory.setCategoryId(categorDtoId);
        categoryInDataBase = categoryRepository.save(receivedCategory);

        return modelMapper.map(categoryInDataBase, CategoryDTO.class);

    }

    @Override
    public CategoryDTO deleteCategory(Long categoryDTOId) {

        Category category = categoryRepository.findById(categoryDTOId).
                orElseThrow(
                        () -> new ResourceNotFoundException("category ", "Id ", categoryDTOId));
//
        CategoryDTO deletedCategoryDTO = modelMapper.map(category, CategoryDTO.class);
        categoryRepository.delete(category);

        return deletedCategoryDTO;
    }


}
