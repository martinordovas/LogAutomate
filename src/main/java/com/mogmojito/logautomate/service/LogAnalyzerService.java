package com.mogmojito.logautomate.service;

import org.springframework.ai.chat.model.ChatModel; // Import oficial de la versión estable
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Service
public class LogAnalyzerService {

    private final ChatModel chatModel; // Cambiado a ChatModel

    // Inyección limpia por constructor
    public LogAnalyzerService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String analyzeLogFile(MultipartFile file) {
        try {
            // Leemos el contenido del archivo
            String logContent = new BufferedReader(new InputStreamReader(file.getInputStream()))
                    .lines()
                    .collect(Collectors.joining("\n"));

            if (logContent.trim().isEmpty()) {
                return "## ❌ Error\nEl archivo de log proporcionado está completamente vacío.";
            }

            // Concatenamos el rol de sistema y el log en un solo prompt directo
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
                    """
                    + logContent;

            // ChatModel mantiene el método directo .call() que necesitas
            return this.chatModel.call(systemPrompt);

        } catch (Exception e) {
            return "## ❌ Error Interno\nNo se pudo procesar el archivo debido a: " + e.getMessage();
        }
    }
}