package HistorialChat.Utilities;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * ChatGenerator
 * -------------
 * Utilidad AUXILIAR (no forma parte de la solución evaluada) para generar
 * un archivo de historial de chat de prueba con el formato requerido:
 *      [YYYY-MM-DD HH:MM] Contacto: Mensaje del chat
 *
 * Sirve para probar rápidamente TaskParallelismAnalyzer y
 * DataParallelismAnalyzer con volúmenes de datos grandes, sin depender
 * de un archivo real de WhatsApp/Telegram.
 *
 * Uso: java chatanalysis.ChatGenerator <numMensajes> <rutaSalida>
 */
public class ChatGenerator {

    private static final String[] CONTACTS = {
            "Ana", "Carlos", "Mateo", "Laura", "Sofia", "Diego", "Valentina", "Andres"
    };
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void generateSampleFile(String path, int numMessages) throws IOException {
        Random random = new Random(42); // semilla fija para resultados reproducibles
        LocalDateTime cursor = LocalDateTime.of(2026, 1, 1, 8, 0);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path))) {
            for (int i = 0; i < numMessages; i++) {
                cursor = cursor.plusMinutes(1 + random.nextInt(120));
                String contact = CONTACTS[random.nextInt(CONTACTS.length)];
                String message = randomMessage(random);
                writer.write("[" + cursor.format(FORMATTER) + "] " + contact + ": " + message);
                writer.newLine();
            }
        }
    }

    private static String randomMessage(Random random) {
        int length = 5 + random.nextInt(150);
        StringBuilder sb = new StringBuilder();
        String palabras = "hola que tal como estas vamos a la reunion manana revisa el codigo " +
                "gracias por la ayuda nos vemos pronto tengo una duda sobre el proyecto";
        String[] tokens = palabras.split(" ");
        while (sb.length() < length) {
            sb.append(tokens[random.nextInt(tokens.length)]).append(" ");
        }
        return sb.toString().trim();
    }

    public static void main(String[] args) throws IOException {
        int n = args.length > 0 ? Integer.parseInt(args[0]) : 10000;
        String path = args.length > 1 ? args[1] : "chat_sample.txt";
        generateSampleFile(path, n);
        System.out.println("Archivo generado: " + path + " con " + n + " mensajes.");
    }
}
