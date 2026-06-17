const sidebar = document.getElementById('sidebar');
const btnMenu = document.getElementById('btnMenu');
const sidebarOverlay = document.getElementById('sidebarOverlay');

const historyList = document.getElementById('historyList');
const dropzone = document.getElementById('dropzone');
const fileInput = document.getElementById('fileInput');
const btnAnalyze = document.getElementById('btnAnalyze');
const fileNameDisplay = document.getElementById('fileNameDisplay');

const resultContainer = document.getElementById('resultContainer');
const output = document.getElementById('output');
const btnCloseAnalysis = document.getElementById('btnCloseAnalysis');

// --- NUEVOS ELEMENTOS PARA EL CONTROL DE BORRADO ---
const deleteModal = document.getElementById('deleteModal');
const btnCancelDelete = document.getElementById('btnCancelDelete');
const btnConfirmDelete = document.getElementById('btnConfirmDelete');

let logIdToDelete = null;       // Guarda temporalmente el ID que se va a borrar
let currentAnalysisId = null;   // Guarda el ID del análisis que se está visualizando en pantalla

// --- DISPARAR CARGA DEL HISTORIAL AL INICIAR ---
document.addEventListener('DOMContentLoaded', fetchHistory);

// --- MENÚ HAMBURGUESA ---
function toggleSidebar() {
    const isHidden = sidebar.classList.contains('-translate-x-full');
    if (isHidden) {
        sidebar.classList.remove('-translate-x-full');
        sidebarOverlay.classList.remove('hidden');
    } else {
        sidebar.classList.add('-translate-x-full');
        sidebarOverlay.classList.add('hidden');
    }
}

btnMenu.addEventListener('click', toggleSidebar);
sidebarOverlay.addEventListener('click', toggleSidebar);


// --- CONSULTA A LA BD: CARGAR LISTA DEL HISTORIAL (CON DISEÑO DIVIDIDO) ---
async function fetchHistory() {
    try {
        const response = await fetch('/api/v1/analyzer/history');
        if (!response.ok) throw new Error('Error al traer historial');

        const logs = await response.json();
        historyList.innerHTML = '';

        if (logs.length === 0) {
            historyList.innerHTML = '<p class="text-xs text-zinc-600 text-center py-4">No hay análisis guardados</p>';
            return;
        }

        logs.forEach(log => {
            const badgeType = log.status === 'ERROR' ? 'bg-red-500/10 text-red-400 border-red-500/20' : 'bg-lime-500/10 text-lime-400 border-lime-500/20';

            // Contenedor de la fila (Dividido)
            const rowWrapper = document.createElement('div');
            rowWrapper.className = "w-full flex items-stretch gap-1.5 group/row mb-1.5";

            // BOTÓN IZQUIERDO: Ver detalles del log
            const logButton = document.createElement('button');
            logButton.className = "flex-1 text-left p-3 rounded-xl bg-zinc-800/20 hover:bg-zinc-800/60 border border-zinc-800/50 hover:border-zinc-700/80 transition-all cursor-pointer flex flex-col gap-1 min-w-0";
            logButton.innerHTML = `
                <span class="text-sm font-medium text-zinc-300 group-hover/row:text-lime-400 transition-colors truncate w-full">${log.fileName}</span>
                <div class="flex items-center justify-between w-full text-[11px] text-zinc-500">
                    <span>${log.formattedDate || 'Reciente'}</span>
                    <span class="px-1.5 py-0.5 rounded border font-mono font-bold ${badgeType}">${log.status || 'LOG'}</span>
                </div>
            `;
            logButton.addEventListener('click', () => {
                fetchSingleAnalysis(log.id);
                if (!sidebar.classList.contains('-translate-x-full')) {
                    toggleSidebar();
                }
            });

            // BOTÓN DERECHO: Eliminar log
            const deleteButton = document.createElement('button');
            deleteButton.className = "px-3 rounded-xl bg-zinc-800/10 hover:bg-red-500/10 border border-zinc-800/40 hover:border-red-500/30 text-zinc-600 hover:text-red-400 transition-all cursor-pointer flex items-center justify-center flex-shrink-0" + 
                                     (currentAnalysisId === log.id ? " border-red-500/20" : "");
            deleteButton.title = "Eliminar este análisis";
            deleteButton.innerHTML = `
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                </svg>
            `;
            deleteButton.addEventListener('click', (e) => {
                e.stopPropagation(); // Evita que se dispare el click del botón contenedor
                openDeleteModal(log.id);
            });

            rowWrapper.appendChild(logButton);
            rowWrapper.appendChild(deleteButton);
            historyList.appendChild(rowWrapper);
        });
    } catch (error) {
        historyList.innerHTML = '<p class="text-xs text-red-400/70 text-center py-4">Error al cargar historial</p>';
        console.error(error);
    }
}

