package SumaVectorial;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * BenchmarkRunner
 * ---------------
 * Herramienta AUXILIAR (complementa a Main, no lo reemplaza) que corre
 * automáticamente la comparación exigida en el informe técnico:
 * la Versión 2 (paralela) evaluada para n ∈ {1, 2, 4, 8, 16} hilos,
 * usando siempre el mismo vector y el mismo T1 (tiempo secuencial) como
 * línea base.
 *
 * Imprime la tabla de tiempos en consola y además exporta un archivo
 * CSV (tiempo_ms, suma, speedup, eficiencia por cada n) listo para
 * importar en Excel/Sheets y construir la "Gráfica de Tiempo de
 * Ejecución vs. Número de Hilos" que pide la guía.
 *
 * Uso:
 *   java vectorsum.BenchmarkRunner [N] [rutaSalidaCsv]
 *
 * Ejemplo:
 *   java vectorsum.BenchmarkRunner 10000000 resultados_actividad2.csv
 */
public class BenchmarkRunner {

    private static final int[] THREAD_COUNTS = {1, 2, 4, 8, 16};

    public static void main(String[] args) throws InterruptedException, IOException {
        int n = args.length > 0 ? Integer.parseInt(args[0]) : 10_000_000;
        String csvPath = args.length > 1 ? args[1] : "resultados_actividad2.csv";

        System.out.println("Generando vector de " + n + " elementos aleatorios...");
        int[] array = VectorGenerator.generate(n);

        System.out.println();
        System.out.println("=== VERSIÓN 1: SUMA SECUENCIAL (línea base T1) ===");
        SumResult sequentialResult = SequentialSumCalculator.compute(array);
        long t1 = sequentialResult.getExecutionTimeMillis();
        System.out.println("Suma: " + sequentialResult.getSum() + " | T1 = " + t1 + " ms");
        System.out.println();

        System.out.println("=== VERSIÓN 2: SUMA PARALELA, variando n ∈ {1, 2, 4, 8, 16} ===");
        System.out.printf("%-8s %-14s %-20s %-12s %-12s%n", "n", "Tiempo(ms)", "Suma", "Speedup", "Eficiencia");
        System.out.println("---------------------------------------------------------------------");

        List<String[]> csvRows = new ArrayList<>();
        csvRows.add(new String[]{"n_hilos", "tiempo_ms", "suma", "speedup", "eficiencia"});

        for (int threads : THREAD_COUNTS) {
            SumResult r = ParallelSumCalculator.compute(array, threads);
            double speedup = PerformanceMetrics.speedup(t1, r.getExecutionTimeMillis());
            double efficiency = PerformanceMetrics.efficiency(speedup, threads);

            System.out.printf("%-8d %-14d %-20d %-12.4f %-12.4f%n",
                    threads, r.getExecutionTimeMillis(), r.getSum(), speedup, efficiency);

            csvRows.add(new String[]{
                    String.valueOf(threads),
                    String.valueOf(r.getExecutionTimeMillis()),
                    String.valueOf(r.getSum()),
                    String.format(Locale.US, "%.4f", speedup),
                    String.format(Locale.US, "%.4f", efficiency)
            });
        }

        writeCsv(csvPath, csvRows);
        System.out.println();
        System.out.println("Resultados exportados a: " + csvPath);
    }

    private static void writeCsv(String path, List<String[]> rows) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (String[] row : rows) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        }
    }
}
