package com.globalblue.vatrefund.repository;

import com.globalblue.vatrefund.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByUserEmail(String userEmail);
}
