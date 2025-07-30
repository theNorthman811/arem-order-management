package com.arem.dataservice.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.arem.core.model.Product;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findById(long id);
    
    Optional<Product> findByName(String name);
    
    Optional<Product> findByReference(String reference);
    
}
