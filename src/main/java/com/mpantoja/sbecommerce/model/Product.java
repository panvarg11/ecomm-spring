package com.mpantoja.sbecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;

    @NotBlank(message = "Category Name must not be blank")
    @Size(min = 3, message = "Product Name must Contain at least 3 Characters.")
    private String productName;
    private String image;
    private String description="";
    @PositiveOrZero(message = "must be 0 or above")
    private Integer quantity=0;

    @NotNull(message = "Price is mandatory")
    @PositiveOrZero(message = "must be 0 or above")
    private Double price;
    @PositiveOrZero(message = "must be 0 or above")
    private Double discount=0D;
    private Double finalPrice;


    @ManyToOne
    @JoinColumn(name = "Category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User user;





}
