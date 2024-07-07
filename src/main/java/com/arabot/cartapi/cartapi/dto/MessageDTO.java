package com.arabot.cartapi.cartapi.dto;

import com.arabot.cartapi.cartapi.model.Product;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MessageDTO {

    private List<ResumeProduct> productList;

    private double totalAmount;

    private String userEmail;

}
