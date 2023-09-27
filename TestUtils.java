
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class TestUtils {
    public static void writeStringToFile(String path, String contents) throws IOException {
        FileWriter fw = new FileWriter(path, false);
        fw.write(contents);
        fw.close();
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        file.delete();
    }

    public static void deleteDirectory(String path) {
        File file = new File(path);
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    deleteDirectory(f.getPath());
                }
            }
        }
        file.delete();
    }
}
