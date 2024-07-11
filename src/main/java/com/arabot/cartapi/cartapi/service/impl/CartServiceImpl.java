package com.arabot.cartapi.cartapi.service.impl;

import com.arabot.cartapi.cartapi.dto.MessageDTO;
import com.arabot.cartapi.cartapi.dto.ProductDTO;
import com.arabot.cartapi.cartapi.dto.ResumeProduct;
import com.arabot.cartapi.cartapi.model.Cart;
import com.arabot.cartapi.cartapi.model.Product;
import com.arabot.cartapi.cartapi.repository.CartRepository;
import com.arabot.cartapi.cartapi.repository.ProductRepository;
import com.arabot.cartapi.cartapi.service.CartService;
import org.arabot.provider.utils.UserUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);
    private final ProductRepository productRepository;

    private final CartRepository cartRepository;

    private final AmqpTemplate amqpTemplate;

    private final UserUtils userUtils;

    private final ModelMapper mapper;


    @Value("${rabbitmq.exchange}")
    String exchange;

    @Value("${rabbitmq.routingkey}")
    private String routingkey;


    @Autowired
    public CartServiceImpl(ProductRepository productRepository, CartRepository cartRepository,
                           AmqpTemplate amqpTemplate,UserUtils userUtils, ModelMapper mapper) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.amqpTemplate = amqpTemplate;
        this.userUtils = userUtils;
        this.mapper = mapper;

    }


    @Override
    public boolean addProductToCart(UUID productId) {

        Product product = productRepository.findById(productId).orElseThrow();
        log.info(product.toString());
        ProductDTO productDTO = mapper.map(product, ProductDTO.class);

        String actualUser =  userUtils.getAutenticatedUserName();

        if (!cartRepository.existsById(actualUser)) {

            createInitialCart(actualUser, productDTO);

        } else {

            addProductToCar(actualUser, productDTO);
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

        String actualUser =  userUtils.getAutenticatedUserName();

        Optional<Cart> car = cartRepository.findById(actualUser);

        List<ResumeProduct> products = car.orElseThrow().getProducts();

        ArrayList<Product> productsToUpdate = new ArrayList<>();

        for (ResumeProduct actualProduct : products) {

            productRepository.findById(actualProduct.getProductId())
                    .ifPresentOrElse(
                            product -> {
                                if (product.getStock() >= actualProduct.getQuantity()) {
                                    product.setStock(product.getStock() - actualProduct.getQuantity());
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
                publishMessage(buildNewMessageDTO(products, actualUser));


            } catch (Exception e) {
                log.info(e.getMessage());
                return false;

            }

        }

        return true;
    }

    @Override
    public List<Product> productsInCart(List<UUID> productsId) {

        return productRepository.findAllById(productsId);
    }

    private void createInitialCart(String email, ProductDTO productDTO) {

        Cart cart = new Cart();
        cart.setId(email);
        cart.setProducts(new ArrayList<>());

        ResumeProduct resumeProduct = buildNewResumeProduct(productDTO);

        cart.getProducts().add(resumeProduct);
        cart.setTotalPrice(resumeProduct.getUnitePrice());

        cartRepository.save(cart);

    }

    private void addProductToCar(String email, ProductDTO productDTO) {

        Cart cart = cartRepository.findById(email).orElseThrow();

        Optional<ResumeProduct> productInCart = cart.getProducts().stream()
                .filter(p -> p.getProductId().equals(productDTO.getId())).findFirst();

        if(productInCart.isPresent()) {

            productInCart.get().increaseQuantity();
            productInCart.get().calculateTotalPrice(); //the orden of this methods are important

        } else {

            cart.getProducts().add(buildNewResumeProduct(productDTO));
        }

        cart.setTotalPrice(calculateTotalCartPrice(cart));

        cartRepository.save(cart);

    }

    private ResumeProduct buildNewResumeProduct(ProductDTO productDTO) {

        return ResumeProduct.builder().productId(productDTO.getId())
                .name(productDTO.getName()).quantity(1).unitePrice(productDTO.getPrice()).totalPrice(productDTO.getPrice()).build();

    }

    private MessageDTO buildNewMessageDTO(List<ResumeProduct> resumeProductList, String actualUser) {

        return MessageDTO.builder().productList(resumeProductList)
                .totalAmount(calculateTotalCartPrice(resumeProductList))
                .userEmail(actualUser)
                .build();
    }

    public void publishMessage(MessageDTO messageDTO){

        amqpTemplate.convertAndSend(exchange,routingkey,messageDTO);
    }

    private double calculateTotalCartPrice(Cart cart) {

        return cart.getProducts().stream().mapToDouble(ResumeProduct::getTotalPrice).sum();
    }

    private double calculateTotalCartPrice(List<ResumeProduct> resumeProductList) {

        return resumeProductList.stream().mapToDouble(ResumeProduct::getTotalPrice).sum();
    }

}