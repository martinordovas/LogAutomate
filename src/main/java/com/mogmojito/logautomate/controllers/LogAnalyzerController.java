package com.mogmojito.logautomate.controllers;

import com.mogmojito.logautomate.service.LogAnalyzerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/analyzer")
public class LogAnalyzerController {

    private final LogAnalyzerService logAnalyzerService;

    // Inyección por constructor (Buenas prácticas de Spring)
    public LogAnalyzerController(LogAnalyzerService logAnalyzerService) {
        this.logAnalyzerService = logAnalyzerService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadLog(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("## ❌ Error\nPor favor, selecciona un archivo válido.");
        }

        // Llamamos al servicio real con IA
        String report = logAnalyzerService.analyzeLogFile(file);
        return ResponseEntity.ok(report);
    }
}