package com.mpantoja.sbecommerce.payload;

import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long productId;

    @NotBlank(message = "Product Name must not be blank")
    @Size(min=3, message = "Product Name must Contain at least 3 Characters.")
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
    private CategoryDTO category;

}
