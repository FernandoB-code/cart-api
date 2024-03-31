package com.arabot.cartapi.cartapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
public class ProductAttribute {

    @NotBlank
    private String attributeName;
    @NotBlank
    private String attributeDescription;

}
