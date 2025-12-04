package com.irico.backend.task.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class FlimRequest {

    @NotNull(message = "Length is required")
    @Positive(message = "Length must be positive")
    private BigDecimal length;

    @NotNull(message = "Breadth is required")
    @Positive(message = "Breadth must be positive")
    private BigDecimal breadth;

    @NotBlank(message = "Source name is required")
    private String sourceName;

    @NotNull(message = "Provide per square inch rate in ₹")
    @Positive(message = "Rate must be positive")
    private BigDecimal sourceRatePerSquareInch;

    @NotNull(message = "Film type is required")
    @Positive(message = "Flim type must be positive number")
    private Integer filmType;

    @NotNull(message = "Film thickness is required")
    @Positive(message = "Flim thickness must be positive number")
    private BigDecimal flimThickness;
}