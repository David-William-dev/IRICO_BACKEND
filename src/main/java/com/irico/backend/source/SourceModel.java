package com.irico.backend.source;


import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "source")
@Data
@NoArgsConstructor
public class SourceModel {
    @Id
    private String sourceId;
    private String sourceName;
    private BigDecimal sourceCount;
    private BigDecimal sourceRate;
    
    public SourceModel(String taskId){
        this.sourceId = taskId;
    }
}
