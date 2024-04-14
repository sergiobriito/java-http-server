import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientConnection implements Runnable{
    private final Socket clientSocket;

    public ClientConnection(Socket clientSocket){
        this.clientSocket = clientSocket;
    };

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            char[] buffer = new char[1024];
            int chars = bufferedReader.read(buffer, 0, 1024);
            String request = String.valueOf(buffer, 0, chars).trim();
            HTTPRequest httpRequest = new HTTPRequest(request);
            String response = httpRequest.handleRequest();
            clientSocket.getOutputStream().write(response.getBytes());
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
