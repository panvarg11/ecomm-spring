package com.mpantoja.sbecommerce.controller;

import com.mpantoja.sbecommerce.config.AppConstants;
import com.mpantoja.sbecommerce.payload.ProductDTO;
import com.mpantoja.sbecommerce.payload.ProductResponse;
import com.mpantoja.sbecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/admin/category/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid  @RequestBody ProductDTO productDTO,
                                                 @PathVariable Long categoryId) {
        return new ResponseEntity<>(productService.createProduct(categoryId, productDTO), HttpStatus.CREATED);
    }

    @GetMapping("/public/products/all")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ) {
        return new ResponseEntity<>(productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    @GetMapping("/public/category/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        return new ResponseEntity<>(productService.searchByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductByKeyword(
            @PathVariable String keyword,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        return new ResponseEntity<>(productService.searchProductByKeyword(keyword, pageNumber, pageSize, sortBy,
                sortOrder), HttpStatus.FOUND);
    }

    @PutMapping("/admin/product/{productId}/update")
    public ResponseEntity<ProductDTO> updateProductById(@Valid @PathVariable Long productId,
                                                        @RequestBody ProductDTO productDTO) {
        return new ResponseEntity<>(productService.updateProductById(productId, productDTO), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/admin/product/{productId}/delete")
    public ResponseEntity<ProductDTO> deleteProductById(@PathVariable Long productId) {
        return new ResponseEntity<>(productService.deleteProductById(productId), HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam("image") MultipartFile image)
            throws IOException {
        return new ResponseEntity<>(productService.updateProductImage(productId, image), HttpStatus.OK);

    }


}
