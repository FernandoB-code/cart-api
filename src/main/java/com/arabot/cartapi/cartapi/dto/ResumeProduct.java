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
    private double price;
    private double totalPrice;

    public void increaseQuantity() {
        this.quantity ++;
    }

    public void calculateTotalPrice() {

        this.totalPrice = this.price * this.quantity; }

}
