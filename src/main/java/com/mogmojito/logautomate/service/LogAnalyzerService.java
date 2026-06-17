package com.mogmojito.logautomate.service;

import com.mogmojito.logautomate.models.AnalysisHistory;
import com.mogmojito.logautomate.repos.AnalysisHistoryRepo;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogAnalyzerService {

    private final ChatModel chatModel;
    private final AnalysisHistoryRepo historyRepository; // 👈 Inyección del repositorio

    // Spring inyecta ambos Beans automáticamente por constructor
    public LogAnalyzerService(ChatModel chatModel, AnalysisHistoryRepo historyRepository) {
        this.chatModel = chatModel;
        this.historyRepository = historyRepository;
    }

    public String analyzeLogFile(MultipartFile file) {
        try {
            // 1. Leer el archivo de log
            String logContent = new BufferedReader(new InputStreamReader(file.getInputStream()))
                    .lines()
                    .collect(Collectors.joining("\n"));

            if (logContent.trim().isEmpty()) {
                return "## ❌ Error\nEl archivo de log proporcionado está completamente vacío.";
            }

            // 2. Montar las instrucciones del sistema para la IA
            String systemPrompt = """
                    Eres un Ingeniero DevOps Senior y experto en administración de sistemas Linux.
                    Tu tarea es analizar detalladamente el volcado de log que te proporcionará el usuario.
                    
                    Debes identificar de forma inteligente un máximo de 3 incidencias críticas y clasificarlas estrictamente en uno de estos dos tipos:
                    - SEVERE: Para caídas de bases de datos, fallos de almacenamiento (como falta de espacio), bloqueos de puertos o errores catastróficos que rompan el servicio.
                    - WARNING: Para problemas de confianza en certificados SSL, excepciones controladas de la aplicación o alertas de red secundarias.
                    
                    Estructura el reporte estrictamente con el siguiente formato Markdown para cada incidencia detectada:
                    
                    ### [🔴 SEVERE] Título conciso de la incidencia
                    - **Causa:** Explicación muy breve de qué ha provocado este fallo en el sistema.
                    - **Solución:** Pasos recomendados para solucionarlo.
                    - **Comando / Configuración:**
                    ```bash
                    # Comando exacto de terminal o línea a modificar
                    ```
                    
                    REGLAS CRÍTICAS DE FORMATO:
                    1. Si es una alerta de tipo Warning, usa obligatoriamente el tag '### [🟡 WARNING] Título'.
                    2. PROHIBIDO: No envuelvas toda tu respuesta dentro de un bloque de código raíz (no pongas ```markdown al principio del documento ni ``` al final). Empieza directamente con el primer encabezado '###'.
                    3. Si el log está limpio y no contiene fallos, genera un único encabezado '### ✅ Sistema Estable' detallando que todo marcha perfectamente.
                    
                    Aquí tienes el log a analizar:
                    """ + logContent;

            // 3. Llamada al modelo de IA
            String aiReport = this.chatModel.call(systemPrompt);

            // 4. 🧠 PARSEO INTELIGENTE DE SEVERIDAD
            // Analizamos el texto devuelto por la IA para clasificar el registro en base de datos
            String maxSeverity = "CLEAN";
            if (aiReport.contains("[🔴 SEVERE]")) {
                maxSeverity = "SEVERE";
            } else if (aiReport.contains("[🟡 WARNING]")) {
                maxSeverity = "WARNING";
            }

            // 5. 💾 PERSISTENCIA EN MARIADB CON JPA
            String originalFileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown.log";
            
            // Usamos el constructor que acabamos de definir arriba
            AnalysisHistory historyEntry = new AnalysisHistory(
                    originalFileName,
                    maxSeverity,
                    logContent,
                    aiReport
            );

            // Guardamos el objeto en la base de datos de forma asíncrona/directa antes del return
            historyRepository.save(historyEntry);

            // 6. Devolver el reporte al controlador para pintar en el frontend
            return aiReport;

        } catch (Exception e) {
            return "## ❌ Error Interno\nNo se pudo procesar el archivo debido a: " + e.getMessage();
        }
    }
    public List<AnalysisHistoryRepo.SummaryProjection> getHistoryList() {
        return historyRepository.findAllProjectedByOrderByCreatedAtDesc();
    }

    public java.util.Optional<AnalysisHistory> getAnalysisDetails(Long id) {
        return historyRepository.findById(id);
    }

    public void deleteAnalysis(Long id) {
        historyRepository.deleteById(id);
    }
}