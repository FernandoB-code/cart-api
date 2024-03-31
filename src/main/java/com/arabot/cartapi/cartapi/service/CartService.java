package com.arabot.cartapi.cartapi.service;

import com.arabot.cartapi.cartapi.model.Product;

import java.util.List;
import java.util.UUID;

public interface CartService {
    boolean addProductToCart(UUID productId);

    boolean removeProductFromCart(UUID productId);

    boolean purchaseProdcutsInCart();



     List<Product> productsInCart(List<UUID> productsId);
}
