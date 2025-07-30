package com.arem.dataservice.services;

import com.arem.core.model.Order;
import com.arem.core.model.Product;
import java.util.List;

public interface IOrderService {
    Order createOrder(long customerId);
    Order addItemToOrder(long orderId, long productId, double quantity);
    Order removeItemFromOrder(long orderId, long itemId);
    Order updateOrderItem(long orderId, long itemId, double quantity);
    Order getOrder(long orderId);
    List<Order> getCustomerOrders(long customerId);
    Order updateOrderStatus(long orderId, String status);
    void deleteOrder(long orderId);
    double calculateOrderTotal(long orderId);
    List<Order> findAll();
} 