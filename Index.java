import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        initialize ();
        Blob blob = new Blob(TestFile);
        File f = new File(TestFile);
        File indexFile = new File("index");
        String name = f.getName();
        if (reader ("index").contains (blob.getSha1 ()))
        {
            return;
        }
        FileWriter writer = new FileWriter(indexFile, true);
        if (!reader ("index").isEmpty ())
        {
            writer.write ("\n");
        }
        writer.write(name + " : " + blob.getSha1());
        writer.close();
    }

}