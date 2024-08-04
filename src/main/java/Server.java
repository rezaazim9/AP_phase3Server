import model.TCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(11211);
            while (true) {
                Socket socket = serverSocket.accept();
                new TCP(socket).start();
            }
        } catch (IOException e) {
        }
    }
}