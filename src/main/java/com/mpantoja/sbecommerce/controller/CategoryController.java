package com.mpantoja.sbecommerce.controller;

import com.mpantoja.sbecommerce.config.AppConstants;
import com.mpantoja.sbecommerce.payload.CategoryDTO;
import com.mpantoja.sbecommerce.payload.CategoryResponse;
import com.mpantoja.sbecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @PostMapping("/admin/category")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO storedCategory = categoryService.addCategory(categoryDTO);

        return new ResponseEntity<>(storedCategory, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryDTOId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryDTOId) {
        CategoryDTO status = categoryService.deleteCategory(categoryDTOId);
        return new ResponseEntity<>(status, HttpStatus.OK);

    }

    @PutMapping("/admin/category/{categoryDTOId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                      @PathVariable Long categoryDTOId) {
        CategoryDTO savedCategoryDTO = categoryService.updateCategory(categoryDTO, categoryDTOId);

        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.OK);
    }

}
