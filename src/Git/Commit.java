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
    String parentSHA = "";
    String date;
    String name;

    Tree origin = new Tree();
    String treeSHA;

    File commit;
    String commitPath;

    public Commit(String author, String summary) throws IOException {
        this.author = author;
        this.summary = summary;
        treeSHA = origin.getSha1(""); 
        createDate();
    }

    public Commit(String SHA, String author, String summary) {
        this.author = author;
        this.summary = summary;
        this.parentSHA = SHA;
        createDate();
    }

    private void initializeCommit() throws Exception {
        if (!commit.exists()) {
            commit.createNewFile();
        }

        commitPath = "./objects/";
    }

    public void write() throws Exception {
        initializeCommit();
        StringBuilder sb = new StringBuilder("");

        sb.append(treeSHA + "\n");
        sb.append(parentSHA + "\n");
        File index = new File("index"); //origin.getIndex();
        String extSHA = this.createHash(this.readFile(index));
        sb.append(extSHA + "\n");
        sb.append(author + "\n");
        sb.append(date + "\n");
        sb.append(summary);
        initializeCommit();
        name = createHash(sb.toString());
        commit = new File(name, commitPath);

        FileWriter fw = new FileWriter(commit, true);
        fw.append(sb.toString());
        fw.close();
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

    public String createHash(String fileContents) throws Exception {
        Tree t = new Tree();
        return t.getSha1(fileContents);

    }

    public String getDate() {
        return date;
    }

    private void createDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = new Date();
        date = formatter.format(today);
    }

    public String getFileName() {
        return name;
    }
}
