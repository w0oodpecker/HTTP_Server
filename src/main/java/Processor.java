import org.apache.http.client.utils.URLEncodedUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Processor {

    private byte[] message;
    private byte[] content;
    private Path filePath;
    private static Processor instance; //для хранения инстанса
    final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js", "/message"); //Разрешенные пути
    public final String GET = "GET";
    public final String POST = "POST";
    final List<String> allowedMethods = List.of(GET, POST); //Разрешенные методы
    private final String BADMESSAGE = "HTTP/1.1 404 Not Found\r\n" +
            "Content-Length: 0\r\n" +
            "Connection: close\r\n" +
            "\r\n";

    private Processor() {
    }


    public Answer toProcess(String request) {

        // read only request line for simplicity
        // must be in form GET /path HTTP/1.1
        final String[] requestSplitted = request.split("\r\n");
        final String[] parts = requestSplitted[0].split(" "); //Выделяем request-line


        try {
            //Проверяем корректность структуры запроса
            if (parts.length != 3) {
                return new Answer(message);
            }

            final Request requestObject = parseRequestLine(parts); //Создаем объект запроса

            //Проверяем есть ли разрешенный метод
            if (!allowedMethods.contains(requestObject.getMethod())) {
                return new Answer(BADMESSAGE.getBytes());
            }

            //Проверяем есть ли разрешенный путь
            if (!validPaths.contains(requestObject.getPath())) {
                return new Answer(BADMESSAGE.getBytes());
            }

            filePath = Path.of(".", "public", requestObject.getPath());
            final String mimeType = Files.probeContentType(filePath);

            // special case for classic
            if (requestObject.getPath().equals("/classic.html")) {
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
                return new Answer(message, content, null);
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


    public Request parseRequestLine(String[] parts) {
        int lengthOfPathQuery = parts[1].length();
        int positionOfDelimeter = parts[1].indexOf("?");

        if (positionOfDelimeter > 0) {
            final String path = parts[1].substring(0, positionOfDelimeter);
            final String query = parts[1].substring(positionOfDelimeter + 1, lengthOfPathQuery);
            return new Request(parts[0],
                    path,
                    parts[2],
                    URLEncodedUtils.parse(query, Charset.forName("utf-8")));
        }
        else{
            final String path = parts[1];
            return new Request(parts[0],
                    path,
                    parts[2]);
        }
    }
}
