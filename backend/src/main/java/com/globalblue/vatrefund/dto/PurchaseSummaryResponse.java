package com.globalblue.vatrefund.dto;

import java.math.BigDecimal;
import java.util.List;

public record PurchaseSummaryResponse (
        String userEmail,
        BigDecimal totalNetAmount,
        BigDecimal totalVat,
        BigDecimal totalRefundableVat,
        String currency,
        List<PurchaseResponse> purchases
){
}
