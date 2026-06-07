package com.globalblue.vatrefund.domain;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum VatRate {

    STANDARD(new BigDecimal("0.27")),
    REDUCED(new BigDecimal("0.18")),
    LOW(new BigDecimal("0.05"));

    private final BigDecimal value;

    VatRate(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public static boolean isSupported(BigDecimal vatRate) {
        return Arrays.stream(values())
                .anyMatch(rate -> rate.value.compareTo(vatRate) == 0);
    }

    public static String supportedValues() {
        return Arrays.stream(values())
                .map(rate -> rate.value.toPlainString())
                .collect(Collectors.joining(", "));
    }
}