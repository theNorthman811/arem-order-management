package com.arem.dataservice.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.arem.core.model.Cart;

@Repository
public interface ICartRepository extends JpaRepository<Cart, Long> {
    
    @Query("SELECT c FROM Cart c WHERE c.customer.id = :customerId AND c.isActive = true")
    Optional<Cart> findByCustomerIdAndActive(@Param("customerId") long customerId);
    
    @Query("SELECT c FROM Cart c WHERE c.customer.id = :customerId")
    List<Cart> findAllByCustomerId(@Param("customerId") long customerId);
    
    @Query("SELECT c FROM Cart c WHERE c.isActive = true")
    List<Cart> findAllActive();
    
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.customer.id = :customerId AND c.isActive = true")
    long countActiveByCustomerId(@Param("customerId") long customerId);
} 