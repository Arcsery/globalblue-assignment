package com.globalblue.vatrefund.controller;

import com.globalblue.vatrefund.dto.PurchaseCreateRequest;
import com.globalblue.vatrefund.dto.PurchaseResponse;
import com.globalblue.vatrefund.dto.PurchaseSummaryResponse;
import com.globalblue.vatrefund.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseResponse createPurchase(@Valid @RequestBody PurchaseCreateRequest request){
        return purchaseService.createPurchase(request);
    }

    @GetMapping("/{userEmail}")
    public PurchaseSummaryResponse getPurchaseByUserEmail(@PathVariable String userEmail){
        return purchaseService.getPurchasesByUserEmail(userEmail);
    }
}
