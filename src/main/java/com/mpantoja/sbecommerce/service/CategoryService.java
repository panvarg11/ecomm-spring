package com.mpantoja.sbecommerce.service;


import com.mpantoja.sbecommerce.model.Category;
import com.mpantoja.sbecommerce.payload.CategoryResponse;

import java.util.List;

public interface CategoryService{

     CategoryResponse getAllCategories();
     void addCategory(Category category);
    String deleteCategory(Long categoryId);

    Category updateCategory(Category category, Long categoryId);
}
