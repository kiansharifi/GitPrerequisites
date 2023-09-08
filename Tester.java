import java.io.IOException;

public class Tester
{
    public static void main (String [] args) throws IOException
    {
        Blob blob = new Blob ("TestFile");
        Index.initialize ();
        Index.addBlob ("TestFile");
        Index.addBlob ("TestFile2");
        Index.addBlob ("TestFile3");
        Index.removeBlob ("TestFile");
    }

}