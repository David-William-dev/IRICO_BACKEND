package com.irico.backend.task.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LocationDetailsRequest {
    @NotEmpty(message = "location is empty")
    List<@NotBlank(message = "use comma in the location correctly") String> locations;

    // @NotNull(message = "Flim data is required")
    @NotEmpty(message = "Flim is blank")    
    @Valid
    private List<FlimRequest> flims;

}
