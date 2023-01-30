import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server extends Thread {

    private Settings settings;
    private ServerSocket serverSocket;
    private ExecutorService service; //Создаем переменную для пулла под потоки


    Server(Settings settings) {
        try {
            this.settings = settings;
            this.serverSocket = new ServerSocket(settings.getServerPort());
            service = Executors.newFixedThreadPool(settings.getThreadBound());
            this.start();
        } catch (IOException exc) {
            System.out.println(Main.ERRORMSG);
        }
    }


    @Override
    public void run() {
        while (!interrupted()) {
            try {
                final Socket socket = serverSocket.accept();
                service.submit(new Connection(socket));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        service.shutdown();
    }
}
