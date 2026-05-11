package com.datasync.datasync.controller;

import com.datasync.datasync.dto.request.SyncJobRequest;
import com.datasync.datasync.dto.request.SyncJobTriggerRequest;
import com.datasync.datasync.dto.response.MessageResponse;
import com.datasync.datasync.dto.response.SyncJobResponse;
import com.datasync.datasync.dto.response.SyncStatsResponse;
import com.datasync.datasync.service.SyncJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sync-jobs")
@RequiredArgsConstructor
@Tag(name = "Sync Jobs", description = "Senkronizasyon iş yönetimi")
public class SyncJobController {

    private final SyncJobService syncJobService;

    @PostMapping
    @Operation(summary = "Sync job oluştur")
    public ResponseEntity<SyncJobResponse> create(
            @Valid @RequestBody SyncJobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(syncJobService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID ile sync job getir")
    public ResponseEntity<SyncJobResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(syncJobService.getById(id));
    }

    @GetMapping("/job/{jobId}")
    @Operation(summary = "Job ID ile sync job getir")
    public ResponseEntity<SyncJobResponse> getByJobId(@PathVariable String jobId) {
        return ResponseEntity.ok(syncJobService.getByJobId(jobId));
    }

    @GetMapping
    @Operation(summary = "Tüm sync job'ları listele")
    public ResponseEntity<List<SyncJobResponse>> getAll() {
        return ResponseEntity.ok(syncJobService.getAll());
    }

    @GetMapping("/pending")
    @Operation(summary = "Bekleyen sync job'ları listele")
    public ResponseEntity<List<SyncJobResponse>> getPending() {
        return ResponseEntity.ok(syncJobService.getPendingJobs());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Sync job güncelle")
    public ResponseEntity<SyncJobResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SyncJobRequest request) {
        return ResponseEntity.ok(syncJobService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Sync job sil")
    public ResponseEntity<MessageResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(syncJobService.delete(id));
    }

    @PostMapping("/trigger")
    @Operation(summary = "Sync job tetikle")
    public ResponseEntity<MessageResponse> trigger(
            @Valid @RequestBody SyncJobTriggerRequest request) {
        return ResponseEntity.ok(syncJobService.trigger(request));
    }

    @PostMapping("/{jobId}/cancel")
    @Operation(summary = "Sync job iptal et")
    public ResponseEntity<MessageResponse> cancel(@PathVariable String jobId) {
        return ResponseEntity.ok(syncJobService.cancel(jobId));
    }

    @PostMapping("/{jobId}/pause")
    @Operation(summary = "Sync job duraklat")
    public ResponseEntity<MessageResponse> pause(@PathVariable String jobId) {
        return ResponseEntity.ok(syncJobService.pause(jobId));
    }

    @PostMapping("/{jobId}/resume")
    @Operation(summary = "Sync job devam ettir")
    public ResponseEntity<MessageResponse> resume(@PathVariable String jobId) {
        return ResponseEntity.ok(syncJobService.resume(jobId));
    }

    @GetMapping("/stats")
    @Operation(summary = "Senkronizasyon istatistikleri")
    public ResponseEntity<SyncStatsResponse> getStats() {
        return ResponseEntity.ok(syncJobService.getStats());
    }
}