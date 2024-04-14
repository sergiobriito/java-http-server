import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileService {
    private String file = "data.txt";
    private PrintWriter writer;

    public FileService (){
        try {
            writer = new PrintWriter(new FileWriter(file, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    public void write(String message) {
        writer.println(message);
        writer.flush();
    }

    public void close() {
        writer.close();
    }
}
