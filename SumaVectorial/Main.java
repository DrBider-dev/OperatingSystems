package SumaVectorial;

import java.util.Scanner;

/**
 * Main
 * ----
 * Punto de entrada INTERACTIVO de la Actividad 2. Tal como pide la guía,
 * el programa le solicita al usuario el tamaño N del vector y el número
 * n de hilos a utilizar, y luego ejecuta y compara:
 *      Versión 1: suma secuencial (monohilo)
 *      Versión 2: suma paralela   (multihilo, con el n ingresado)
 *
 * mostrando tiempos, verificación de correctitud (ambas sumas deben
 * coincidir) y las métricas de Speedup y Eficiencia.
 *
 * Para generar automáticamente la tabla completa exigida en el informe
 * (variando n ∈ {1, 2, 4, 8, 16} en una sola corrida), usar en cambio
 * la clase BenchmarkRunner de este mismo paquete.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el tamaño N del vector (N >= 10,000,000): ");
        int n = Integer.parseInt(scanner.nextLine().trim());
        if (n < 10_000_000) {
            System.out.println("Aviso: la guía exige N >= 10,000,000. Se ajustará N a 10,000,000.");
            n = 10_000_000;
        }

        System.out.print("Ingrese el número de hilos (n) para la versión paralela: ");
        int numThreads = Integer.parseInt(scanner.nextLine().trim());
        if (numThreads < 1) {
            System.out.println("Número de hilos inválido, se usará 1.");
            numThreads = 1;
        }

        System.out.println();
        System.out.println("Generando vector de " + n + " elementos aleatorios...");
        int[] array = VectorGenerator.generate(n);

        System.out.println();
        System.out.println("=== VERSIÓN 1: SUMA SECUENCIAL (Monohilo) ===");
        SumResult sequentialResult = SequentialSumCalculator.compute(array);
        printResult(sequentialResult);

        System.out.println();
        System.out.println("=== VERSIÓN 2: SUMA PARALELA (Multihilo, n = " + numThreads + ") ===");
        SumResult parallelResult = ParallelSumCalculator.compute(array, numThreads);
        printResult(parallelResult);

        System.out.println();
        boolean correcto = sequentialResult.getSum() == parallelResult.getSum();
        System.out.println("¿Las sumas coinciden (verificación de correctitud)? " + (correcto ? "SÍ" : "NO"));

        double speedup = PerformanceMetrics.speedup(
                sequentialResult.getExecutionTimeMillis(), parallelResult.getExecutionTimeMillis());
        double efficiency = PerformanceMetrics.efficiency(speedup, numThreads);

        System.out.println();
        System.out.println("=== MÉTRICAS DE RENDIMIENTO ===");
        System.out.printf("Tiempo secuencial (T1):        %d ms%n", sequentialResult.getExecutionTimeMillis());
        System.out.printf("Tiempo paralelo (Tn), n=%d:     %d ms%n", numThreads, parallelResult.getExecutionTimeMillis());
        System.out.printf("Speedup (Sn = T1 / Tn):         %.4f%n", speedup);
        System.out.printf("Eficiencia (En = Sn / n):       %.4f%n", efficiency);

        scanner.close();
    }

    private static void printResult(SumResult r) {
        System.out.println("Suma total: " + r.getSum());
        System.out.println("Tiempo de ejecución: " + r.getExecutionTimeMillis() + " ms");
    }
}
