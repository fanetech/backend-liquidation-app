package com.example.demoQrcode.repository;

import com.example.demoQrcode.entity.Liquidation;
import com.example.demoQrcode.entity.LiquidationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LiquidationRepository extends JpaRepository<Liquidation, Long> {

    Page<Liquidation> findByStatus(LiquidationStatus status, Pageable pageable);

    @Query("SELECT l FROM Liquidation l WHERE (:customerId IS NULL OR l.customer.id = :customerId) " +
            "AND (:status IS NULL OR l.status = :status) " +
            "AND (:startDate IS NULL OR l.issueDate >= :startDate) " +
            "AND (:endDate IS NULL OR l.issueDate <= :endDate)")
    Page<Liquidation> search(
            @Param("customerId") Long customerId,
            @Param("status") LiquidationStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    List<Liquidation> findByCustomer_Id(Long customerId);
}


