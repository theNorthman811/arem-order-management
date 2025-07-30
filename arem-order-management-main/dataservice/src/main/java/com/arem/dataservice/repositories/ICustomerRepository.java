package com.arem.dataservice.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.arem.core.model.Customer;

@Repository
public interface ICustomerRepository extends JpaRepository<Customer, Long>
{
	Optional<Customer> findById(Long id);
	
	Optional<Customer> findByPhoneNumber(String phoneNumber);
	
	Optional<Customer> findByEmail(String email);
	
	Optional<Customer> findByFirstNameAndLastNameAndPickName(String firstName, String lastName, String pickName);
	
	Optional<Customer> findByPickName(String pickName);
}
