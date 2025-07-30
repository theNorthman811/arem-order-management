package com.arem.dataservice.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arem.core.model.Customer;
import com.arem.core.model.Order;
import com.arem.core.model.OrderItem;
import com.arem.core.model.Product;
import com.arem.core.model.Status;
import com.arem.core.model.Measure;
import com.arem.core.model.ProductNotFoundException;
import com.arem.core.model.InsufficientStockException;
import com.arem.dataservice.repositories.IOrderRepository;
import com.arem.dataservice.repositories.IProductRepository;
import com.arem.dataservice.services.ICustomerService;
import com.arem.dataservice.services.IProductService;
import com.arem.dataservice.services.IPriceService;

@Service
public class OrderService implements IOrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    @Autowired
    private IOrderRepository orderRepository;
    
    @Autowired
    private ICustomerService customerService;
    
    @Autowired
    private IProductService productService;
    
    @Autowired
    private IPriceService priceService;
    
    @Autowired
    private IProductRepository productRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }
    
    @Override
    public Order getOrder(long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }
    
    @Override
    public List<Order> getCustomerOrders(long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    @Override
    public Order createOrder(long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        if (customer == null) {
            throw new RuntimeException("Customer not found with ID: " + customerId);
        }
        
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setStatus(Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        
        return orderRepository.save(order);
    }

    @Override
    public Order addItemToOrder(long orderId, long productId, double quantity) {
        Order order = getOrder(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }
        
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
        
        try {
            // Utiliser le prix du produit directement
            double currentPrice = product.getPrice();
            if (currentPrice <= 0) {
                throw new RuntimeException("Invalid price (≤ 0) found for product " + productId);
            }
            
            order.addItem(product, quantity, product.getMeasure(), currentPrice);
            order.setLastModifiedDate(LocalDateTime.now());
            
            return orderRepository.update(order);
            
        } catch (Exception e) {
            logger.error("Error adding item to order: {}", e.getMessage());
            throw new RuntimeException("Failed to add item to order: " + e.getMessage());
        }
    }

    @Override
    public Order removeItemFromOrder(long orderId, long itemId) {
        Order order = getOrder(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }
        
        order.removeItem(itemId);
        order.setLastModifiedDate(LocalDateTime.now());
        
        return orderRepository.update(order);
    }

    @Override
    public Order updateOrderItem(long orderId, long itemId, double quantity) {
        Order order = getOrder(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }
        
        // Find the item in the order
        OrderItem item = order.getItems().stream()
                .filter(i -> i.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + itemId));
        
        // Update quantity
        item.setQuantity(quantity);
        
        // Force recalculation of order total
        order.setItems(order.getItems()); // This triggers calculateTotalAmount()
        order.setLastModifiedDate(LocalDateTime.now());
        
        return orderRepository.update(order);
    }

    @Override
    public Order updateOrderStatus(long orderId, String status) {
        Order order = getOrder(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }
        
        try {
            Status newStatus = Status.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
            order.setLastModifiedDate(LocalDateTime.now());
            return orderRepository.update(order);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }

    @Override
    public void deleteOrder(long orderId) {
        Order order = getOrder(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }
        orderRepository.delete(orderId);
    }

    @Override
    public double calculateOrderTotal(long orderId) {
        Order order = getOrder(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }
        
        double total = 0;
        for (OrderItem item : order.getItems()) {
            total += item.getSubtotal();
        }
        return total;
    }
} 