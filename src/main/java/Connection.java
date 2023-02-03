import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class Connection extends Thread {

    private Socket socket;
    private BufferedReader in; //Входящий поток
    private BufferedOutputStream out; //Исходящий поток
    private Processor processor;


    Connection(Socket socket) {
        try {
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedOutputStream(socket.getOutputStream());
            processor = Processor.getInstance();
            //this.start(); Нельзя чтобы сам себя запускал, убрал
        } catch (IOException exc) {
            System.out.println(Main.ERRORMSG);
        }
    }


    @Override
    public void run() {
        // read only request line for simplicity
        // must be in form GET /path HTTP/1.1

        try {
            final int limit = Settings.getInstance(Main.nameOfSettingsFile).getBufferLimit();
            in.mark(limit);
            while (!interrupted()) {
                if (in.ready()) {
                    //final String requestLine = in.readLine();
                    final char[] buffer = new char[limit];
                    final var read = in.read(buffer, 0, limit);
                    //System.out.println(new String(buffer, 0, read));
                    final Answer answer = processor.toProcess(new String(buffer, 0, read));
                    this.send(answer);
                }
            }
        } catch (IOException exc) {
        } finally {
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
            }
        }
    }


    public void send(Answer answer) {
        try {
            if (answer != null) {
                out.write(answer.getMessage());
                out.flush();
                if (answer.getContent() != null) {
                    out.write(answer.getContent());
                    out.flush();
                }
                if(answer.getFilePath() != null) {
                    Files.copy(answer.getFilePath(), out);
                    out.flush();
                }
            }
        } catch (IOException exc) {
            System.out.println(Main.ERRORMSG);
        }
    }
}