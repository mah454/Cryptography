package ir.moke.client;

import ir.moke.common.AESSecurityCap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;
import java.util.logging.Logger;

public class ClientSocketHandler extends AESSecurityCap implements Runnable {

    private static final Logger logger = Logger.getLogger(MainClient.class.getName());
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    @Override
    public void run() {
        try {
            socket = new Socket("127.0.0.1", 8080);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            if (socket.isConnected()) {
                onConnect();
                while (!socket.isClosed()) {
                    String content = dis.readUTF();
                    String cmd = content.split(" ")[0];
                    String msg = content.split(" ")[1];
                    switch (cmd.toUpperCase()) {
                        case "PUBLICKEY":
                            PublicKey clientPublicKey = getPublicKey(msg);
                            setReceiverPublicKey(clientPublicKey);
                            break;
                        case "MSG":

                            break;
                        default:
                            logger.info("### WARNING UNKNOWN MESSAGE ###");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onConnect() {
        PublicKey publicKey = getPublicKey();
        sendMessage(messageCapsulation(publicKey.getEncoded(),"PUBLICKEY"));
    }

    protected void sendMessage(String msg) {
        try {
            dos.writeUTF(msg);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String messageCapsulation(byte[] bytes, String cmd) {
        String payload = Base64.getEncoder().encodeToString(bytes);
        return cmd + " " + payload;
    }


    public void close() throws IOException {
        dos.close();
        dis.close();
        socket.close();
        logger.finest("### CLIENT CLOSED ###");
    }
}
