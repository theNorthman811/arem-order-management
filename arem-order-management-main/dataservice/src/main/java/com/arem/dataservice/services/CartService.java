package com.arem.dataservice.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arem.core.model.Cart;
import com.arem.core.model.Customer;
import com.arem.dataservice.repositories.ICartRepository;
import com.arem.dataservice.repositories.ICustomerRepository;
import com.arem.dataservice.repositories.ICartItemRepository;

@Service
@Transactional
public class CartService {
    
    @Autowired
    private ICartRepository cartRepository;
    
    @Autowired
    private ICustomerRepository customerRepository;
    
    @Autowired
    private ICartItemRepository cartItemRepository;
    
    /**
     * Récupère le panier actif d'un client
     */
    public Optional<Cart> getActiveCartByCustomerId(long customerId) {
        return cartRepository.findByCustomerIdAndActive(customerId);
    }
    
    /**
     * Crée un nouveau panier pour un client
     */
    public Cart createCartForCustomer(long customerId) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Client non trouvé avec l'ID: " + customerId);
        }
        
        // Désactiver l'ancien panier s'il existe
        Optional<Cart> existingCart = cartRepository.findByCustomerIdAndActive(customerId);
        if (existingCart.isPresent()) {
            Cart oldCart = existingCart.get();
            oldCart.setActive(false);
            cartRepository.save(oldCart);
        }
        
        // Créer un nouveau panier
        Cart newCart = new Cart(customerOpt.get());
        return cartRepository.save(newCart);
    }
    
    /**
     * Récupère ou crée un panier actif pour un client
     */
    public Cart getOrCreateActiveCart(long customerId) {
        Optional<Cart> cartOpt = getActiveCartByCustomerId(customerId);
        Cart cart = cartOpt.orElseGet(() -> createCartForCustomer(customerId));
        
        // Ne pas utiliser setItems() à cause de orphanRemoval = true
        // Les items seront chargés automatiquement avec FetchType.EAGER
        // ou on peut les charger manuellement si nécessaire
        
        return cart;
    }
    
    /**
     * Récupère tous les paniers d'un client
     */
    public List<Cart> getAllCartsByCustomerId(long customerId) {
        return cartRepository.findAllByCustomerId(customerId);
    }
    
    /**
     * Vide le panier d'un client
     */
    public void clearCart(long customerId) {
        Optional<Cart> cartOpt = getActiveCartByCustomerId(customerId);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cart.clear();
            cartRepository.save(cart);
        }
    }
    
    /**
     * Désactive le panier d'un client
     */
    public void deactivateCart(long customerId) {
        Optional<Cart> cartOpt = getActiveCartByCustomerId(customerId);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cart.setActive(false);
            cartRepository.save(cart);
        }
    }
    
    /**
     * Récupère un panier par son ID
     */
    public Optional<Cart> getCartById(long cartId) {
        return cartRepository.findById(cartId);
    }
    
    /**
     * Sauvegarde un panier
     */
    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }
    
    /**
     * Supprime un panier
     */
    public void deleteCart(long cartId) {
        cartRepository.deleteById(cartId);
    }
    
    /**
     * Vérifie si un client a un panier actif
     */
    public boolean hasActiveCart(long customerId) {
        return cartRepository.countActiveByCustomerId(customerId) > 0;
    }
} 