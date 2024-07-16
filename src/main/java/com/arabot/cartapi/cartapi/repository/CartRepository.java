package com.arabot.cartapi.cartapi.repository;

import com.arabot.cartapi.cartapi.model.Cart;
import com.arabot.cartapi.cartapi.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {

    @Query("{ 'groupId': ?0 }")
    Cart findAllByGroupId(String id);

}
