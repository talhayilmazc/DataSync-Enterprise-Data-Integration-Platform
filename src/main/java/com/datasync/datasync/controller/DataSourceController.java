package com.datasync.datasync.controller;

import com.datasync.datasync.dto.request.DataSourceRequest;
import com.datasync.datasync.dto.response.DataSourceResponse;
import com.datasync.datasync.dto.response.MessageResponse;
import com.datasync.datasync.service.DataSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/data-sources")
@RequiredArgsConstructor
@Tag(name = "Data Sources", description = "Veri kaynağı yönetimi")
public class DataSourceController {

    private final DataSourceService dataSourceService;

    @PostMapping
    @Operation(summary = "Veri kaynağı oluştur")
    public ResponseEntity<DataSourceResponse> create(
            @Valid @RequestBody DataSourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dataSourceService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID ile veri kaynağı getir")
    public ResponseEntity<DataSourceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(dataSourceService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Tüm veri kaynaklarını listele")
    public ResponseEntity<List<DataSourceResponse>> getAll() {
        return ResponseEntity.ok(dataSourceService.getAll());
    }

    @GetMapping("/active")
    @Operation(summary = "Aktif veri kaynaklarını listele")
    public ResponseEntity<List<DataSourceResponse>> getAllActive() {
        return ResponseEntity.ok(dataSourceService.getAllActive());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Veri kaynağını güncelle")
    public ResponseEntity<DataSourceResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody DataSourceRequest request) {
        return ResponseEntity.ok(dataSourceService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Veri kaynağını sil")
    public ResponseEntity<MessageResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(dataSourceService.delete(id));
    }

    @PostMapping("/{id}/test")
    @Operation(summary = "Bağlantı testi yap")
    public ResponseEntity<MessageResponse> testConnection(@PathVariable Long id) {
        return ResponseEntity.ok(dataSourceService.testConnection(id));
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Veri kaynağını aktifleştir")
    public ResponseEntity<MessageResponse> activate(@PathVariable Long id) {
        return ResponseEntity.ok(dataSourceService.activate(id));
    }

    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Veri kaynağını deaktifleştir")
    public ResponseEntity<MessageResponse> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(dataSourceService.deactivate(id));
    }
}