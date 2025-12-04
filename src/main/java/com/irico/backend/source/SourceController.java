package com.irico.backend.source;

import com.irico.backend.source.dto.SourceDeltaRequest;
import com.irico.backend.source.dto.SourceRequest;
import com.irico.backend.source.dto.SourceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/sources")
public class SourceController {

    @Autowired
    private SourceService sourceService;

    @PostMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<SourceResponse> createSource(@Valid @RequestBody SourceRequest request) {
        SourceResponse response = sourceService.createSource(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('EMPLOYEE') or hasAuthority('ADMIN')")
    @GetMapping("/{sourceId}")
    public ResponseEntity<SourceResponse> getSourceById(@PathVariable String sourceId) {
        SourceResponse response = sourceService.getSourceById(sourceId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('EMPLOYEE') or hasAuthority('ADMIN')")
    @GetMapping("/name/{sourceName}")
    public ResponseEntity<SourceResponse> getSourceByName(@PathVariable String sourceName) {
        SourceResponse response = sourceService.getSourceByName(sourceName);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('EMPLOYEE') or hasAuthority('ADMIN')")
    public ResponseEntity<List<SourceResponse>> getAllSources() {
        List<SourceResponse> sources = sourceService.getAllSources();
        return ResponseEntity.ok(sources);
    }

    @PutMapping("/{sourceId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<SourceResponse> updateSource(
            @PathVariable String sourceId,
            @Valid @RequestBody SourceRequest request) {
        SourceResponse response = sourceService.updateSource(sourceId, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{sourceId}")
    public ResponseEntity<Void> deleteSource(@PathVariable String sourceId) {
        sourceService.deleteSource(sourceId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('EMPLOYEE') or hasAuthority('ADMIN')")
    @PostMapping("/add")
    @Operation(summary = "Add amount to source count", description = "Atomically adds a specified amount to the current sourceCount.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Source count updated successfully"),
            @ApiResponse(responseCode = "404", description = "Source not found"),
            @ApiResponse(responseCode = "400", description = "Invalid amount or zero")
    })
    public ResponseEntity<SourceResponse> addSourceCount(
            @RequestParam String sourceId,
            @Valid @RequestBody SourceDeltaRequest request) {
        SourceResponse response = sourceService.addSourceCount(sourceId, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('EMPLOYEE') or hasAuthority('ADMIN')")
    @PostMapping("/{sourceId}/subtract")
    @Operation(summary = "Subtract amount from source count", description = "Atomically subtracts a specified amount from the current sourceCount. Fails if result would be negative.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Source count updated successfully"),
            @ApiResponse(responseCode = "404", description = "Source not found"),
            @ApiResponse(responseCode = "400", description = "Amount too large (would make count negative)")
    })
    public ResponseEntity<SourceResponse> subtractSourceCount(
            @PathVariable String sourceId,
            @Valid @RequestBody SourceDeltaRequest request) {
        SourceResponse response = sourceService.subtractSourceCount(sourceId, request);
        return ResponseEntity.ok(response);
    }
}