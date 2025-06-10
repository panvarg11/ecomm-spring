package com.mpantoja.sbecommerce.service;


import com.mpantoja.sbecommerce.model.Category;
import com.mpantoja.sbecommerce.payload.CategoryDTO;
import com.mpantoja.sbecommerce.payload.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse getAllCategories();

    CategoryDTO addCategory(CategoryDTO categoryDTO);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long CategoryDtoId);

    CategoryDTO deleteCategory(Long CategoryDtoId);
}
