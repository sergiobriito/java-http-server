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
    private HashMap<String,String> body = null;

    public HTTPRequest(String request) {
        parse(request);
    }

    public String handleRequest(){
        String response = switch (method){
            case "GET" -> handleGet();
            case "POST" -> handlePost();
            default -> "Not implemented";
        };
        return response;
    };

    public String handleGet() {
        String responseLine = "";
        String responseHeaders = "";
        String responsebody = "";
        String filePath = "src" + uri;
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

    public String handlePost(){
        FileService fileService = new FileService();
        fileService.write(String.valueOf(body));
        String responseLine = getResponseLine(Status.CREATED);
        String responseHeaders = getResponseHeaders(null);
        String responsebody = "<h1>CREATED</h1>";
        return responseLine + responseHeaders + "\r\n" + responsebody;
    };

    public String getResponseLine(Status status) {
        String statusName = switch (status) {
            case OK -> "OK";
            case CREATED -> "Created";
            case NOT_FOUND -> "Not Found";
            default -> "Not Implemented";
        };
        return httpVersion + " " + status.getCode() + " " + statusName + "\r\n";
    }

    public String getResponseHeaders(HashMap<String, String> extraHeaders) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Server", "HTTP-SERVER");
        if (extraHeaders != null) {headers.putAll(extraHeaders);};
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\r\n");
        }
        return builder.toString();
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
        String contentType = "";
        Path path = Paths.get(file);
        try {
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            System.out.println("Failed to get the content type");
        }
        return contentType;
    }

    public void parse(String request){
        String[] arr = request.split(" ");
        method = arr[0];
        uri = arr[1];
        httpVersion = arr[2].substring(0,8);
        body = parseBody(request);
    };

    public HashMap<String, String> parseBody(String request) {
        HashMap<String, String> map = new HashMap<>();
        String[] lines = request.split("\\r?\\n");
        boolean isBody = false;
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : lines) {
            if (isBody) {
                stringBuilder.append(line);
            } else if (line.isEmpty()) {
                isBody = true;
            }
        }
        String body = stringBuilder.toString().trim();
        if (body.startsWith("{") && body.endsWith("}")) {
            body = body.substring(1, body.length() - 1);
            String[] keyValuePairs = body.split(",");
            for (String pair : keyValuePairs) {
                String[] entry = pair.trim().split(":");
                if (entry.length == 2) {
                    String key = entry[0].trim().replaceAll("\"", "");
                    String value = entry[1].trim().replaceAll("\"", "");
                    map.put(key, value);
                }
            }
        }

        return map;
    }
}
