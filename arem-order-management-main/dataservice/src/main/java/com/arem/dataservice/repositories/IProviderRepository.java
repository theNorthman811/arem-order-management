package com.arem.dataservice.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.arem.core.model.Provider;

@Repository
public interface IProviderRepository extends JpaRepository<Provider, Long> {
    
    Optional<Provider> findById(long id);
    
    Optional<Provider> findByPhoneNumber(String phoneNumber);
    
    Optional<Provider> findByFirstNameAndLastNameAndPickName(String firstName, String lastName, String pickName);
    
    Optional<Provider> findByPickName(String pickName);
    
}
