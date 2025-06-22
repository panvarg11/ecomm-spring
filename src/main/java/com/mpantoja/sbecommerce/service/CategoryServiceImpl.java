package com.mpantoja.sbecommerce.service;

import com.mpantoja.sbecommerce.Utils.PaginationUtil;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Pageable pageDetails = PaginationUtil.buildPageRequest(pageNumber, pageSize, sortBy, sortOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);


        List<Category> categories = categoryPage.getContent();
        if (categories.isEmpty()) {
            throw new APIException("No Categories Found");
        }

        List<CategoryDTO> categoryDTOS = categories.stream().map(
                category -> modelMapper.map(category, CategoryDTO.class)).toList();
        return new CategoryResponse(categoryDTOS, categoryPage.getNumber(), categoryPage.getSize(),
                categoryPage.getTotalElements(), categoryPage.getTotalPages(), categoryPage.isLast());
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {

        Category receivedCategory = modelMapper.map(categoryDTO, Category.class);

        Category categoryInDataBase = categoryRepository.findByCategoryName(receivedCategory.
                getCategoryName());
        if (categoryInDataBase != null) {
            throw new APIException("Category with name \"" + categoryDTO.getCategoryName() + "\" Already exists with id " + categoryDTO.getCategoryId());
        }
        Category savedCategory = categoryRepository.save(receivedCategory);

        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categorDtoId) {

        Optional<Category> categoryInDataBase = categoryRepository.findById(categorDtoId);
        if (categoryInDataBase.isEmpty()) throw new ResourceNotFoundException("Category", "ID", categorDtoId);
        if (categoryRepository.findByCategoryName(categoryDTO.getCategoryName()) != null)
            throw new APIException("Category with that name already exists :" + categoryDTO.getCategoryName());

        Category savedCategory = categoryInDataBase.get();
        savedCategory.setCategoryId(categorDtoId);
        savedCategory.setCategoryName(categoryDTO.getCategoryName());
        return modelMapper.map(categoryRepository.save(savedCategory), CategoryDTO.class);

    }

    @Override
    public CategoryDTO deleteCategory(Long categoryDTOId) {

        Category category = categoryRepository.findById(categoryDTOId).
                orElseThrow(
                        () -> new ResourceNotFoundException("category ", "Id ", categoryDTOId));
        CategoryDTO deletedCategoryDTO = modelMapper.map(category, CategoryDTO.class);
        categoryRepository.delete(category);

        return deletedCategoryDTO;
    }


}
