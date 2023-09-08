import java.io.IOException;

public class Tester
{
    public static void main (String [] args) throws IOException
    {
        Blob blob = new Blob ();
        String name = "kiansharifi8";
        System.out.println (blob.encryptPassword("TestFile"));

        blob.writeToObjects(name);
    }
}