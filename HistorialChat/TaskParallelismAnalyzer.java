package HistorialChat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TaskParallelismAnalyzer
 * -----------------------
 * ESTRATEGIA A: Paralelismo de Tareas (Task Parallelism).
 *
 * Se crean 3 hilos trabajadores. Cada hilo recorre el documento COMPLETO,
 * pero cada uno ejecuta una tarea distinta:
 *      Hilo 1 -> Fecha del mensaje más largo
 *      Hilo 2 -> Día de la semana con mayor actividad
 *      Hilo 3 -> Contacto más frecuente
 *
 * El hilo principal (main) crea los 3 hilos, espera su finalización con
 * join() y luego integra (consolida) los tres resultados parciales en un
 * único AnalysisResult.
 *
 * Nota sobre sincronización: cada hilo trabajador escribe únicamente en
 * sus propios campos de resultado (resultDate, resultDay, resultContact),
 * por lo que no existe una región crítica compartida entre ellos y no se
 * requieren locks explícitos. La visibilidad de esos resultados hacia el
 * hilo principal está garantizada por la relación happens-before que
 * Java establece entre Thread.start()/Thread.join() y las operaciones
 * del hilo hijo.
 */
public class TaskParallelismAnalyzer {

    public static AnalysisResult analyze(List<ChatMessage> messages) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        LongestMessageTask task1 = new LongestMessageTask(messages);
        BusiestDayTask task2 = new BusiestDayTask(messages);
        FrequentContactTask task3 = new FrequentContactTask(messages);

        Thread t1 = new Thread(task1, "Hilo-1-MensajeMasLargo");
        Thread t2 = new Thread(task2, "Hilo-2-DiaMasActivo");
        Thread t3 = new Thread(task3, "Hilo-3-ContactoFrecuente");

        // Lanzamos los 3 hilos: cada uno procesa TODO el documento
        t1.start();
        t2.start();
        t3.start();

        // El hilo principal espera a que los 3 terminen (integración)
        t1.join();
        t2.join();
        t3.join();

        long endTime = System.currentTimeMillis();

        AnalysisResult result = new AnalysisResult();
        result.setLongestMessageDate(task1.getResultDate());
        result.setLongestMessageLength(task1.getResultLength());
        result.setBusiestDay(task2.getResultDay());
        result.setBusiestDayCount(task2.getResultCount());
        result.setMostFrequentContact(task3.getResultContact());
        result.setMostFrequentContactCount(task3.getResultCount());
        result.setExecutionTimeMillis(endTime - startTime);

        return result;
    }

    /** Hilo 1: recorre todo el documento buscando el mensaje de mayor longitud. */
    private static class LongestMessageTask implements Runnable {
        private final List<ChatMessage> messages;
        private LocalDate resultDate;
        private int resultLength = -1;

        LongestMessageTask(List<ChatMessage> messages) {
            this.messages = messages;
        }

        @Override
        public void run() {
            for (ChatMessage msg : messages) {
                if (msg.getLength() > resultLength) {
                    resultLength = msg.getLength();
                    resultDate = msg.getDate();
                }
            }
        }

        LocalDate getResultDate() {
            return resultDate;
        }

        int getResultLength() {
            return resultLength;
        }
    }

    /** Hilo 2: recorre todo el documento acumulando mensajes por día de la semana. */
    private static class BusiestDayTask implements Runnable {
        private final List<ChatMessage> messages;
        private DayOfWeek resultDay;
        private long resultCount = 0;

        BusiestDayTask(List<ChatMessage> messages) {
            this.messages = messages;
        }

        @Override
        public void run() {
            Map<DayOfWeek, Long> counts = new EnumMap<>(DayOfWeek.class);
            for (ChatMessage msg : messages) {
                counts.merge(msg.getDayOfWeek(), 1L, Long::sum);
            }
            for (Map.Entry<DayOfWeek, Long> entry : counts.entrySet()) {
                if (entry.getValue() > resultCount) {
                    resultCount = entry.getValue();
                    resultDay = entry.getKey();
                }
            }
        }

        DayOfWeek getResultDay() {
            return resultDay;
        }

        long getResultCount() {
            return resultCount;
        }
    }

    /** Hilo 3: recorre todo el documento acumulando mensajes por contacto. */
    private static class FrequentContactTask implements Runnable {
        private final List<ChatMessage> messages;
        private String resultContact;
        private long resultCount = 0;

        FrequentContactTask(List<ChatMessage> messages) {
            this.messages = messages;
        }

        @Override
        public void run() {
            Map<String, Long> counts = new HashMap<>();
            for (ChatMessage msg : messages) {
                counts.merge(msg.getContact(), 1L, Long::sum);
            }
            for (Map.Entry<String, Long> entry : counts.entrySet()) {
                if (entry.getValue() > resultCount) {
                    resultCount = entry.getValue();
                    resultContact = entry.getKey();
                }
            }
        }

        String getResultContact() {
            return resultContact;
        }

        long getResultCount() {
            return resultCount;
        }
    }
}
