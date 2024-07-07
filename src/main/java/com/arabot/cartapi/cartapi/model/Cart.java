package com.arabot.cartapi.cartapi.model;

import com.arabot.cartapi.cartapi.dto.ProductDTO;
import com.arabot.cartapi.cartapi.dto.ResumeProduct;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
public class Cart {

    @Id
    String id;

    List<ResumeProduct> products;

    double totalPrice;

}
