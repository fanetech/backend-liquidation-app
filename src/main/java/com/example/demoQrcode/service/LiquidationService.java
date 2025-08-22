package com.example.demoQrcode.service;

import com.example.demoQrcode.entity.Liquidation;
import com.example.demoQrcode.entity.LiquidationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LiquidationService {

    Page<Liquidation> list(Pageable pageable);

    Optional<Liquidation> get(Long id);

    Liquidation create(Liquidation liquidation);

    Optional<Liquidation> update(Long id, Liquidation liquidation);

    Optional<Liquidation> markAsPaid(Long id);

    Page<Liquidation> searchWithFilters(Long customerId, LiquidationStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<Liquidation> findByCustomer(Long customerId);

    BigDecimal calculatePenalty(Liquidation liquidation, BigDecimal dailyRate);

    Page<Liquidation> searchByTerm(String term, Pageable pageable);
}


