package com.mpantoja.sbecommerce.controller;

import com.mpantoja.sbecommerce.model.Product;
import com.mpantoja.sbecommerce.payload.ProductDTO;
import com.mpantoja.sbecommerce.payload.ProductResponse;
import com.mpantoja.sbecommerce.service.ProductService;
import com.mpantoja.sbecommerce.service.ProductServiceImpl;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/admin/category/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody Product product,
                                                 @PathVariable Long categoryId ){
        return new ResponseEntity<>(productService.createProduct(categoryId, product), HttpStatus.CREATED);
    }

    @GetMapping("/user/products/all")
    public ResponseEntity<ProductResponse> getAllProducts(){
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/user/category/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId){
        return new ResponseEntity<>(productService.searchByCategory(categoryId), HttpStatus.OK);
    }

    @GetMapping("/user/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductByKeyword(@PathVariable String keyword){
           return new ResponseEntity<>(productService.searchProductByKeyword(keyword),HttpStatus.FOUND);
    }

    @DeleteMapping("/admin/product/{productId}/delete")
    public ResponseEntity<ProductDTO> deleteProductById(@PathVariable Long productId){
        return new ResponseEntity<>(productService.deleteProductById(productId),HttpStatus.OK);
    }

}
