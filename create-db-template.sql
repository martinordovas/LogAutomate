-- 1. Crear la base de datos con codificación UTF-8 para almacenar bien los emojis y markdown
CREATE DATABASE logautomate CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. Crear el usuario de la aplicación (cambia 'tu_password_segura' por la que quieras)
CREATE USER 'dev_user'@'localhost' IDENTIFIED BY 'tu_password_segura';
ALTER USER 'dev_user'@'localhost' IDENTIFIED BY 'logautomate2026';
FLUSH PRIVILEGES;

-- 3. Darle permisos absolutos sobre la base de datos del proyecto
GRANT ALL PRIVILEGES ON logautomate.* TO 'dev_user'@'localhost';

-- 4. Refrescar privilegios y salir
FLUSH PRIVILEGES;

use logautomate;

CREATE TABLE analysis_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    max_severity VARCHAR(20) NOT NULL, -- Guardará: 'CLEAN', 'WARNING' o 'SEVERE'
    raw_log LONGTEXT NOT NULL,          -- El contenido completo del log que subió el usuario
    ai_report LONGTEXT NOT NULL,        -- El Markdown generado por la IA
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

select * from analysis_history;