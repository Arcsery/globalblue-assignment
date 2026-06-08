package com.globalblue.vatrefund.exception;

public class PurchasesNotFoundException extends RuntimeException {

    public PurchasesNotFoundException(String userEmail) {
        super("No purchases found for user email: " + userEmail);
    }
}