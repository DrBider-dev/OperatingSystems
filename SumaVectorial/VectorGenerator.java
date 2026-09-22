package SumaVectorial;

import java.util.concurrent.ThreadLocalRandom;

/**
 * VectorGenerator
 * ---------------
 * Genera el arreglo/vector de N números enteros aleatorios que exige la
 * Actividad 2 (N >= 10,000,000). Se usa ThreadLocalRandom porque es más
 * eficiente que java.util.Random para generación masiva de números, y la
 * generación en sí se hace de forma secuencial en el hilo principal ANTES
 * de medir los tiempos de las versiones 1 y 2: lo que se quiere comparar
 * es el costo de SUMAR el vector, no el de crearlo.
 */
public class VectorGenerator {

    /**
     * @param n tamaño del vector a generar (cantidad de elementos)
     * @return arreglo de n enteros aleatorios en el rango [0, 999]
     */
    public static int[] generate(int n) {
        int[] array = new int[n];
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < n; i++) {
            array[i] = random.nextInt(0, 1000);
        }
        return array;
    }
}
