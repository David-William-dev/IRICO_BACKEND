package com.irico.backend.source.dto;


import java.math.BigDecimal;

import com.irico.backend.source.SourceModel;

import lombok.Data;

@Data
public class SourceResponse {
    private String sourceId;
    private String sourceName;
    private BigDecimal sourceCount;
    private BigDecimal sourceRate;

    public SourceResponse(SourceModel model) {
        this.sourceId = model.getSourceId();
        this.sourceName = model.getSourceName();
        this.sourceCount = model.getSourceCount();
        this.sourceRate = model.getSourceRate();
    }
}