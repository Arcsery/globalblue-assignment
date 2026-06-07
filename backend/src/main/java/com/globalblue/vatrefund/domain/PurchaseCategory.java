package com.globalblue.vatrefund.domain;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum PurchaseCategory {

    ELECTRONICS(new BigDecimal("1.00")),
    CLOTHING(new BigDecimal("0.50")),
    FOOD(new BigDecimal("0.00"));

    private final BigDecimal refundPercentage;

    PurchaseCategory(BigDecimal refundPercentage) {
        this.refundPercentage = refundPercentage;
    }

    public BigDecimal getRefundPercentage() {
        return refundPercentage;
    }

    public static PurchaseCategory fromValue(String value) {
        return Arrays.stream(values())
                .filter(category -> category.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported category: " + value + ". Supported categories are: " + supportedValues()
                ));
    }

    public static String supportedValues() {
        return Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}