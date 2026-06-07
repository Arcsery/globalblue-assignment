package com.globalblue.vatrefund.dto;

import com.globalblue.vatrefund.domain.PurchaseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PurchaseCreateRequest (

    @NotBlank(message = "User email is required")
    @Email(message = "User email must be valid")
    String userEmail,

    @NotBlank(message = "Product name is required")
    String productName,

    @NotNull(message = "Category is required")
    String category,

    @NotNull(message = "Net amount is required")
    @DecimalMin(value = "0.01", message = "Net amount must be positive")
    BigDecimal netAmount,

    @NotNull(message = "VAT rate is required")
    BigDecimal vatRate,

    @NotNull(message = "Purchase date is required")
    LocalDate purchaseDate)
{}
