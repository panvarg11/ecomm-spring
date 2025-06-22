package com.mpantoja.sbecommerce.repositories;

import com.mpantoja.sbecommerce.model.Category;
import com.mpantoja.sbecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryOrderByPriceAsc(Category category);

    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageable);

    Optional<Product> findByProductNameAndCategory_CategoryId(String productName, Long categoryId);

    Page<Product> findByCategory(Category category, Pageable pageable);
}