// --- CONSULTA A LA BD: TRAER UN ANÁLISIS ESPECÍFICO ---
async function fetchSingleAnalysis(id) {
    try {
        const response = await fetch(`/api/v1/analyzer/history/${id}`);
        if (!response.ok) throw new Error('No se pudo leer el análisis');

        const rawText = await response.text();
        let detailedText = rawText;

        try {
            const json = JSON.parse(rawText);

            if (typeof json === 'object' && json !== null) {
                if (Object.keys(json).length === 0) {
                    detailedText = "⚠️ El backend devolvió un objeto vacío {}.";
                } else {
                    detailedText = json.aiReport || json.analysis || json.result || JSON.stringify(json, null, 2);
                }
            }
        } catch (e) {
            // Si no es un JSON válido, ya era texto plano
        }

        currentAnalysisId = id; // Guardamos cuál estamos visualizando actualmente
        resultContainer.classList.remove('hidden');
        btnCloseAnalysis.classList.remove('hidden');
        output.textContent = detailedText;
    } catch (error) {
        alert('No se pudo recuperar el análisis: ' + error.message);
    }
}


// --- GESTIÓN DEL MODAL DE BORRADO ---
function openDeleteModal(id) {
    logIdToDelete = id;
    deleteModal.classList.remove('hidden');
}

function closeDeleteModal() {
    logIdToDelete = null;
    deleteModal.classList.add('hidden');
}

btnCancelDelete.addEventListener('click', closeDeleteModal);

btnConfirmDelete.addEventListener('click', async () => {
    if (!logIdToDelete) return;

    try {
        const response = await fetch(`/api/v1/analyzer/history/${logIdToDelete}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('No se ha podido eliminar el registro del servidor');

        // GESTIÓN DE UX EXTRA: Si el log borrado es el que el usuario tiene abierto en pantalla, lo limpiamos de la vista
        if (currentAnalysisId === logIdToDelete) {
            resetMainView();
        }

        closeDeleteModal();
        fetchHistory(); // Recargar la lista lateral actualizada

    } catch (error) {
        alert('Error al borrar: ' + error.message);
    }
});


// --- SOPORTE REAL DRAG & DROP + CLICK ---
dropzone.addEventListener('click', () => fileInput.click());

['dragover', 'dragenter'].forEach(eventName => {
    dropzone.addEventListener(eventName, (e) => {
        e.preventDefault();
        dropzone.classList.add('border-lime-500/50', 'bg-zinc-900/40');
    });
});

['dragleave', 'drop'].forEach(eventName => {
    dropzone.addEventListener(eventName, (e) => {
        e.preventDefault();
        dropzone.classList.remove('border-lime-500/50', 'bg-zinc-900/40');
    });
});

dropzone.addEventListener('drop', (e) => {
    const dt = e.dataTransfer;
    const files = dt.files;
    if (files.length > 0) {
        fileInput.files = files;
        fileNameDisplay.textContent = files[0].name;
        btnAnalyze.disabled = false;
    }
});

fileInput.addEventListener('change', (e) => {
    if (e.target.files.length > 0) {
        fileNameDisplay.textContent = e.target.files[0].name;
        btnAnalyze.disabled = false;
    }
});


// --- CONTROLADOR AJAX (PROCESAR NUEVO LOG) ---
btnAnalyze.addEventListener('click', async () => {
    const file = fileInput.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    btnAnalyze.disabled = true;
    btnAnalyze.textContent = 'Analizando con IA...';

    try {
        const response = await fetch('/api/v1/analyzer/upload', {
            method: 'POST',
            body: formData
        });

        const data = await response.text();

        currentAnalysisId = null; // Como es uno nuevo recién subido y no guardado por ID directo en sesión, reseteamos el tracking
        resultContainer.classList.remove('hidden');
        btnCloseAnalysis.classList.remove('hidden');
        output.textContent = data;

        fetchHistory();

    } catch (error) {
        alert('Error en el servidor: ' + error.message);
    } finally {
        btnAnalyze.disabled = false;
        btnAnalyze.textContent = 'Analizar Log';
    }
});


// --- FUNCIÓN CENTRALIZADA PARA LIMPIAR PANTALLA ---
function resetMainView() {
    resultContainer.classList.add('hidden');
    btnCloseAnalysis.classList.add('hidden');
    output.textContent = '';

    fileInput.value = '';
    fileNameDisplay.textContent = 'Haz clic para seleccionar o arrastra un archivo .log';
    btnAnalyze.disabled = true;
    currentAnalysisId = null;
}

// BOTÓN CERRAR VISTA TRADICIONAL
btnCloseAnalysis.addEventListener('click', resetMainView);