package com.coralpay.repository;


import com.coralpay.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByUserId(Long userId);

    Customer findCustomerById(Long id);

    Customer findCustomerByUserId(Long id);

    List<Customer> findByIsActive(Boolean isActive);

    @Query("SELECT p FROM Customer p WHERE ((:companyName IS NULL) OR (:companyName IS NOT NULL AND p.companyName like %:companyName%)) order by p.id desc")
    Page<Customer> findCustomersProperties(@Param("companyName") String companyName, Pageable pageable);

}
