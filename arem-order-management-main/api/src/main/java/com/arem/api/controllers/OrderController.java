package com.arem.api.controllers;

import com.arem.core.model.Order;
import com.arem.dataservice.services.IOrderService;
import com.arem.dataservice.services.CartService;
import com.arem.dataservice.services.ICustomerService;
import com.arem.core.model.Cart;
import com.arem.core.model.CartItem;
import com.arem.core.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.arem.core.auth.CustomerDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final IOrderService orderService;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private ICustomerService customerService;

    @Autowired
    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestParam long customerId) {
        Order order = orderService.createOrder(customerId);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<Order> addItemToOrder(
            @PathVariable long orderId,
            @RequestParam long productId,
            @RequestParam double quantity) {
        Order order = orderService.addItemToOrder(orderId, productId, quantity);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<Order> removeItemFromOrder(
            @PathVariable long orderId,
            @PathVariable long itemId) {
        Order order = orderService.removeItemFromOrder(orderId, itemId);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<Order> updateOrderItem(
            @PathVariable long orderId,
            @PathVariable long itemId,
            @RequestBody Map<String, Double> body) {
        Double quantity = body.get("quantity");
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        Order order = orderService.updateOrderItem(orderId, itemId, quantity.doubleValue());
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable long orderId) {
        Order order = orderService.getOrder(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getCustomerOrders(@PathVariable long customerId) {
        List<Order> orders = orderService.getCustomerOrders(customerId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable long orderId,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        Order order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderId}/total")
    public ResponseEntity<Double> getOrderTotal(@PathVariable long orderId) {
        double total = orderService.calculateOrderTotal(orderId);
        return ResponseEntity.ok(total);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.findAll();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getMyOrders(Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Object principal = authentication.getPrincipal();
            Long customerId = null;
            if (principal instanceof CustomerDetails) { // Handles CustomerDetails
                CustomerDetails customerDetails = (CustomerDetails) principal;
                customerId = customerDetails.getCustomer().getId();
            } else if (principal instanceof org.springframework.security.core.userdetails.User) {
                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) principal;
                Customer customer = customerService.getCustomerByEmail(user.getUsername());
                if (customer != null) {
                    customerId = customer.getId();
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if (customerId != null) {
                List<Order> orders = orderService.getCustomerOrders(customerId);
                return ResponseEntity.ok(orders);
            } else {
                return ResponseEntity.ok(new ArrayList<>());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Endpoint pour transformer le panier en commande (checkout simplifié)
     */
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(Authentication authentication) {
        try {
            logger.info("Starting checkout process");
            
            if (authentication == null) {
                logger.error("Authentication is null");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Non authentifié"));
            }
            
            // Récupérer le customer
            Customer customer = null;
            if (authentication.getPrincipal() instanceof CustomerDetails) {
                CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();
                customer = customerDetails.getCustomer();
                logger.info("Customer found: {}", customer.getEmail());
            } else if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
                customer = customerService.getCustomerByEmail(user.getUsername());
                logger.info("Customer found via email: {}", user.getUsername());
            }
            
            if (customer == null) {
                logger.error("Customer not found");
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Client non trouvé"));
            }
            
            // Récupérer le panier actif
            Cart cart = cartService.getOrCreateActiveCart(customer.getId());
            logger.info("Cart found with {} items", cart.getItems().size());
            
            if (cart.isEmpty()) {
                logger.warn("Cart is empty for customer {}", customer.getId());
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Le panier est vide"));
            }
            
            // Créer la commande
            Order order = orderService.createOrder(customer.getId());
            logger.info("Order created with ID: {}", order.getId());
            
            // Ajouter les items du panier à la commande
            for (CartItem cartItem : cart.getItems()) {
                if (cartItem.getProduct() != null) {
                    orderService.addItemToOrder(
                        order.getId(), 
                        cartItem.getProduct().getId(), 
                        (double) cartItem.getQuantity()  // Conversion int → double
                    );
                    logger.info("Added product {} (qty: {}) to order", 
                        cartItem.getProduct().getName(), cartItem.getQuantity());
                }
            }
            
            // Vider le panier après création de la commande
            cartService.clearCart(customer.getId());
            logger.info("Cart cleared for customer {}", customer.getId());
            
            // Récupérer la commande complète
            Order finalOrder = orderService.getOrder(order.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Commande créée avec succès",
                "orderId", finalOrder.getId(),
                "orderStatus", finalOrder.getStatus().toString(),
                "totalAmount", finalOrder.getTotalAmount()
            ));
            
        } catch (Exception e) {
            logger.error("Error during checkout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur lors de la création de la commande: " + e.getMessage()));
        }
    }
} 