package com.arem.dataservice.repositories;

import com.arem.core.model.Order;
import java.util.List;
import java.util.Optional;

public interface IOrderRepository {
    Order save(Order order);
    Optional<Order> findById(long id);
    List<Order> findByCustomerId(long customerId);
    List<Order> findAll();
    void delete(long id);
    Order update(Order order);
} 