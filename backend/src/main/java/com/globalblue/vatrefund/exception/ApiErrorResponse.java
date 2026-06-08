package com.globalblue.vatrefund.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse (
        LocalDateTime timestamp,
        int status,
        String error,
        List<String> messages
){
}
