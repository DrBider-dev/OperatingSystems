package SumaVectorial;

/**
 * SumResult
 * ---------
 * Objeto de transferencia que agrupa el resultado de sumar el vector
 * (Versión 1 o Versión 2), el tiempo que tardó en milisegundos y el
 * número de hilos usados (1 para la versión secuencial). Se usa 'long'
 * para la suma porque con N >= 10,000,000 elementos, el total puede
 * superar fácilmente el rango de un int (2,147,483,647) y provocar
 * overflow.
 */
public class SumResult {

    private final long sum;
    private final long executionTimeMillis;
    private final int threadCount;

    public SumResult(long sum, long executionTimeMillis, int threadCount) {
        this.sum = sum;
        this.executionTimeMillis = executionTimeMillis;
        this.threadCount = threadCount;
    }

    public long getSum() {
        return sum;
    }

    public long getExecutionTimeMillis() {
        return executionTimeMillis;
    }

    public int getThreadCount() {
        return threadCount;
    }
}
