package com.mpantoja.sbecommerce.service;

import com.mpantoja.sbecommerce.model.Product;
import com.mpantoja.sbecommerce.payload.ProductDTO;
import com.mpantoja.sbecommerce.payload.ProductResponse;
import org.springframework.http.ResponseEntity;

public interface ProductService {


    ProductDTO createProduct(Long categoryId, Product product);

    ProductResponse getAllProducts();

    ProductResponse searchByCategory(Long categoryId);

    ProductResponse searchProductByKeyword(String keyword);

    ProductDTO deleteProductById(Long productId);

    ProductDTO updateProductById(Long productId);
}
