package com.arabot.cartapi.cartapi.dto;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ResumeProduct {

    private UUID productId;
    private String name;
    private int quantity;
    private double totalPrice;

    public void increaseQuantity() {
        this.quantity ++;
    }

    public void increaseTotalPrice() { this.totalPrice += this.totalPrice; }

}
