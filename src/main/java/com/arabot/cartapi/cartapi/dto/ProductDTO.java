package com.arabot.cartapi.cartapi.dto;

import com.arabot.cartapi.cartapi.enums.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class ProductDTO {

    private UUID id;
    @NotBlank
    private String name;
    @NotNull
    private double price;
    @NotNull
    private int stock;
    @NotBlank
    private String brand;
    @NotNull
    private ProductCategory productCategory;
    @NotNull
    private Set<ProductAttribute> productAttributes;

}
