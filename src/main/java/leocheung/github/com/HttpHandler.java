package leocheung.github.com;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpHandler extends Thread {
    private final Socket socket;

    public HttpHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (InputStream input = socket.getInputStream()) {
            try (OutputStream output = socket.getOutputStream()) {
                handle(input, output);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
            System.out.println("client disconnected.");
        }
    }

    /*
        For test, just process GET request
     */
    private void handle(InputStream input, OutputStream output) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        var writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        boolean ok = false;
        String requestHeaderFirstLine = reader.readLine();
        if (requestHeaderFirstLine.startsWith("GET / HTTP/1.")) {
            ok = true;
        }
        while (true) {
            String header = reader.readLine();
            if (header.isEmpty()) {
                break;
            }
            System.out.println(header);
        }
        System.out.println(ok ? "Response OK" : "Response Error");
        if (!ok) {
            // 发送错误响应:
            writer.write("HTTP/1.0 404 Not Found\r\n");
            writer.write("Content-Length: 0\r\n");
            writer.write("\r\n");
            writer.flush();
        } else {
            // 发送成功响应:
            String data = "<html><body><h1>Hello, world!</h1></body></html>";
            int length = data.getBytes(StandardCharsets.UTF_8).length;
            writer.write("HTTP/1.0 200 OK\r\n");
            writer.write("Connection: close\r\n");
            writer.write("Content-Type: text/html\r\n");
            writer.write("Content-Length: " + length + "\r\n");
            writer.write("\r\n"); // 空行标识Header和Body的分隔
            writer.write(data);
            writer.flush();
        }
    }
}
