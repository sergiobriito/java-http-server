import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HTTPRequest {
    private String method = null;
    private String uri = null;
    private String httpVersion = null;

    public HTTPRequest(String requestLine) {
        parse(requestLine);
    }

    public void parse(String request){
        String[] arr = request.split(" ");
        this.method = arr[0];
        this.uri = arr[1];
        this.httpVersion = arr[2];
    };

    public String handleRequest(){
        String response;
        if (this.method.equals("GET")){
            response = handleGet();
        }else{
            response = "Not implemented";
        }
        return response;
    };

    public String handleGet() {
        String responseLine;
        String responseHeaders;
        String responsebody = "";
        String filePath = "src"+ uri;
        File file = new File(filePath);
        if (file.exists()){
            responseLine = getResponseLine(Status.OK);
            HashMap<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Content-Type", getContentType(filePath));
            responseHeaders = getResponseHeaders(extraHeaders);
            responsebody = getResponseBody(file);
        }else{
            responseLine = getResponseLine(Status.NOT_FOUND);
            responseHeaders = getResponseHeaders(null);
            responsebody = "<h1>404 Not Found</h1>";
        };

        return responseLine + responseHeaders + "\r\n" + responsebody;
    };

    public String getResponseLine(Status status) {
        String statusName = switch (status) {
            case OK -> "OK";
            case NOT_FOUND -> "Not Found";
            default -> "Not Implemented";
        };
        return this.httpVersion + " " + status.getCode() + " " + statusName + "\r\n";
    }


    public String getResponseHeaders(HashMap<String, String> extraHeaders) {
        String response;
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Server", "HTTP-SERVER");
        if (extraHeaders != null) {
            headers.putAll(extraHeaders);
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\r\n");
        }
        response = builder.toString();
        return response;
    }


    public String getResponseBody(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            System.out.println("Failed to read the file");
        }
        return stringBuilder.toString();
    }


    public String getContentType(String file) {
        Path path = Paths.get(file);
        try {
            return Files.probeContentType(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
