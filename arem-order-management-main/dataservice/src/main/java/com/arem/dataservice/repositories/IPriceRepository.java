package com.arem.dataservice.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.arem.core.model.Price;

@Repository
public interface IPriceRepository extends JpaRepository<Price, Long> {
    
    Optional<Price> findById(long id);
    
    List<Price> findByProductId(long productId);
    
}
