package com.mogmojito.logautomate.repos;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mogmojito.logautomate.models.AnalysisHistory;

public interface AnalysisHistoryRepo extends JpaRepository<AnalysisHistory, Long>{

    interface SummaryProjection {
        Long getId();
        String getFileName();
        String getMaxSeverity();
        LocalDateTime getCreatedAt();
    }
    
    List<SummaryProjection> findAllProjectedByOrderByCreatedAtDesc();    
}
