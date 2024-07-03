package leocheung.github.com;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    public static HttpServer create() {
        return new HttpServer();
    }

    private HttpServer() {
    }

    public void run(int port) {
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("http server is running on port " + port + "...");
            while (true) {
                Socket socket = ss.accept();
                System.out.println("connected from " + socket.getRemoteSocketAddress());
                new HttpHandler(socket).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
