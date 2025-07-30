package com.arem.dataservice.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.arem.core.model.Seller;

@Repository
public interface ISellerRepository extends JpaRepository<Seller, Long> {
    
    Optional<Seller> findById(long id);
    
    Optional<Seller> findByPhoneNumber(String phoneNumber);
    
    Optional<Seller> findByFirstNameAndLastNameAndPickName(String firstName, String lastName, String pickName);
    
    Optional<Seller> findByPickName(String pickName);
    
    Optional<Seller> findByEmail(String email);
    
}
