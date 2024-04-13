import java.io.IOException;
import java.net.*;

public class TCPServer {
    private Integer port = 8080;
    private ServerSocket serverSocket = null;

    public TCPServer () {
        this.start();
    };

    public TCPServer (Integer port) {
        this.port = port;
        this.start();
    };

    private void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from: " + clientSocket.getRemoteSocketAddress());
                new Thread(new ClientConnection(clientSocket)).start();
            }

        } catch (IOException e) {
            System.out.println("Failed to start the server");
        }

        close();
    }

    public void close(){
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            System.out.println("Failed to close the server");
        }
    };

}
