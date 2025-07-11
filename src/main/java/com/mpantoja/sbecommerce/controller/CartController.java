package com.mpantoja.sbecommerce.controller;

import com.mpantoja.sbecommerce.payload.CartDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CartController {

    @PostMapping("/cart/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable  Long productId,
                                                    @PathVariable Integer quantity){

        CartDTO cartDTO = cartService.addProductToCart();
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);

    }

}
