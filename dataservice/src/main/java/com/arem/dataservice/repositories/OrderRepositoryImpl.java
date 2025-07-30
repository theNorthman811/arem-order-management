package com.arem.dataservice.repositories;

import com.arem.core.model.Order;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class OrderRepositoryImpl implements IOrderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Order save(Order order) {
        entityManager.persist(order);
        return order;
    }

    @Override
    public Optional<Order> findById(long id) {
        Order order = entityManager.find(Order.class, id);
        return Optional.ofNullable(order);
    }

    @Override
    public List<Order> findByCustomerId(long customerId) {
        TypedQuery<Order> query = entityManager.createQuery(
            "SELECT o FROM Order o WHERE o.customerId = :customerId", 
            Order.class
        );
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    @Override
    public List<Order> findAll() {
        return entityManager.createQuery("SELECT o FROM Order o", Order.class)
            .getResultList();
    }

    @Override
    public void delete(long id) {
        Optional<Order> orderOpt = findById(id);
        orderOpt.ifPresent(order -> entityManager.remove(order));
    }

    @Override
    public Order update(Order order) {
        return entityManager.merge(order);
    }
} 