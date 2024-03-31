package com.arabot.cartapi.cartapi.controller;

import com.arabot.cartapi.cartapi.dto.ProductDTO;
import com.arabot.cartapi.cartapi.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping("/add")
    public ResponseEntity<?> addProductToCart(@RequestParam("productId") UUID productId) {

        String token = request.getHeader("Authorization");
        token = token.replace("Bearer ", "");

        return new ResponseEntity<>(cartService.addProductToCart(productId), HttpStatus.OK);

    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeProductToCart(@RequestParam("productId") UUID productId) {

        return new ResponseEntity<>(cartService.removeProductFromCart(productId), HttpStatus.OK);

    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseProductsInCart() {

        return new ResponseEntity<>(cartService.purchaseProdcutsInCart(), HttpStatus.OK);

    }
}
