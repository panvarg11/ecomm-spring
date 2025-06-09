package com.mpantoja.sbecommerce.service;


import com.mpantoja.sbecommerce.model.Category;
import java.util.List;

public interface CategoryService{

     List<Category> getAllCategories();
     void addCategory(Category category);
    String deleteCategory(Long categoryId);

    Category updateCategory(Category category, Long categoryId);
}
