package com.irico.backend.source.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SourceRequest {

    @NotBlank(message = "sourceName cannot be null or empty")
    private String sourceName;

    @NotNull(message = "sourceCount must be provided")
    @Min(value = 1, message = "sourceCount must be non-negative")
    private BigDecimal sourceCount;

    @NotNull(message = "source need the rate rupees per sqare inch")
    @Min(value = 0, message = "source rate is non negative")
    private BigDecimal sourceRate;
}