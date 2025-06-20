package com.mpantoja.sbecommerce.service;

import com.mpantoja.sbecommerce.exceptions.ResourceNotFoundException;
import com.mpantoja.sbecommerce.model.Category;
import com.mpantoja.sbecommerce.model.Product;
import com.mpantoja.sbecommerce.payload.ProductDTO;
import com.mpantoja.sbecommerce.payload.ProductResponse;
import com.mpantoja.sbecommerce.repositories.CategoryRepository;
import com.mpantoja.sbecommerce.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductDTO createProduct(Long categoryId, Product product) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category", "CategoryId", categoryId));
        product.setCategory(category);
        product.setImage("default.png");
        double specialPrice= product.getPrice()-
                (product.getPrice()*(product.getDiscount()*0.01));
        product.setSpecialPrice(specialPrice);

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {
        return new ProductResponse(productRepository.findAll().stream()
                .map(product->modelMapper.map(product, ProductDTO.class)).toList());
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category", "CategoryId",categoryId));

        return new ProductResponse(productRepository.findByCategoryOrderByPriceAsc(category).stream()
                .map(product -> modelMapper.map(product,ProductDTO.class)).toList());
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword) {

        return new ProductResponse(productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%').stream()
                .map(product -> modelMapper.map(product, ProductDTO.class)).toList());
    }



    @Override
    public ProductDTO deleteProductById(Long productId) {
        Product productInDataBase =  productRepository.findById(productId).orElseThrow(
                ()->new ResourceNotFoundException("Product", "Product Id", productId));
        productRepository.delete(productInDataBase);
        return modelMapper.map(productInDataBase, ProductDTO.class);
    }


}
