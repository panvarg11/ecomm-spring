package com.mpantoja.sbecommerce.repositories;

import com.mpantoja.sbecommerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository <Cart, Long>{
}
