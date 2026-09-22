package HistorialChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ChatFileReader
 * --------------
 * Responsable de leer el archivo de texto plano (.txt / .csv) con el
 * historial de chats y convertir cada línea válida en un objeto
 * ChatMessage. Esta clase se ejecuta de forma secuencial en el hilo
 * principal, ANTES de lanzar los hilos trabajadores: la lectura de disco
 * es I/O y no se beneficia de paralelizarse con este enfoque, y además
 * ambas estrategias (A y B) necesitan la lista completa ya cargada en
 * memoria para poder dividir el trabajo entre los hilos.
 */
public class ChatFileReader {

    // Formato esperado por línea: [YYYY-MM-DD HH:MM] Contacto: Mensaje
    private static final Pattern LINE_PATTERN =
            Pattern.compile("^\\[(\\d{4}-\\d{2}-\\d{2})\\s+(\\d{2}:\\d{2})]\\s*([^:]+):\\s?(.*)$");

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Lee el archivo indicado y retorna la lista de mensajes válidos.
     * Las líneas que no coincidan con el formato esperado se reportan
     * por consola de error y se omiten, para no detener todo el proceso
     * por un registro mal formado.
     */
    public static List<ChatMessage> readMessages(String filePath) throws IOException {
        List<ChatMessage> messages = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                Matcher matcher = LINE_PATTERN.matcher(line);
                if (matcher.matches()) {
                    String dateStr = matcher.group(1);
                    String timeStr = matcher.group(2);
                    String contact = matcher.group(3).trim();
                    String messageText = matcher.group(4);

                    try {
                        LocalDateTime dateTime =
                                LocalDateTime.parse(dateStr + " " + timeStr, DATE_TIME_FORMATTER);
                        messages.add(new ChatMessage(dateTime, contact, messageText));
                    } catch (Exception parseEx) {
                        System.err.println("Línea " + lineNumber
                                + ": fecha/hora inválida, se omite -> " + line);
                    }
                } else {
                    System.err.println("Línea " + lineNumber
                            + ": no coincide con el formato esperado, se omite -> " + line);
                }
            }
        }

        return messages;
    }
}
