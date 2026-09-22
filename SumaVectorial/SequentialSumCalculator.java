package SumaVectorial;

/**
 * SequentialSumCalculator
 * -----------------------
 * VERSIÓN 1 (Monohilo / Secuencial) de la Actividad 2.
 *
 * Un único hilo recorre el arreglo completo, de la posición 0 a N-1,
 * acumulando el total. Este resultado (y sobre todo su tiempo T1) es la
 * línea base contra la cual se calculan Speedup (Sn = T1 / Tn) y
 * Eficiencia (En = Sn / n) de la versión paralela.
 */
public class SequentialSumCalculator {

    public static SumResult compute(int[] array) {
        long startTime = System.currentTimeMillis();

        long total = 0;
        for (int i = 0; i < array.length; i++) {
            total += array[i];
        }

        long endTime = System.currentTimeMillis();
        return new SumResult(total, endTime - startTime, 1);
    }
}
