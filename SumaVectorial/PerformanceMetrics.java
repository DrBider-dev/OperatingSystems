package SumaVectorial;

/**
 * PerformanceMetrics
 * -------------------
 * Calcula las métricas de rendimiento formales exigidas por la guía:
 *      Aceleración (Speedup):  Sn = T1 / Tn
 *      Eficiencia:              En = Sn / n
 *
 * donde T1 es el tiempo de la versión secuencial (línea base) y Tn el
 * tiempo de la versión paralela con n hilos. Estos valores son el
 * insumo directo para la sección de "Discusión y Conclusiones" del
 * informe técnico (overhead, punto de saturación, Ley de Amdahl).
 */
public class PerformanceMetrics {

    public static double speedup(long tSequential, long tParallel) {
        if (tParallel <= 0) {
            return 0.0;
        }
        return (double) tSequential / (double) tParallel;
    }

    public static double efficiency(double speedup, int numThreads) {
        if (numThreads <= 0) {
            return 0.0;
        }
        return speedup / numThreads;
    }
}
