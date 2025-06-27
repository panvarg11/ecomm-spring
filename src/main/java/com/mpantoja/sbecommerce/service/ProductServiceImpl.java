package com.mpantoja.sbecommerce.service;

import com.mpantoja.sbecommerce.Utils.PaginationUtil;
import com.mpantoja.sbecommerce.exceptions.APIException;
import com.mpantoja.sbecommerce.exceptions.ResourceNotFoundException;
import com.mpantoja.sbecommerce.model.Category;
import com.mpantoja.sbecommerce.model.Product;
import com.mpantoja.sbecommerce.payload.ProductDTO;
import com.mpantoja.sbecommerce.payload.ProductResponse;
import com.mpantoja.sbecommerce.repositories.CategoryRepository;
import com.mpantoja.sbecommerce.repositories.ProductRepository;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.DoubleBinaryOperator;

@Service
public class ProductServiceImpl implements ProductService {

    private static final String ENTITY_NAME = "Product";


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO createProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        if (productRepository.findByProductNameAndCategory_CategoryId(productDTO.getProductName(), categoryId).isPresent()) {
            throw new APIException("A product with name " + productDTO.getProductName() + " already exists in this category");
        }

        Product product = modelMapper.map(productDTO, Product.class);
        product.setCategory(category);
        product.setImage("default.png");
        product.setFinalPrice(
                calculateSpecialPrice.applyAsDouble(productDTO.getPrice(), productDTO.getDiscount()));

        return modelMapper.map(productRepository.save(product), ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Pageable pageDetails = PaginationUtil.buildPageRequest(pageNumber, pageSize, sortBy, sortOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);

        List<ProductDTO> foundProducts = productPage.getContent().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        if (foundProducts.isEmpty()) throw new APIException("No products found");

        return new ProductResponse(foundProducts,
                productPage.getNumber(), productPage.getSize(), productPage.getTotalElements(),
                productPage.getTotalPages(), productPage.isLast());
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {


        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        Pageable pageDetails = PaginationUtil.buildPageRequest(pageNumber, pageSize, sortBy, sortOrder);
        Page<Product> productPage = productRepository.findByCategory(category, pageDetails);

        List<ProductDTO> foundProducts = productPage.getContent().stream().
                map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        if (foundProducts.isEmpty()) throw new APIException("Category " + category.getCategoryName() + " Is Empty");

        return new ProductResponse(foundProducts,
                productPage.getNumber(), productPage.getSize(), productPage.getTotalElements(),
                productPage.getTotalPages(), productPage.isLast());
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageDetails = PaginationUtil.buildPageRequest(pageNumber, pageSize, sortBy, sortOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);

        List<ProductDTO> foundProducts = productPage.getContent().stream().
                map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        if (foundProducts.isEmpty()) throw new ResourceNotFoundException(ENTITY_NAME, "Keyword", keyword);

        return new ProductResponse(foundProducts,
                productPage.getNumber(), productPage.getSize(), productPage.getTotalElements(),
                productPage.getTotalPages(), productPage.isLast());
    }

    @Override
    public ProductDTO updateProductById(Long productId, ProductDTO productDTO) {
        Product productInDataBase = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException(ENTITY_NAME, "Product Id", productId));
        //
        Optional<Product> existing = productRepository.
                findByProductNameAndCategory_CategoryId(productDTO.getProductName(), productInDataBase
                        .getCategory().getCategoryId());
        if (existing.isPresent() && !existing.get().getProductId().equals(productId)) {
            throw new APIException(
                    "A product with name \"" + productDTO.getProductName() + "\" already exists in this category");
        }

        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(productDTO, productInDataBase);

        productInDataBase.setFinalPrice(
                calculateSpecialPrice.applyAsDouble(productInDataBase.getPrice(), productInDataBase.getDiscount()));

        return modelMapper.map(productRepository.save(productInDataBase), ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product productFromDataBase = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException(ENTITY_NAME, "Product ID", productId));

        String fileName = fileService.uploadImage(path, image);
        productFromDataBase.setImage(fileName);
        return modelMapper.map(productRepository.save(productFromDataBase), ProductDTO.class);
    }


    @Override
    public ProductDTO deleteProductById(Long productId) {
        Product productInDataBase = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException(ENTITY_NAME, "Product Id", productId));
        productRepository.delete(productInDataBase);
        return modelMapper.map(productInDataBase, ProductDTO.class);
    }

    DoubleBinaryOperator calculateSpecialPrice = (price, discount) -> price - (price * (discount * 0.01));

}
