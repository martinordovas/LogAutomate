# 🤖 LogAutomate — IA Log Analyzer & Diagnostic Tool

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-v4.0-06B6D4?style=for-the-badge&logo=tailwind-css&logoColor=white)](https://tailwindcss.com/)
[![JavaScript](https://img.shields.io/badge/JavaScript-ES6%2B-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)](https://developer.mozilla.org/es/docs/Web/JavaScript)
[![Database](https://img.shields.io/badge/MySQL%20%2F%20PostgreSQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)]()

**LogAutomate** es una aplicación web full-stack diseñada para centralizar, automatizar y diagnosticar errores críticos en archivos de log (`.log`) utilizando modelos de Inteligencia Artificial de última generación mediante **Spring AI**. 

Olvídate de revisar miles de líneas manualmente. Arrastra tu archivo de log y deja que la IA clasifique la severidad del error (`CLEAN`, `WARNING`, `SEVERE`), extraiga el *stacktrace* relevante y te ofrezca una solución o diagnóstico detallado en segundos.

---

## ✨ Características Principales

* **📁 Interfaz Drag & Drop Intuitiva:** Sube tus archivos `.log` arrastrándolos directamente a la zona de carga interactiva.
* **🧠 Diagnóstico Inteligente (Spring AI):** Integración nativa con LLMs para interpretar excepciones, errores del sistema y sugerir correcciones automáticas.
* **📊 Historial Persistente e Interactivo:** Barra lateral izquierda que indexa y ordena de forma cronológica los análisis anteriores almacenados en base de datos.
* **🗑️ Gestión de Registros Completa:** Interfaz dividida en el historial para visualizar detalles o eliminar registros permanentemente mediante un flujo seguro con **modal de confirmación estéticamente integrado**.
* **📱 Diseño 100% Responsivo y Fluido:** Maquetación moderna con estética *Dark Mode*, optimizada con utilidades avanzadas de Flexbox para evitar bloqueos de escalado en pantallas móviles o tablets.

---

## 🛠️ Stack Tecnológico

### Backend
* **Java 17** como lenguaje principal.
* **Spring Boot 3.x** (Spring Web, Spring Data JPA).
* **Spring AI** para la orquestación y comunicación fluida con modelos de lenguaje masivos (LLMs).
* **Lombok** para mantener el código limpio y libre de boilerplate.

### Frontend
* **HTML5 / Thymeleaf** para el renderizado dinámico en servidor.
* **Tailwind CSS v4.0** (vía CDN nativo) para un diseño de componentes moderno, estilizado y efectos visuales de desenfoque (`backdrop-blur`).
* **JavaScript Vanilla (ES6+)** para el manejo asíncrono de peticiones (`fetch`, `async/await`), eventos Drag & Drop y manipulación dinámica del DOM.

### Persistencia / Infraestructura
* **Relational DB:** Compatible con MySQL, MariaDB o PostgreSQL.
* **Hibernate / JPA** para el mapeo y la abstracción de consultas.

---

## 📸 Vista de la Aplicación

*(Opcional: Añade aquí una captura de pantalla o GIF de tu aplicación en acción)*
```img
[Inserta aquí tu captura: p.ej. [https://github.com/tu-usuario/LogAutomate/raw/main/screenshot.png](https://github.com/tu-usuario/LogAutomate/raw/main/screenshot.png)]
