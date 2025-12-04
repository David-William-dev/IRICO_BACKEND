package com.irico.backend.source.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class SourceDeltaRequest {

    @NotNull(message = "Amount cannot be null")
    private BigDecimal sourceCount;

    @AssertTrue(message = "Amount cannot be zero")
    public boolean isAmountNonZero() {
        return sourceCount == null || sourceCount.intValue() != 0;
    }
}