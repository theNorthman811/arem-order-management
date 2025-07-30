package com.arem.productInput.contracts;

import com.arem.core.model.CartItem;

public class CartItemContract {
    private long id;
    private long cartId;
    private long productId;
    private int quantity;
    private ProductContract product;
    private double subtotal;
    
    public CartItemContract() {}
    
    public CartItemContract(CartItem cartItem) {
        this.id = cartItem.getId();
        this.cartId = cartItem.getCart() != null ? cartItem.getCart().getId() : 0;
        this.productId = cartItem.getProduct() != null ? cartItem.getProduct().getId() : 0;
        this.quantity = cartItem.getQuantity();
        this.subtotal = cartItem.getSubtotal();
        
        if (cartItem.getProduct() != null) {
            this.product = new ProductContract(cartItem.getProduct());
        }
    }
    
    public CartItem getModel() {
        CartItem cartItem = new CartItem();
        cartItem.setId(this.id);
        cartItem.setQuantity(this.quantity);
        return cartItem;
    }
    
    // Getters et Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getCartId() {
        return cartId;
    }
    
    public void setCartId(long cartId) {
        this.cartId = cartId;
    }
    
    public long getProductId() {
        return productId;
    }
    
    public void setProductId(long productId) {
        this.productId = productId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public ProductContract getProduct() {
        return product;
    }
    
    public void setProduct(ProductContract product) {
        this.product = product;
    }
    
    public double getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
} 