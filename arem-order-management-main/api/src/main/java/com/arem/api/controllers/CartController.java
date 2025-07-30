package com.arem.api.controllers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arem.core.model.Cart;
import com.arem.core.model.CartItem;
import com.arem.core.model.Customer;
import com.arem.core.model.Product;
import com.arem.core.model.Seller;
import com.arem.dataservice.services.CartItemService;
import com.arem.dataservice.services.CartService;
import com.arem.dataservice.services.ICustomerService;
import com.arem.dataservice.services.IProductService;
import com.arem.productInput.contracts.CartContract;
import com.arem.productInput.contracts.CartItemContract;
import com.arem.core.auth.SellerDetails;
import com.arem.core.auth.CustomerDetails;
import com.arem.dataservice.repositories.ICartItemRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private CartItemService cartItemService;
    
    @Autowired
    private ICustomerService customerService;
    
    @Autowired
    private ICartItemRepository cartItemRepository;
    
    @Autowired
    private IProductService productService;
    
    /**
     * Récupère le panier actif de l'utilisateur connecté
     */
    @GetMapping("/my-cart")
    public ResponseEntity<CartContract> getMyCart(Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Customer customer = null;
            
            if (authentication.getPrincipal() instanceof SellerDetails) {
                SellerDetails sellerDetails = (SellerDetails) authentication.getPrincipal();
                Seller seller = sellerDetails.getSeller();
                customer = getOrCreateCustomerForSeller(seller);
            } else if (authentication.getPrincipal() instanceof CustomerDetails) {
                CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();
                customer = customerDetails.getCustomer();
            } else if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
                customer = customerService.getCustomerByEmail(user.getUsername());
            }
            
            if (customer == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Cart cart = cartService.getOrCreateActiveCart(customer.getId());
            return ResponseEntity.ok(new CartContract(cart));
        } catch (Exception e) {
            logger.error("Error getting my cart", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Ajoute un produit au panier de l'utilisateur connecté
     */
    @PostMapping("/add-product")
    public ResponseEntity<CartContract> addProductToMyCart(
            Authentication authentication,
            @RequestParam long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        try {
            logger.info("Adding product {} with quantity {} to cart", productId, quantity);
            
            if (authentication == null) {
                logger.error("Authentication is null");
                return ResponseEntity.badRequest().build();
            }
            
            Customer customer = null;
            
            if (authentication.getPrincipal() instanceof SellerDetails) {
                SellerDetails sellerDetails = (SellerDetails) authentication.getPrincipal();
                Seller seller = sellerDetails.getSeller();
                customer = getOrCreateCustomerForSeller(seller);
                logger.info("Using seller {} as customer", seller.getEmail());
            } else if (authentication.getPrincipal() instanceof CustomerDetails) {
                CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();
                customer = customerDetails.getCustomer();
                logger.info("Using customer {} with ID {}", customer.getEmail(), customer.getId());
            } else if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
                customer = customerService.getCustomerByEmail(user.getUsername());
                logger.info("Using generic user {} as customer", user.getUsername());
            }
            
            if (customer == null) {
                logger.error("Customer is null");
                return ResponseEntity.badRequest().build();
            }
            
            // Vérifier que le produit existe
            Product product = productService.getProductById(productId);
            if (product == null) {
                logger.error("Product with ID {} not found", productId);
                return ResponseEntity.badRequest().build();
            }
            logger.info("Product found: {}", product.getName());
            
            // Récupérer ou créer le panier
            Cart cart = cartService.getOrCreateActiveCart(customer.getId());
            logger.info("Cart retrieved/created with ID {} for customer {}", cart.getId(), customer.getId());
            
            // Vérifier si le produit est déjà dans le panier (sans fetch join pour éviter les problèmes)
            Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
            
            if (existingItem.isPresent()) {
                logger.info("Product already in cart, updating quantity");
                // Mettre à jour la quantité
                CartItem item = existingItem.get();
                int oldQuantity = item.getQuantity();
                item.setQuantity(oldQuantity + quantity);
                cartItemRepository.save(item);
                logger.info("Updated quantity from {} to {}", oldQuantity, item.getQuantity());
            } else {
                logger.info("Adding new product to cart");
                // Créer un nouvel élément SANS modifier la collection du cart
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setProduct(product);
                newItem.setQuantity(quantity);
                
                // Sauvegarder directement - Hibernate s'occupera de la relation
                CartItem savedItem = cartItemRepository.save(newItem);
                logger.info("New cart item saved with ID {}", savedItem.getId());
            }
            
            // Recharger le cart pour avoir la version à jour
            Cart updatedCart = cartService.getOrCreateActiveCart(customer.getId());
            logger.info("Cart reloaded successfully");
            
            return ResponseEntity.ok(new CartContract(updatedCart));
        } catch (Exception e) {
            logger.error("Error adding product to cart", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Méthode utilitaire pour créer ou récupérer un Customer correspondant au Seller
     */
    private Customer getOrCreateCustomerForSeller(Seller seller) {
        try {
            // Essayer de trouver un Customer existant avec le même nom
            Customer existingCustomer = customerService.getCustomerByFirstNameAndLastNameAndPickName(
                seller.getFirstName(), 
                seller.getLastName(), 
                seller.getPickName()
            );
            if (existingCustomer != null) {
                return existingCustomer;
            }
        } catch (Exception e) {
            // Customer n'existe pas, on va le créer
        }
        
        // Créer un nouveau Customer basé sur le Seller
        Customer newCustomer = new Customer();
        newCustomer.setFirstName(seller.getFirstName());
        newCustomer.setLastName(seller.getLastName());
        newCustomer.setPhoneNumber(seller.getPhoneNumber());
        newCustomer.setAddress(seller.getAddress());
        newCustomer.setPickName(seller.getPickName());
        newCustomer.setVersion(1);
        newCustomer.setCreationDate(java.time.LocalDateTime.now());
        newCustomer.setModifDate(java.time.LocalDateTime.now());
        
        try {
            long customerId = customerService.save(newCustomer);
            return customerService.getCustomerById(customerId);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création du customer", e);
        }
    }
    
    /**
     * Récupère le panier actif d'un client (pour admin)
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CartContract> getActiveCart(@PathVariable long customerId) {
        try {
            Cart cart = cartService.getOrCreateActiveCart(customerId);
            return ResponseEntity.ok(new CartContract(cart));
        } catch (Exception e) {
            logger.error("Error getting active cart for customer {}", customerId, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Ajoute un produit au panier (pour admin)
     */
    @PostMapping("/customer/{customerId}/add")
    public ResponseEntity<CartContract> addProductToCart(
            @PathVariable long customerId,
            @RequestParam long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        try {
            // Récupérer ou créer le panier
            Cart cart = cartService.getOrCreateActiveCart(customerId);
            
            // Ajouter le produit
            CartItem cartItem = cartItemService.addProductToCart(cart.getId(), productId, quantity);
            
            // Associer l'élément au panier
            cart.addItem(cartItem);
            cartService.saveCart(cart);
            
            return ResponseEntity.ok(new CartContract(cart));
        } catch (Exception e) {
            logger.error("Error adding product to cart for customer {}", customerId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Met à jour la quantité d'un produit dans le panier
     */
    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<CartItemContract> updateQuantity(
            @PathVariable long cartItemId,
            @RequestParam int quantity) {
        try {
            CartItem cartItem = cartItemService.updateQuantity(cartItemId, quantity);
            return ResponseEntity.ok(new CartItemContract(cartItem));
        } catch (Exception e) {
            logger.error("Error updating quantity for cart item {}", cartItemId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Supprime un produit du panier
     */
    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<Void> removeProductFromCart(@PathVariable long cartItemId) {
        try {
            cartItemService.removeProductFromCart(cartItemId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error removing product from cart", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Vide le panier d'un client
     */
    @DeleteMapping("/customer/{customerId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable long customerId) {
        try {
            cartService.clearCart(customerId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error clearing cart for customer {}", customerId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupère tous les éléments d'un panier
     */
    @GetMapping("/{cartId}/items")
    public ResponseEntity<List<CartItemContract>> getCartItems(@PathVariable long cartId) {
        try {
            List<CartItem> items = cartItemService.getItemsByCartId(cartId);
            List<CartItemContract> contracts = items.stream()
                    .map(CartItemContract::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            logger.error("Error getting cart items for cart {}", cartId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Vérifie si un produit est dans le panier
     */
    @GetMapping("/customer/{customerId}/check/{productId}")
    public ResponseEntity<Boolean> isProductInCart(
            @PathVariable long customerId,
            @PathVariable long productId) {
        try {
            Cart cart = cartService.getOrCreateActiveCart(customerId);
            boolean isInCart = cartItemService.isProductInCart(cart.getId(), productId);
            return ResponseEntity.ok(isInCart);
        } catch (Exception e) {
            logger.error("Error checking if product {} is in cart for customer {}", productId, customerId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupère la quantité d'un produit dans le panier
     */
    @GetMapping("/customer/{customerId}/quantity/{productId}")
    public ResponseEntity<Integer> getProductQuantityInCart(
            @PathVariable long customerId,
            @PathVariable long productId) {
        try {
            Cart cart = cartService.getOrCreateActiveCart(customerId);
            int quantity = cartItemService.getProductQuantityInCart(cart.getId(), productId);
            return ResponseEntity.ok(quantity);
        } catch (Exception e) {
            logger.error("Error getting product quantity {} in cart for customer {}", productId, customerId, e);
            return ResponseEntity.badRequest().build();
        }
    }
} 