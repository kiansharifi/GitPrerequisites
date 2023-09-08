import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Index {
    public static void initialize() throws IOException {
        File indexFile = new File("index");
        indexFile.createNewFile(); // if file already exists will do nothing
        Files.createDirectories(Paths.get("objects/"));
    }

    public static String reader(String TestFile) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader breader = new BufferedReader(new FileReader(TestFile));
        while (breader.ready()) {
            String s = breader.readLine();
            output.append(s);
        }
        breader.close();
        return output.toString();
    }

    public static void addBlob(String TestFile) throws IOException {
        initialize();
        Blob blob = new Blob(TestFile);
        File f = new File(TestFile);
        File indexFile = new File("index");
        String name = f.getName();
        if (reader("index").contains(blob.getSha1())) {
            return;
        }
        FileWriter writer = new FileWriter(indexFile, true);
        if (!reader("index").isEmpty()) {
            writer.write("\n");
        }
        writer.write(name + " : " + blob.getSha1());
        writer.close();
    }

    public static void removeBlob(String TestFile) throws IOException {

        File f = new File(TestFile);
        String name = f.getName();
        String file = reader("index");
        if (!file.contains(name)) {
            return;
        }
        File inputFile = new File("index");
        File tempFile = new File("TempFile");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String lineToRemove = name;
        String currentLine;

        while ((currentLine = reader.readLine()) != null) {
            // trim newline when comparing with lineToRemove
            String trimmedLine = currentLine.trim();
            if (trimmedLine.split(" : ")[0].equals((lineToRemove)))
                continue;
            writer.write(currentLine + System.getProperty("line.separator"));
        }
        writer.close();
        reader.close();
        tempFile.renameTo(inputFile);
    }

}