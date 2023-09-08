import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Index
{
    public static void initialize () throws IOException
    {
        File yourFile = new File("index");
        yourFile.createNewFile(); // if file already exists will do nothing
        Files.createDirectories(Paths.get("objects/"));
    }

    public static void addBlob (String TestFile) throws IOException
    {
        Blob blob = new Blob (TestFile);
        File f = new File(TestFile);
        String name = f.getName ();
    }
}