package HistorialChat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ChatMessage
 * -----------
 * Representa un único registro de chat parseado desde el archivo de entrada.
 * Cada línea del archivo tiene el formato:
 *      [YYYY-MM-DD HH:MM] Contacto: Mensaje del chat
 *
 * Esta clase es inmutable (todos sus campos son 'final'), lo cual es
 * importante en un contexto multihilo: una vez creado un ChatMessage,
 * puede ser leído de forma segura por múltiples hilos simultáneamente
 * sin necesidad de sincronización, ya que ningún hilo modifica su estado.
 */
public class ChatMessage {

    private final LocalDateTime dateTime;
    private final String contact;
    private final String message;

    public ChatMessage(LocalDateTime dateTime, String contact, String message) {
        this.dateTime = dateTime;
        this.contact = contact;
        this.message = message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /** @return la fecha (sin hora) en la que se envió el mensaje. */
    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }

    /** @return el día de la semana (LUNES..DOMINGO) en que se envió el mensaje. */
    public DayOfWeek getDayOfWeek() {
        return dateTime.getDayOfWeek();
    }

    public String getContact() {
        return contact;
    }

    public String getMessage() {
        return message;
    }

    /** @return la longitud en caracteres del texto del mensaje. */
    public int getLength() {
        return message.length();
    }

    @Override
    public String toString() {
        return "[" + dateTime + "] " + contact + ": " + message;
    }
}
