package com.example.ecommerce.service;

import com.example.ecommerce.dto.AddToCartRequest;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;

    public CartItem addToCart(AddToCartRequest req) {
        Product product = productService.getById(req.getProductId());

        if (product.getStock() < req.getQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        CartItem item = new CartItem();
        item.setId(UUID.randomUUID().toString());
        item.setUserId(req.getUserId());
        item.setProductId(req.getProductId());
        item.setQuantity(req.getQuantity());

        return cartRepository.save(item);
    }

    public List<CartItem> getUserCart(String userId) {
        return cartRepository.findByUserId(userId);
    }

    public void clearCart(String userId) {
        cartRepository.deleteByUserId(userId);
    }
}
