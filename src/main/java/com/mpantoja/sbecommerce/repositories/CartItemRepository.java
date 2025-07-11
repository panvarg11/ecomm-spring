package com.mpantoja.sbecommerce.repositories;

import com.mpantoja.sbecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
