package com.arem.dataservice.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arem.core.model.CartItem;
import com.arem.core.model.Product;
import com.arem.dataservice.repositories.ICartItemRepository;
import com.arem.dataservice.repositories.IProductRepository;

@Service
@Transactional
public class CartItemService {
    
    @Autowired
    private ICartItemRepository cartItemRepository;
    
    @Autowired
    private IProductRepository productRepository;
    
    /**
     * Ajoute un produit au panier
     */
    public CartItem addProductToCart(long cartId, long productId, int quantity) {
        // Vérifier si le produit existe
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Produit non trouvé avec l'ID: " + productId);
        }
        
        Product product = productOpt.get();
        
        // Vérifier le stock
        if (!product.hasEnoughStock(quantity)) {
            throw new RuntimeException("Stock insuffisant pour le produit: " + product.getName());
        }
        
        // Vérifier si le produit est déjà dans le panier
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cartId, productId);
        
        if (existingItem.isPresent()) {
            // Mettre à jour la quantité
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            
            // Vérifier le stock pour la nouvelle quantité
            if (!product.hasEnoughStock(newQuantity)) {
                throw new RuntimeException("Stock insuffisant pour la quantité demandée");
            }
            
            item.setQuantity(newQuantity);
            return cartItemRepository.save(item);
        } else {
            // Créer un nouvel élément
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            
            // Récupérer le cart et l'associer
            // Note: On ne peut pas injecter CartService ici à cause des dépendances circulaires
            // Le cart sera défini par le service appelant via cart.addItem(newItem)
            return newItem; // Ne pas sauvegarder ici, laisser le CartService s'en charger
        }
    }
    
    /**
     * Met à jour la quantité d'un produit dans le panier
     */
    public CartItem updateQuantity(long cartItemId, int newQuantity) {
        Optional<CartItem> itemOpt = cartItemRepository.findById(cartItemId);
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Élément du panier non trouvé avec l'ID: " + cartItemId);
        }
        
        CartItem item = itemOpt.get();
        Product product = item.getProduct();
        
        // Vérifier le stock
        if (!product.hasEnoughStock(newQuantity)) {
            throw new RuntimeException("Stock insuffisant pour la quantité demandée");
        }
        
        item.setQuantity(newQuantity);
        return cartItemRepository.save(item);
    }
    
    /**
     * Supprime un produit du panier
     */
    public void removeProductFromCart(long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
    
    /**
     * Récupère tous les éléments d'un panier
     */
    public List<CartItem> getItemsByCartId(long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }
    
    /**
     * Récupère un élément du panier par son ID
     */
    public Optional<CartItem> getCartItemById(long cartItemId) {
        return cartItemRepository.findById(cartItemId);
    }
    
    /**
     * Vérifie si un produit est dans le panier
     */
    public boolean isProductInCart(long cartId, long productId) {
        return cartItemRepository.findByCartIdAndProductId(cartId, productId).isPresent();
    }
    
    /**
     * Récupère la quantité d'un produit dans le panier
     */
    public int getProductQuantityInCart(long cartId, long productId) {
        Optional<CartItem> item = cartItemRepository.findByCartIdAndProductId(cartId, productId);
        return item.map(CartItem::getQuantity).orElse(0);
    }
    
    /**
     * Vide tous les éléments d'un panier
     */
    public void clearCartItems(long cartId) {
        cartItemRepository.deleteByCartId(cartId);
    }
    
    /**
     * Sauvegarde un élément du panier
     */
    public CartItem saveCartItem(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }
} 