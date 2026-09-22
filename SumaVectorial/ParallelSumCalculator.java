package SumaVectorial;

/**
 * ParallelSumCalculator
 * ---------------------
 * VERSIÓN 2 (Multihilo / Paralelismo de Datos) de la Actividad 2.
 *
 * El arreglo se divide en n sub-rangos CONTINUOS de tamaño aproximado
 * N / n. Se lanzan n hilos trabajadores; cada uno recorre únicamente su
 * segmento asignado y acumula una suma parcial local. El hilo principal
 * espera con join() a que todos terminen y luego realiza la suma global
 * acumulando las n sumas parciales (reducción secuencial y segura, ya
 * que ocurre después de que todos los hilos han finalizado).
 *
 * Nota sobre el reparto: cuando N no es múltiplo exacto de n, el último
 * hilo recibe también el residuo (los elementos sobrantes), de modo que
 * no se pierda ningún elemento del arreglo sin sumar.
 *
 * Nota sobre sincronización: cada SumWorker escribe únicamente en su
 * propio campo 'partialSum' y solo lee el segmento del arreglo que le
 * corresponde (sin solapamiento con los demás hilos), por lo que no hay
 * condiciones de carrera y no se requieren locks explícitos.
 */
public class ParallelSumCalculator {

    public static SumResult compute(int[] array, int numThreads) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        int n = array.length;
        int blockSize = n / numThreads;

        SumWorker[] workers = new SumWorker[numThreads];
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            int from = i * blockSize;
            // El último hilo absorbe el residuo de la división N / n
            int to = (i == numThreads - 1) ? n : from + blockSize;

            workers[i] = new SumWorker(array, from, to);
            threads[i] = new Thread(workers[i], "Hilo-Suma-" + (i + 1));
        }

        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        // Suma global: se acumulan las sumas parciales recibidas tras el join
        long total = 0;
        for (SumWorker w : workers) {
            total += w.getPartialSum();
        }

        long endTime = System.currentTimeMillis();
        return new SumResult(total, endTime - startTime, numThreads);
    }

    /** Hilo trabajador: suma únicamente el sub-rango [from, to) que se le asignó. */
    private static class SumWorker implements Runnable {
        private final int[] array;
        private final int from;
        private final int to;
        private long partialSum = 0;

        SumWorker(int[] array, int from, int to) {
            this.array = array;
            this.from = from;
            this.to = to;
        }

        @Override
        public void run() {
            long localSum = 0;
            for (int i = from; i < to; i++) {
                localSum += array[i];
            }
            partialSum = localSum;
        }

        long getPartialSum() {
            return partialSum;
        }
    }
}
