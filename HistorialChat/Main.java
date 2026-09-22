package HistorialChat;

import java.io.IOException;
import java.util.List;

/**
 * Main
 * ----
 * Punto de entrada del programa. Coordina el flujo completo de la
 * Actividad 1:
 *   1. Lee y parsea el archivo de historial de chat (hilo principal).
 *   2. Ejecuta la Estrategia A (Paralelismo de Tareas).
 *   3. Ejecuta la Estrategia B (Paralelismo de Datos).
 *   4. Muestra los resultados de ambas y una comparativa de tiempos,
 *      insumo directo para la tabla y el análisis pedidos en el informe
 *      técnico (Speedup, Eficiencia, discusión, etc.).
 *
 * Uso:
 *   java chatanalysis.Main <ruta_archivo_chat> [numHilosEstrategiaB]
 *
 * Ejemplo:
 *   java chatanalysis.Main chat_sample.txt 3
 */
public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java chatanalysis.Main <ruta_archivo_chat> [numHilosEstrategiaB]");
            System.out.println("Ejemplo: java chatanalysis.Main chat_sample.txt 3");
            return;
        }

        String filePath = args[0];
        int numThreadsB = args.length > 1 ? Integer.parseInt(args[1]) : 3;

        try {
            System.out.println("Cargando mensajes desde: " + filePath);
            List<ChatMessage> messages = ChatFileReader.readMessages(filePath);
            System.out.println("Total de mensajes cargados: " + messages.size());
            System.out.println();

            // --- Estrategia A: Paralelismo de Tareas ---
            System.out.println("=== ESTRATEGIA A: Paralelismo de Tareas (3 hilos, 1 tarea c/u) ===");
            AnalysisResult resultA = TaskParallelismAnalyzer.analyze(messages);
            printResult(resultA);
            System.out.println();

            // --- Estrategia B: Paralelismo de Datos ---
            System.out.println("=== ESTRATEGIA B: Paralelismo de Datos (" + numThreadsB
                    + " hilos, 3 tareas c/u sobre su bloque) ===");
            AnalysisResult resultB = DataParallelismAnalyzer.analyze(messages, numThreadsB);
            printResult(resultB);
            System.out.println();

            // --- Comparativa de tiempos (para la tabla del informe técnico) ---
            System.out.println("=== COMPARATIVA DE TIEMPOS ===");
            System.out.printf("Estrategia A (Paralelismo de Tareas): %d ms%n", resultA.getExecutionTimeMillis());
            System.out.printf("Estrategia B (Paralelismo de Datos):  %d ms%n", resultB.getExecutionTimeMillis());

            // Verificación de consistencia: ambas estrategias deben llegar al mismo resultado
            boolean consistente =
                    resultA.getLongestMessageDate().equals(resultB.getLongestMessageDate())
                    && resultA.getBusiestDay().equals(resultB.getBusiestDay())
                    && resultA.getMostFrequentContact().equals(resultB.getMostFrequentContact());
            System.out.println("¿Resultados A y B coinciden? " + (consistente ? "SÍ" : "NO"));

        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Ejecución interrumpida: " + e.getMessage());
        }
    }

    private static void printResult(AnalysisResult r) {
        System.out.println("Fecha con mensaje más largo: " + r.getLongestMessageDate()
                + " (" + r.getLongestMessageLength() + " caracteres)");
        System.out.println("Día de mayor actividad: " + r.getBusiestDay()
                + " (" + r.getBusiestDayCount() + " mensajes)");
        System.out.println("Contacto más frecuente: " + r.getMostFrequentContact()
                + " (" + r.getMostFrequentContactCount() + " mensajes)");
        System.out.println("Tiempo de ejecución: " + r.getExecutionTimeMillis() + " ms");
    }
}
