package HistorialChat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DataParallelismAnalyzer
 * -----------------------
 * ESTRATEGIA B: Paralelismo de Datos (Data Parallelism).
 *
 * El documento se divide en N bloques de tamaño equivalente (por defecto 3,
 * según la guía, aunque el método admite un número de hilos parametrizable
 * para reutilizarse también en la Actividad 2). Se crean N hilos
 * trabajadores; cada hilo ejecuta las TRES tareas (mensaje más largo, día
 * más activo, contacto más frecuente) pero SOLO sobre el bloque que le fue
 * asignado, produciendo resultados parciales/locales.
 *
 * El hilo principal realiza la REDUCCIÓN: combina los máximos y conteos
 * locales de cada bloque en los máximos y conteos globales finales.
 *
 * Nota sobre sincronización: al igual que en la Estrategia A, cada
 * BlockWorker escribe exclusivamente en sus propias estructuras locales
 * (no hay memoria compartida entre hilos durante el procesamiento), por lo
 * que no se necesitan locks. La reducción ocurre después de los join(),
 * ya en el hilo principal, de forma secuencial y segura.
 */
public class DataParallelismAnalyzer {

    /** Sobrecarga con el número de hilos por defecto (3), como pide la Actividad 1. */
    public static AnalysisResult analyze(List<ChatMessage> messages) throws InterruptedException {
        return analyze(messages, 3);
    }

    public static AnalysisResult analyze(List<ChatMessage> messages, int numThreads) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        int n = messages.size();
        int blockSize = (int) Math.ceil((double) n / numThreads);

        BlockWorker[] workers = new BlockWorker[numThreads];
        Thread[] threads = new Thread[numThreads];

        // División del arreglo en bloques equivalentes (partición de datos)
        for (int i = 0; i < numThreads; i++) {
            int from = Math.min(i * blockSize, n);
            int to = Math.min(from + blockSize, n);
            List<ChatMessage> block = messages.subList(from, to);

            workers[i] = new BlockWorker(block);
            threads[i] = new Thread(workers[i], "Hilo-Bloque-" + (i + 1));
        }

        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        // --- Reducción: el hilo principal combina los resultados parciales ---
        LocalDate globalLongestDate = null;
        int globalLongestLength = -1;
        Map<DayOfWeek, Long> globalDayCounts = new EnumMap<>(DayOfWeek.class);
        Map<String, Long> globalContactCounts = new HashMap<>();

        for (BlockWorker w : workers) {
            // Reducción de máximos locales -> máximo global (mensaje más largo)
            if (w.getLocalLongestLength() > globalLongestLength) {
                globalLongestLength = w.getLocalLongestLength();
                globalLongestDate = w.getLocalLongestDate();
            }
            // Suma de conteos parciales por día
            for (Map.Entry<DayOfWeek, Long> e : w.getLocalDayCounts().entrySet()) {
                globalDayCounts.merge(e.getKey(), e.getValue(), Long::sum);
            }
            // Suma de conteos parciales por contacto
            for (Map.Entry<String, Long> e : w.getLocalContactCounts().entrySet()) {
                globalContactCounts.merge(e.getKey(), e.getValue(), Long::sum);
            }
        }

        DayOfWeek globalBusiestDay = null;
        long globalBusiestCount = 0;
        for (Map.Entry<DayOfWeek, Long> e : globalDayCounts.entrySet()) {
            if (e.getValue() > globalBusiestCount) {
                globalBusiestCount = e.getValue();
                globalBusiestDay = e.getKey();
            }
        }

        String globalFrequentContact = null;
        long globalFrequentCount = 0;
        for (Map.Entry<String, Long> e : globalContactCounts.entrySet()) {
            if (e.getValue() > globalFrequentCount) {
                globalFrequentCount = e.getValue();
                globalFrequentContact = e.getKey();
            }
        }

        long endTime = System.currentTimeMillis();

        AnalysisResult result = new AnalysisResult();
        result.setLongestMessageDate(globalLongestDate);
        result.setLongestMessageLength(globalLongestLength);
        result.setBusiestDay(globalBusiestDay);
        result.setBusiestDayCount(globalBusiestCount);
        result.setMostFrequentContact(globalFrequentContact);
        result.setMostFrequentContactCount(globalFrequentCount);
        result.setExecutionTimeMillis(endTime - startTime);

        return result;
    }

    /**
     * Hilo trabajador de la Estrategia B: ejecuta las TRES tareas
     * (mensaje más largo, día más activo, contacto más frecuente)
     * pero únicamente sobre el bloque/tercio de datos que se le asignó.
     */
    private static class BlockWorker implements Runnable {
        private final List<ChatMessage> block;

        private LocalDate localLongestDate;
        private int localLongestLength = -1;
        private final Map<DayOfWeek, Long> localDayCounts = new EnumMap<>(DayOfWeek.class);
        private final Map<String, Long> localContactCounts = new HashMap<>();

        BlockWorker(List<ChatMessage> block) {
            this.block = block;
        }

        @Override
        public void run() {
            for (ChatMessage msg : block) {
                // Tarea 1: mensaje más largo, a nivel local del bloque
                if (msg.getLength() > localLongestLength) {
                    localLongestLength = msg.getLength();
                    localLongestDate = msg.getDate();
                }
                // Tarea 2: conteo de mensajes por día de la semana, a nivel local
                localDayCounts.merge(msg.getDayOfWeek(), 1L, Long::sum);
                // Tarea 3: conteo de mensajes por contacto, a nivel local
                localContactCounts.merge(msg.getContact(), 1L, Long::sum);
            }
        }

        LocalDate getLocalLongestDate() {
            return localLongestDate;
        }

        int getLocalLongestLength() {
            return localLongestLength;
        }

        Map<DayOfWeek, Long> getLocalDayCounts() {
            return localDayCounts;
        }

        Map<String, Long> getLocalContactCounts() {
            return localContactCounts;
        }
    }
}
