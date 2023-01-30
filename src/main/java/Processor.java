import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Processor {

    private byte[] message;
    private byte[] content;
    private Path filePath;
    private static Processor instance; //для хранения инстанса логгера

    final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");


    private Processor() {
    }


    public Answer toProcess(String requestLine) {

        final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
        // read only request line for simplicity
        // must be in form GET /path HTTP/1.1
        final String[] parts = requestLine.split(" ");

        try {
            if (parts.length != 3) {
                // just close socket
                return null;
            }

            final String path = parts[1];
            if (!validPaths.contains(path)) {
                message = (
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes();
            }

            filePath = Path.of(".", "public", path);
            final String mimeType = Files.probeContentType(filePath);

            // special case for classic
            if (path.equals("/classic.html")) {
                final String template = Files.readString(filePath);
                content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                message = (
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes();
            }

            long length;
            try {
                length = Files.size(filePath);
            } catch (IOException exc) {
                return new Answer(message, content, filePath);
            }

            message = (
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes();
        } catch (IOException exc) {
            System.out.println(Main.ERRORMSG);
        }
        return new Answer(message, content, filePath);
    }


    public static Processor getInstance() {
        if (instance == null) {
            instance = new Processor();
        }
        return instance;
    }
}
