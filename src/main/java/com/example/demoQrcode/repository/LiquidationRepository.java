package com.example.demoQrcode.repository;

import com.example.demoQrcode.entity.Liquidation;
import com.example.demoQrcode.entity.LiquidationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface LiquidationRepository extends JpaRepository<Liquidation, Long>, JpaSpecificationExecutor<Liquidation> {

    Page<Liquidation> findByStatus(LiquidationStatus status, Pageable pageable);

    List<Liquidation> findByCustomer_Id(Long customerId);
}


