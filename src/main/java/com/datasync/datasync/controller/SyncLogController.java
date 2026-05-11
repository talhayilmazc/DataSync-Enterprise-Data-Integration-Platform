package com.datasync.datasync.controller;

import com.datasync.datasync.dto.response.SyncLogResponse;
import com.datasync.datasync.service.SyncLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sync-logs")
@RequiredArgsConstructor
@Tag(name = "Sync Logs", description = "Senkronizasyon log yönetimi")
public class SyncLogController {

    private final SyncLogService syncLogService;

    @GetMapping
    @Operation(summary = "Tüm logları listele")
    public ResponseEntity<List<SyncLogResponse>> getAll() {
        return ResponseEntity.ok(syncLogService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID ile log getir")
    public ResponseEntity<SyncLogResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(syncLogService.getById(id));
    }

    @GetMapping("/job/{jobId}")
    @Operation(summary = "Job ID ile logları getir")
    public ResponseEntity<List<SyncLogResponse>> getByJobId(
            @PathVariable String jobId) {
        return ResponseEntity.ok(syncLogService.getByJobId(jobId));
    }

    @GetMapping("/job/{jobId}/total-records")
    @Operation(summary = "Job toplam işlenen kayıt sayısı")
    public ResponseEntity<Long> getTotalRecords(@PathVariable String jobId) {
        return ResponseEntity.ok(syncLogService.getTotalRecordsProcessed(jobId));
    }
}