package ir.moke.client;

import java.io.Closeable;
import java.util.Scanner;

public class MainClient implements Closeable {

    public static void main(String[] args) throws Exception {
        ClientSocketHandler socketHandler = new ClientSocketHandler();
        new Thread(socketHandler).start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String userMsg = scanner.nextLine();
            String encMsg = socketHandler.encrypt(userMsg);
            socketHandler.sendMessage("MSG " + encMsg);
        }
    }

    @Override
    public void close() {
        System.out.println("Closed");
    }
}
