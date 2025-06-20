package com.mpantoja.sbecommerce.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long categoryId;

    @NotBlank(message = "Category Name must not be blank")
    @Size(min=5, message = "Category Name must Contain at least 5 Characters.")
    private String categoryName;


}
