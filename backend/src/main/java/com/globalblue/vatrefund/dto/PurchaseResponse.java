package com.globalblue.vatrefund.dto;

import com.globalblue.vatrefund.domain.PurchaseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PurchaseResponse(
        Long id,
        String userEmail,
        String productName,
        PurchaseCategory category,
        BigDecimal netAmount,
        BigDecimal vatRate,
        BigDecimal vatAmount,
        BigDecimal refund,
        LocalDate purchaseDate
) {
}
