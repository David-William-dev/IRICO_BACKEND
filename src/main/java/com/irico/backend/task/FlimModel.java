package com.irico.backend.task;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FlimModel {
    private BigDecimal length;
    private BigDecimal breadth;
    private String sourceName;
    private Integer filmType;
    private BigDecimal flim_thickness;

    public FlimModel() {
    }
}