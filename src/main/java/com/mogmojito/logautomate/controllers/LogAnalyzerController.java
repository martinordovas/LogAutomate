package com.mogmojito.logautomate.controllers;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mogmojito.logautomate.models.AnalysisHistory;
import com.mogmojito.logautomate.repos.AnalysisHistoryRepo;
import com.mogmojito.logautomate.service.LogAnalyzerService;

@RestController
@RequestMapping("/api/v1/analyzer")
public class LogAnalyzerController {

    private final LogAnalyzerService logAnalyzerService;

    public LogAnalyzerController(LogAnalyzerService logAnalyzerService) {
        this.logAnalyzerService = logAnalyzerService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadLog(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("## ❌ Error\nPor favor, selecciona un archivo válido.");
        }
        return ResponseEntity.ok(logAnalyzerService.analyzeLogFile(file));
    }

    // 📋 Endpoint para rellenar la barra lateral al cargar la app
    @GetMapping("/history")
    public ResponseEntity<List<AnalysisHistoryRepo.SummaryProjection>> getHistory() {
        return ResponseEntity.ok(logAnalyzerService.getHistoryList());
    }

    // 🔍 Endpoint para recuperar un análisis pasado completo
    @GetMapping("/history/{id}")
    public ResponseEntity<AnalysisHistory> getHistoryDetails(@PathVariable Long id) {
        return logAnalyzerService.getAnalysisDetails(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🗑️ Endpoint para eliminar un análisis del historial
    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id) {
        logAnalyzerService.deleteAnalysis(id);
        return ResponseEntity.noContent().build(); // Devuelve un 204 No Content ideal para borrados
    }
}