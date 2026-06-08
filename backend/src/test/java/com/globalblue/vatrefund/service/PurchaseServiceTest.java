package com.globalblue.vatrefund.service;

import com.globalblue.vatrefund.domain.Purchase;
import com.globalblue.vatrefund.domain.PurchaseCategory;
import com.globalblue.vatrefund.dto.PurchaseCreateRequest;
import com.globalblue.vatrefund.dto.PurchaseResponse;
import com.globalblue.vatrefund.dto.PurchaseSummaryResponse;
import com.globalblue.vatrefund.repository.PurchaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    @Test
    void createPurchase_shouldCalculateFullRefundForElectronics() {
        PurchaseCreateRequest request = new PurchaseCreateRequest(
                "USER@example.com",
                "Laptop",
                "Electronics",
                new BigDecimal("1000"),
                new BigDecimal("0.27"),
                LocalDate.of(2026, 6, 1)
        );

        Purchase savedPurchase = Purchase.builder()
                .id(1L)
                .userEmail("user@example.com")
                .productName("Laptop")
                .category(PurchaseCategory.ELECTRONICS)
                .netAmount(new BigDecimal("1000"))
                .vatRate(new BigDecimal("0.27"))
                .purchaseDate(LocalDate.of(2026, 6, 1))
                .build();

        when(purchaseRepository.save(any(Purchase.class))).thenReturn(savedPurchase);

        PurchaseResponse response = purchaseService.createPurchase(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.userEmail()).isEqualTo("user@example.com");
        assertThat(response.productName()).isEqualTo("Laptop");
        assertThat(response.category()).isEqualTo(PurchaseCategory.ELECTRONICS);
        assertThat(response.vatAmount()).isEqualByComparingTo(new BigDecimal("270"));
        assertThat(response.refund()).isEqualByComparingTo(new BigDecimal("270"));

        ArgumentCaptor<Purchase> purchaseCaptor = ArgumentCaptor.forClass(Purchase.class);
        verify(purchaseRepository).save(purchaseCaptor.capture());

        Purchase purchaseToSave = purchaseCaptor.getValue();

        assertThat(purchaseToSave.getUserEmail()).isEqualTo("user@example.com");
        assertThat(purchaseToSave.getProductName()).isEqualTo("Laptop");
        assertThat(purchaseToSave.getCategory()).isEqualTo(PurchaseCategory.ELECTRONICS);
    }

    @Test
    void createPurchase_shouldCalculateHalfRefundForClothing() {
        PurchaseCreateRequest request = new PurchaseCreateRequest(
                "user@example.com",
                "Jacket",
                "Clothing",
                new BigDecimal("1000"),
                new BigDecimal("0.27"),
                LocalDate.of(2026, 6, 1)
        );

        Purchase savedPurchase = Purchase.builder()
                .id(2L)
                .userEmail("user@example.com")
                .productName("Jacket")
                .category(PurchaseCategory.CLOTHING)
                .netAmount(new BigDecimal("1000"))
                .vatRate(new BigDecimal("0.27"))
                .purchaseDate(LocalDate.of(2026, 6, 1))
                .build();

        when(purchaseRepository.save(any(Purchase.class))).thenReturn(savedPurchase);

        PurchaseResponse response = purchaseService.createPurchase(request);

        assertThat(response.vatAmount()).isEqualByComparingTo(new BigDecimal("270"));
        assertThat(response.refund()).isEqualByComparingTo(new BigDecimal("135"));
    }

    @Test
    void createPurchase_shouldCalculateZeroRefundForFood() {
        PurchaseCreateRequest request = new PurchaseCreateRequest(
                "user@example.com",
                "Bread",
                "Food",
                new BigDecimal("1000"),
                new BigDecimal("0.27"),
                LocalDate.of(2026, 6, 1)
        );

        Purchase savedPurchase = Purchase.builder()
                .id(3L)
                .userEmail("user@example.com")
                .productName("Bread")
                .category(PurchaseCategory.FOOD)
                .netAmount(new BigDecimal("1000"))
                .vatRate(new BigDecimal("0.27"))
                .purchaseDate(LocalDate.of(2026, 6, 1))
                .build();

        when(purchaseRepository.save(any(Purchase.class))).thenReturn(savedPurchase);

        PurchaseResponse response = purchaseService.createPurchase(request);

        assertThat(response.vatAmount()).isEqualByComparingTo(new BigDecimal("270"));
        assertThat(response.refund()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void createPurchase_shouldThrowExceptionForUnsupportedVatRate() {
        PurchaseCreateRequest request = new PurchaseCreateRequest(
                "user@example.com",
                "Laptop",
                "Electronics",
                new BigDecimal("1000"),
                new BigDecimal("0.12"),
                LocalDate.of(2026, 6, 1)
        );

        assertThatThrownBy(() -> purchaseService.createPurchase(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported VAT rate");

        verify(purchaseRepository, never()).save(any(Purchase.class));
    }

    @Test
    void getPurchasesByUserEmail_shouldReturnCorrectSummary() {
        List<Purchase> purchases = List.of(
                Purchase.builder()
                        .id(1L)
                        .userEmail("user@example.com")
                        .productName("Laptop")
                        .category(PurchaseCategory.ELECTRONICS)
                        .netAmount(new BigDecimal("1000"))
                        .vatRate(new BigDecimal("0.27"))
                        .purchaseDate(LocalDate.of(2026, 6, 1))
                        .build(),
                Purchase.builder()
                        .id(2L)
                        .userEmail("user@example.com")
                        .productName("Jacket")
                        .category(PurchaseCategory.CLOTHING)
                        .netAmount(new BigDecimal("1000"))
                        .vatRate(new BigDecimal("0.27"))
                        .purchaseDate(LocalDate.of(2026, 6, 1))
                        .build()
        );

        when(purchaseRepository.findByUserEmail("user@example.com"))
                .thenReturn(purchases);

        PurchaseSummaryResponse response = purchaseService.getPurchasesByUserEmail("USER@example.com");

        assertThat(response.userEmail()).isEqualTo("USER@example.com");
        assertThat(response.totalNetAmount()).isEqualByComparingTo(new BigDecimal("2000"));
        assertThat(response.totalVat()).isEqualByComparingTo(new BigDecimal("540"));
        assertThat(response.totalRefundableVat()).isEqualByComparingTo(new BigDecimal("405"));
        assertThat(response.currency()).isEqualTo("EUR");
        assertThat(response.purchases()).hasSize(2);

        verify(purchaseRepository).findByUserEmail("user@example.com");
    }
}