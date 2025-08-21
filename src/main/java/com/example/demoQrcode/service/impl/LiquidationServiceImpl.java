package com.example.demoQrcode.service.impl;

import com.example.demoQrcode.entity.Customer;
import com.example.demoQrcode.entity.Liquidation;
import com.example.demoQrcode.entity.LiquidationStatus;
import com.example.demoQrcode.repository.CustomerRepository;
import com.example.demoQrcode.repository.LiquidationRepository;
import com.example.demoQrcode.service.LiquidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class LiquidationServiceImpl implements LiquidationService {

    @Autowired
    private LiquidationRepository liquidationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Page<Liquidation> list(Pageable pageable) {
        return liquidationRepository.findAll(pageable);
    }

    @Override
    public Optional<Liquidation> get(Long id) {
        return liquidationRepository.findById(id);
    }

    @Override
    public Liquidation create(Liquidation liquidation) {
        if (liquidation.getCustomer() == null || liquidation.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Client non spécifié");
        }
        Customer customer = customerRepository.findById(liquidation.getCustomer().getId())
                .orElseThrow(() -> new IllegalArgumentException("Client introuvable"));
        liquidation.setCustomer(customer);
        if (liquidation.getIssueDate() == null) {
            liquidation.setIssueDate(LocalDate.now());
        }
        if (liquidation.getDueDate() == null || liquidation.getDueDate().isBefore(liquidation.getIssueDate())) {
            throw new IllegalArgumentException("Échéance invalide");
        }
        // Déterminer le statut initial (OVERDUE si déjà en retard au moment de la création)
        LiquidationStatus status = LocalDate.now().isAfter(liquidation.getDueDate()) ? LiquidationStatus.OVERDUE : LiquidationStatus.PENDING;
        liquidation.setStatus(status);
        return liquidationRepository.save(liquidation);
    }

    @Override
    public Optional<Liquidation> update(Long id, Liquidation liquidation) {
        return liquidationRepository.findById(id).map(existing -> {
            if (liquidation.getCustomer() != null && liquidation.getCustomer().getId() != null) {
                Customer customer = customerRepository.findById(liquidation.getCustomer().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Client introuvable"));
                existing.setCustomer(customer);
            }
            if (liquidation.getTaxType() != null) existing.setTaxType(liquidation.getTaxType());
            if (liquidation.getAmount() != null) existing.setAmount(liquidation.getAmount());
            if (liquidation.getIssueDate() != null) existing.setIssueDate(liquidation.getIssueDate());
            if (liquidation.getDueDate() != null) existing.setDueDate(liquidation.getDueDate());

            // Mettre à jour statut en fonction de l'échéance si pas payé
            if (existing.getStatus() != LiquidationStatus.PAID) {
                LiquidationStatus status = LocalDate.now().isAfter(existing.getDueDate()) ? LiquidationStatus.OVERDUE : LiquidationStatus.PENDING;
                existing.setStatus(status);
            }
            return liquidationRepository.save(existing);
        });
    }

    @Override
    public Optional<Liquidation> markAsPaid(Long id) {
        return liquidationRepository.findById(id).map(l -> {
            l.setStatus(LiquidationStatus.PAID);
            return liquidationRepository.save(l);
        });
    }

    @Override
    public Page<Liquidation> searchWithFilters(Long customerId, LiquidationStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Specification<Liquidation> spec = Specification.where(null);
        if (customerId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("customer").get("id"), customerId));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (startDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("issueDate"), startDate));
        }
        if (endDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("issueDate"), endDate));
        }
        return liquidationRepository.findAll(spec, pageable);
    }

    @Override
    public List<Liquidation> findByCustomer(Long customerId) {
        return liquidationRepository.findByCustomer_Id(customerId);
    }

    @Override
    public BigDecimal calculatePenalty(Liquidation liquidation, BigDecimal dailyRate) {
        if (liquidation == null || dailyRate == null || dailyRate.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (liquidation.getStatus() == LiquidationStatus.PAID) {
            return BigDecimal.ZERO;
        }
        LocalDate today = LocalDate.now();
        if (!today.isAfter(liquidation.getDueDate())) {
            return BigDecimal.ZERO;
        }
        long overdueDays = ChronoUnit.DAYS.between(liquidation.getDueDate(), today);
        if (overdueDays <= 0) return BigDecimal.ZERO;
        // pénalité = amount * dailyRate * overdueDays
        BigDecimal penalty = liquidation.getAmount()
                .multiply(dailyRate)
                .multiply(BigDecimal.valueOf(overdueDays));
        return penalty.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Page<Liquidation> searchByTerm(String term, Pageable pageable) {
        final String like = term == null ? "" : term.trim().toLowerCase();
        if (like.isEmpty()) {
            return liquidationRepository.findAll(pageable);
        }
        Specification<Liquidation> spec = Specification.where((root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("taxType")), "%" + like + "%"),
                cb.like(cb.lower(root.get("status").as(String.class)), "%" + like + "%"),
                cb.like(cb.lower(root.get("customer").get("firstName")), "%" + like + "%"),
                cb.like(cb.lower(root.get("customer").get("lastName")), "%" + like + "%"),
                cb.like(cb.lower(root.get("customer").get("ifu")), "%" + like + "%")
        ));
        return liquidationRepository.findAll(spec, pageable);
    }
}


