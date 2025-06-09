package com.mpantoja.sbecommerce.service;

import com.mpantoja.sbecommerce.exceptions.APIException;
import com.mpantoja.sbecommerce.exceptions.ResourceNotFoundException;
import com.mpantoja.sbecommerce.model.Category;
import com.mpantoja.sbecommerce.payload.CategoryDTO;
import com.mpantoja.sbecommerce.payload.CategoryResponse;
import com.mpantoja.sbecommerce.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService{



    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()){
            throw new APIException("No Categories Found");
        }

        List<CategoryDTO> categoryDTOS= categories.stream().map(
                category -> modelMapper.map(category, CategoryDTO.class)).toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        return categoryResponse;
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {

        Category receivedCategory = modelMapper.map(categoryDTO, Category.class);

        Category categoryInDataBase= categoryRepository.findByCategoryName(receivedCategory.
                getCategoryName());
        if(categoryInDataBase!=null){
            throw new APIException("Category with name \""+categoryDTO.getCategoryName()+"\" Already exists");
        }
        Category savedCategory = categoryRepository.save(receivedCategory);

        return modelMapper.map(savedCategory, CategoryDTO.class);
    }


    @Override
    public String deleteCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId).
                orElseThrow(
                        ()->new ResourceNotFoundException("category ", "Id ",categoryId));
//

        categoryRepository.delete(category);
        return "category with ID "+categoryId+ " has been removed successfully";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {


        Category savedCategory= categoryRepository.findById(categoryId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category", "ID", categoryId));


        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return savedCategory;

    }


}
