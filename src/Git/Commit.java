package src.Git;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Commit {
    String author;
    String summary;
    String parentSHA;
    String date;
    String commitSHA;

    String treeSHA;

    File commit;
    String commitPath;
    
    public Commit(String author, String summary) throws Exception {
        this.author = author;
        this.summary = summary;
        this.parentSHA = "";
        createDate();
        this.treeSHA = createTree();
        write();

        Tree tree = new Tree ();
        FileReader fw = new FileReader ("index");
        BufferedReader br = new BufferedReader (fw);
        try {
            String line;
            while ((line = br.readLine()) != null)
            {
                tree.add(line);
            }
        }
        catch (IOException e) {
            System.out.println ("Error reading the file: " + e.getMessage());
        }

    }

    public Commit(String SHA, String author, String summary) throws Exception {
        this.author = author;
        this.summary = summary;
        this.parentSHA = SHA;
        createDate();
        this.treeSHA = createTree();
        write();

        Tree tree = new Tree ();
        FileReader fw = new FileReader ("index");
        BufferedReader br = new BufferedReader (fw);
        try {
            String line;
            while ((line = br.readLine()) != null)
            {
                tree.add(line);
            }
        }
        catch (IOException e) {
            System.out.println ("Error reading the file: " + e.getMessage());
        }
    }

    private void saveCommitToObjectsFolder(String commitContents) throws Exception {
        TestUtils.writeStringToFile("./objects/" + commitSHA, commitContents);
    }

    public String getSHA() {
        return commitSHA;
    }

    public void write() throws Exception {
        StringBuilder sb = new StringBuilder("");

        sb.append(treeSHA + "\n");
        sb.append(parentSHA + "\n");
        // Skip 3rd line for sha
        sb.append(author + "\n");
        sb.append(date + "\n");
        sb.append(summary);
        commitSHA = TestUtils.getSha1(sb.toString());

        StringBuilder sbWithBlank3rdLine= new StringBuilder("");
        sbWithBlank3rdLine.append(treeSHA + "\n");
        sbWithBlank3rdLine.append(parentSHA + "\n");
        sbWithBlank3rdLine.append("\n");
        sbWithBlank3rdLine.append(author + "\n");
        sbWithBlank3rdLine.append(date + "\n");
        sbWithBlank3rdLine.append(summary);
        saveCommitToObjectsFolder(sbWithBlank3rdLine.toString());

    }

    public String readFile(File fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        StringBuilder string = new StringBuilder();
        while (reader.ready()) {
            string.append((char) reader.read());
        }
        reader.close();
        return string.toString();
    }

    public String createTree() throws Exception {
        Tree t = new Tree();
        t.save();
        return t.getSha();
    }


    public String getDate() {
        return date;
    }

    private void createDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = new Date();
        date = formatter.format(today);
    }
}
