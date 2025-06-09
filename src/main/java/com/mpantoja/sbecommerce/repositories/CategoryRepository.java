package com.mpantoja.sbecommerce.repositories;

import com.mpantoja.sbecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {


    Category findByCategoryName(String categoryName);
}
