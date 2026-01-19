package com.example.ecommerce.service;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public Order createOrder(String userId) {

        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double total = 0;

        for (CartItem cart : cartItems) {
            Product p = productRepository.findById(cart.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product missing"));
            if (p.getStock() < cart.getQuantity()) {
                throw new RuntimeException("Out of stock");
            }
            total += p.getPrice() * cart.getQuantity();
        }

        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setStatus("CREATED");
        order.setCreatedAt(Instant.now());

        orderRepository.save(order);

        for (CartItem cart : cartItems) {
            Product p = productRepository.findById(cart.getProductId()).get();

            OrderItem item = new OrderItem();
            item.setId(UUID.randomUUID().toString());
            item.setOrderId(order.getId());
            item.setProductId(p.getId());
            item.setQuantity(cart.getQuantity());
            item.setPrice(p.getPrice());
            orderItemRepository.save(item);

            p.setStock(p.getStock() - cart.getQuantity());
            productRepository.save(p);
        }

        cartRepository.deleteByUserId(userId);
        return order;
    }

    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public void updateStatus(String orderId, String status) {
        Order order = getOrder(orderId);
        order.setStatus(status);
        orderRepository.save(order);
    }
}
