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
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String requestLine = input.readLine();
            HTTPRequest httpRequest = new HTTPRequest(requestLine);
            String response = httpRequest.handleRequest();
            clientSocket.getOutputStream().write(response.getBytes());
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
