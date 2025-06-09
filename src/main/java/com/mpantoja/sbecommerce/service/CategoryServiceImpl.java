package com.mpantoja.sbecommerce.service;

import com.mpantoja.sbecommerce.exceptions.APIException;
import com.mpantoja.sbecommerce.exceptions.ResourceNotFoundException;
import com.mpantoja.sbecommerce.model.Category;
import com.mpantoja.sbecommerce.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class CategoryServiceImpl implements CategoryService{



    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()){
            throw new APIException("No Categories Found");
        }
        return categories;
    }

    @Override
    public void addCategory(Category category) {
        Category savedCategory= categoryRepository.findByCategoryName(category.getCategoryName());
        if(savedCategory!=null){
            throw new APIException("Category with name \""+category.getCategoryName()+"\" Already exists");
        }
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId).
                orElseThrow(
                        ()->new ResourceNotFoundException("category ", "Id ",categoryId));
//

        categoryRepository.delete(category);git s
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
