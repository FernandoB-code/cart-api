package com.arabot.cartapi.cartapi.model;


import com.arabot.cartapi.cartapi.dto.ProductAttribute;
import com.arabot.cartapi.cartapi.dto.ProductCategory;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.UUID;

@Document
@Data
public class Product {

    @Id
    private UUID id;
    private String name;
    private double price;
    private int stock;
    private String brand;
    private ProductCategory productCategory;
    private Set<ProductAttribute> productAttributes;

}
