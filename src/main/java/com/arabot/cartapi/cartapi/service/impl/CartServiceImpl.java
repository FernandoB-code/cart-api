package com.arabot.cartapi.cartapi.service.impl;

import com.arabot.cartapi.cartapi.dto.ResumeProduct;
import com.arabot.cartapi.cartapi.model.Cart;
import com.arabot.cartapi.cartapi.model.Product;
import com.arabot.cartapi.cartapi.repository.CartRepository;
import com.arabot.cartapi.cartapi.repository.ProductRepository;
import com.arabot.cartapi.cartapi.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;

    private final CartRepository cartRepository;

    private final AmqpTemplate amqpTemplate;


    @Value("${rabbitmq.exchange}")
    String exchange;

    @Value("${rabbitmq.routingkey}")
    private String routingkey;


    @Autowired
    public CartServiceImpl(ProductRepository productRepository, CartRepository cartRepository,
                           AmqpTemplate amqpTemplate) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.amqpTemplate = amqpTemplate;

    }


    @Override
    public boolean addProductToCart(UUID productId) {

        Optional<Product> product = productRepository.findById(productId);
        String email = "test@gmail.com";

        if (!cartRepository.existsById(email)) {

            createInitialCart(email, productId);

        } else {

            addProductToCar(email, productId);
        }

        return true;
    }

    @Override
    public boolean removeProductFromCart(UUID productId) {
        return false;
    }

    @Override
    @Transactional
    public boolean purchaseProductsInCart() {

        String email = "test@gmail.com";

        Optional<Cart> car = cartRepository.findById(email);

        List<ResumeProduct> products = car.orElseThrow().getProducts();

        ArrayList<Product> productsToUpdate = new ArrayList<>();


        for (ResumeProduct actualProduct : products) {

            productRepository.findById(actualProduct.getProductId())
                    .ifPresentOrElse(
                            product -> {
                                if (product.getStock() >= actualProduct.getCant()) {
                                    product.setStock(product.getStock() - actualProduct.getCant());
                                    productsToUpdate.add(product);

                                } else {

                                    throw new RuntimeException("No stock for product: " + product.getId());
                                }
                            },

                            () -> {

                                throw new RuntimeException("Product with ID: " + actualProduct.getProductId() + " not found");
                            }
                    );

            try {

                productRepository.saveAll(productsToUpdate);
                publishMessage(productsToUpdate);


            } catch (Exception e) {

                return false;

            }

        }

        return true;
    }

    @Override
    public List<Product> productsInCart(List<UUID> productsId) {

        return productRepository.findAllById(productsId);
    }

    private void createInitialCart(String email, UUID productId) {

        Cart cart = new Cart();
        cart.setId(email);
        cart.setProducts(new ArrayList<>());

        cart.getProducts().add(buildNewResumeProduct(productId));

        cartRepository.save(cart);

    }

    private void addProductToCar(String email, UUID productId) {

        Cart cart = cartRepository.findById(email).orElseThrow();
        Optional<ResumeProduct> resumeProduct = cart.getProducts().stream().filter(p -> p.getProductId().equals(productId)).findFirst();

        resumeProduct.ifPresentOrElse(
                ResumeProduct::increaseCant, () -> cart.getProducts().add(buildNewResumeProduct(productId)));

        cartRepository.save(cart);


    }

    private ResumeProduct buildNewResumeProduct(UUID productId) {

        return ResumeProduct.builder().productId(productId).cant(1).build();

    }

    public void publishMessage(List<Product> products){

        amqpTemplate.convertAndSend(exchange,routingkey,products);
    }

}
