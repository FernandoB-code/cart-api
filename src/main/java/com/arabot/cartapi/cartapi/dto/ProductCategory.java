package com.arabot.cartapi.cartapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class ProductCategory {

    @NotBlank
    private String category;

    @NotBlank
    private String subCategory;

}
