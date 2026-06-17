package com.mogmojito.logautomate.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "analysis_history")
@Getter
@Setter
public class AnalysisHistory {

    public AnalysisHistory() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "max_severity", nullable = false, length = 20)
    private String maxSeverity;

    @Lob
    @Column(name = "raw_log", nullable = false, columnDefinition = "LONGTEXT")
    private String rawLog;

    @Lob
    @Column(name = "ai_report", nullable = false, columnDefinition = "LONGTEXT")
    private String aiReport;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // 🛠️ Constructor manual clásico para evitar usar la magia de @Builder
    public AnalysisHistory(String fileName, String maxSeverity, String rawLog, String aiReport) {
        this.fileName = fileName;
        this.maxSeverity = maxSeverity;
        this.rawLog = rawLog;
        this.aiReport = aiReport;
    }
}