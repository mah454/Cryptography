package ir.moke.server;

import ir.moke.common.AESSecurityCap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;

public class ServerSocketHandler extends AESSecurityCap implements Runnable {
    private static final Logger logger = Logger.getLogger(ServerSocketHandler.class.getName());

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public ServerSocketHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            if (socket.isConnected()) {
                logger.fine("Client " + socket.toString() + " is connected");
            }
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            while (socket.isConnected()) {
                String content = dis.readUTF();
                String cmd = content.split(" ")[0];
                String msg = content.split(" ")[1];
                switch (cmd.toUpperCase()) {
                    case "PUBLICKEY":
                        onOpen(msg);
                        break;
                    case "MSG":
                        System.out.println("Receive : " + decrypt(msg));
                        break;
                    default:
                        System.out.println("WARNING UNKNOWN MESSAGE : " + msg);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onOpen(String publicKeyBase64) {
        PublicKey clientPublicKey = getPublicKey(publicKeyBase64);
        setReceiverPublicKey(clientPublicKey);

        PublicKey serverPublicKey = getPublicKey();
        sendMessage(messageCapsulation(serverPublicKey.getEncoded(),"PUBLICKEY"));
    }

    private void sendMessage(String msg) {
        try {
            dos.writeUTF(msg);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String messageCapsulation(byte[] bytes, String cmd) {
        String payload = Base64.getEncoder().encodeToString(bytes);
        return cmd + " " + payload;
    }

    private void closeConnection() {
        try {
            dis.close();
            dos.close();
            socket.close();
            logger.fine("Client " + socket.toString() + " disconnected .\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
