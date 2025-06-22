package com.mpantoja.sbecommerce.service;

import com.mpantoja.sbecommerce.payload.ProductDTO;
import com.mpantoja.sbecommerce.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {


    ProductDTO createProduct(Long categoryId, ProductDTO productDTO);

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductDTO updateProductById(Long productId,ProductDTO productDTO) ;

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

    ProductDTO deleteProductById(Long productId);
}
