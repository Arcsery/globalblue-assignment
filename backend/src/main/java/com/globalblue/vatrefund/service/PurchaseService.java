package com.globalblue.vatrefund.service;

import com.globalblue.vatrefund.domain.Purchase;
import com.globalblue.vatrefund.domain.PurchaseCategory;
import com.globalblue.vatrefund.domain.VatRate;
import com.globalblue.vatrefund.dto.PurchaseCreateRequest;
import com.globalblue.vatrefund.dto.PurchaseResponse;
import com.globalblue.vatrefund.dto.PurchaseSummaryResponse;
import com.globalblue.vatrefund.exception.PurchasesNotFoundException;
import com.globalblue.vatrefund.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.globalblue.vatrefund.business.VatRefundPolicy.CURRENCY;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    public PurchaseResponse createPurchase(PurchaseCreateRequest request) {
        validateVatRate(request.vatRate());

        Purchase purchase = Purchase.builder()
                .userEmail(normalizeEmail(request.userEmail()))
                .productName(request.productName().trim())
                .category(PurchaseCategory.fromValue(request.category()))
                .netAmount(request.netAmount())
                .vatRate(request.vatRate())
                .purchaseDate(request.purchaseDate())
                .build();

        Purchase savedPurchase = purchaseRepository.save(purchase);

        return mapToPurchaseResponse(savedPurchase);
    }

    public PurchaseSummaryResponse getPurchasesByUserEmail(String userEmail) {
        String normalizedEmail = normalizeEmail(userEmail);

        List<PurchaseResponse> purchases = purchaseRepository.findByUserEmail(normalizedEmail)
                .stream()
                .map(this::mapToPurchaseResponse)
                .toList();

        if (purchases.isEmpty()) {
            throw new PurchasesNotFoundException(normalizedEmail);
        }

        BigDecimal totalNetAmount = BigDecimal.ZERO;
        BigDecimal totalVat = BigDecimal.ZERO;
        BigDecimal totalRefundableVat = BigDecimal.ZERO;

        for (PurchaseResponse purchase : purchases) {
            totalNetAmount = totalNetAmount.add(purchase.netAmount());
            totalVat = totalVat.add(purchase.vatAmount());
            totalRefundableVat = totalRefundableVat.add(purchase.refund());
        }
        return new PurchaseSummaryResponse(
                userEmail,
                totalNetAmount,
                totalVat,
                totalRefundableVat,
                CURRENCY,
                purchases
        );
    }

    private PurchaseResponse mapToPurchaseResponse(Purchase purchase) {
        BigDecimal vatAmount = calculateVatAmount(purchase);
        BigDecimal refund = calculateRefund(purchase, vatAmount);

        return new PurchaseResponse(
                purchase.getId(),
                purchase.getUserEmail(),
                purchase.getProductName(),
                purchase.getCategory(),
                purchase.getNetAmount(),
                purchase.getVatRate(),
                vatAmount,
                refund,
                purchase.getPurchaseDate()
        );
    }

    private BigDecimal calculateVatAmount(Purchase purchase) {
        return purchase.getNetAmount()
                .multiply(purchase.getVatRate());
    }

    private BigDecimal calculateRefund(Purchase purchase, BigDecimal vatAmount) {
        return vatAmount.multiply(
                purchase.getCategory().getRefundPercentage()
        );
    }

    private void validateVatRate(BigDecimal vatRate) {
        if (!VatRate.isSupported(vatRate)) {
            throw new IllegalArgumentException(
                    "Unsupported VAT rate: " + vatRate + ". Supported VAT rates are: " + VatRate.supportedValues()
            );
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}