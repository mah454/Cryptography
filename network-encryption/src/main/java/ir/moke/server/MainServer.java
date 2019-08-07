package ir.moke.server;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Logger;

public class MainServer implements Closeable {
    private static final Logger logger = Logger.getLogger(MainServer.class.getName());
    private static ServerSocket server;

    public static void main(String[] args) {
        var port = 8080;
        try {
            server = new ServerSocket(port);
            while (!server.isClosed()) {
                var socket = server.accept();
                ServerSocketHandler serverSocketHandler = new ServerSocketHandler(socket);
                new Thread(serverSocketHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        server.close();
        logger.fine("Server closed and shutdown");
    }
}
