package com.globalblue.vatrefund.exeption;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse (
        LocalDateTime timestamp,
        int status,
        String error,
        List<String> messages
){
}
