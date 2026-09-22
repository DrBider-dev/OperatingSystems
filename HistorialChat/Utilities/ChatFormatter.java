package HistorialChat.Utilities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFormatter {

    /*
     * Formato original de WhatsApp:
     *
     * [8/27/26, 6:59:34 PM] @cristian.s.escarraga: Tengo problemas...
     *
     * Grupos:
     * 1 -> fecha
     * 2 -> hora
     * 3 -> resto del mensaje
     */
    private static final Pattern MESSAGE_PATTERN = Pattern.compile(
            "^\\[(\\d{1,2}/\\d{1,2}/\\d{2}), " +
            "(\\d{1,2}:\\d{2}:\\d{2} [AP]M)\\] (.*)$"
    );

    /*
     * Formato para interpretar la fecha original.
     */
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("M/d/yy h:mm:ss a");

    /*
     * Formato que queremos generar.
     */
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public static void main(String[] args) {

        // Archivo de entrada
        Path input = Path.of("chat.txt");

        // Archivo de salida
        Path output = Path.of("chat_formateado.txt");

        try {
            convertirChat(input, output);

            System.out.println("Chat convertido correctamente.");
            System.out.println("Archivo generado: " + output.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error al procesar el archivo:");
            System.err.println(e.getMessage());
        }
    }


    public static void convertirChat(Path input, Path output)
            throws IOException {

        List<String> lines = Files.readAllLines(
                input,
                StandardCharsets.UTF_8
        );

        List<String> mensajes = new ArrayList<>();

        LocalDateTime fechaHoraActual = null;
        String contactoActual = null;
        StringBuilder mensajeActual = new StringBuilder();

        for (String line : lines) {

            Matcher matcher = MESSAGE_PATTERN.matcher(line);

            /*
             * Encontramos una nueva línea de mensaje.
             */
            if (matcher.matches()) {

                // Guardar el mensaje anterior antes de comenzar uno nuevo
                if (fechaHoraActual != null && contactoActual != null) {

                    agregarMensaje(
                            mensajes,
                            fechaHoraActual,
                            contactoActual,
                            mensajeActual.toString()
                    );
                }

                String fecha = matcher.group(1);
                String hora = matcher.group(2);
                String contenido = matcher.group(3);

                /*
                 * Convertimos:
                 *
                 * 8/27/26, 6:59:34 PM
                 *
                 * a LocalDateTime.
                 */
                fechaHoraActual = LocalDateTime.parse(
                        fecha + " " + hora,
                        INPUT_FORMAT
                );

                /*
                 * Separamos contacto y mensaje.
                 *
                 * Ejemplo:
                 *
                 * @cristian.s.escarraga: Hola
                 *
                 * contacto = @cristian.s.escarraga
                 * mensaje = Hola
                 */
                int separador = contenido.indexOf(": ");

                if (separador != -1) {

                    contactoActual =
                            contenido.substring(0, separador);

                    mensajeActual = new StringBuilder(
                            contenido.substring(separador + 2)
                    );

                } else {

                    /*
                     * Es probablemente un mensaje del sistema
                     * de WhatsApp.
                     *
                     * Por ejemplo:
                     *
                     * - Someone joined via invite link
                     *
                     * Lo ignoramos.
                     */
                    fechaHoraActual = null;
                    contactoActual = null;
                    mensajeActual.setLength(0);
                }

            } else if (fechaHoraActual != null) {

                /*
                 * Si la línea no comienza con una fecha,
                 * pertenece al mensaje anterior.
                 *
                 * Esto permite manejar mensajes como:
                 *
                 * [fecha] Persona: Hola
                 * Esta es otra línea
                 * Y esta también.
                 */
                if (!line.isBlank()) {

                    if (!mensajeActual.isEmpty()) {
                        mensajeActual.append(" ");
                    }

                    mensajeActual.append(line.trim());
                }
            }
        }

        /*
         * Guardar el último mensaje del archivo.
         */
        if (fechaHoraActual != null && contactoActual != null) {

            agregarMensaje(
                    mensajes,
                    fechaHoraActual,
                    contactoActual,
                    mensajeActual.toString()
            );
        }

        /*
         * Escribir todo el resultado.
         */
        Files.write(
                output,
                mensajes,
                StandardCharsets.UTF_8
        );
    }


    private static void agregarMensaje(
            List<String> mensajes,
            LocalDateTime fechaHora,
            String contacto,
            String mensaje
    ) {

        /*
         * Formato final:
         *
         * [2026-08-27 18:59] @cristian.s.escarraga: Hola
         */
        String resultado = String.format(
                "[%s] %s: %s",
                fechaHora.format(OUTPUT_FORMAT),
                contacto,
                mensaje.trim()
        );

        mensajes.add(resultado);
    }
}
