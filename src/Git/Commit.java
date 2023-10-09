package src.Git;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Commit {
    String author;
    String summary;
    String parentSHA; //make getter
    String date;
    String commitSHA;

    String treeSHA; //make getter

    File commit;
    String commitPath;

    public Commit(String author, String summary) throws Exception {
        this.author = author;
        this.summary = summary;
        this.parentSHA = "";
        createDate();
        
        System.out.println("Content of Index Before Reading into Tree: " + Index.reader("index"));

        Tree tree = new Tree();
        try (FileReader fw = new FileReader("index");
             BufferedReader br = new BufferedReader(fw)) {
            String line;
            while ((line = br.readLine()) != null) {
                tree.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading from index: " + e.getMessage());
        }

        if (!this.parentSHA.isEmpty()) {
            tree.add("tree : " + this.parentSHA);
        }

        tree.save();
        this.treeSHA = tree.getSha();

        try (FileWriter fwIndexClear = new FileWriter("index")) {
            fwIndexClear.write("");
        } catch (IOException e) {
            System.out.println("Error clearing the index");
        }
        write();

        if (!this.parentSHA.isEmpty()) {
            File previousCommitFile = new File("./objects/" + this.parentSHA);
            if (previousCommitFile.exists()) {
                updateThirdLineOfFile(previousCommitFile, this.commitSHA);
            } else {
                System.out.println("Previous commit file not found");
            }
        }
    }

    public Commit(String SHA, String author, String summary) throws Exception {
        this.author = author;
        this.summary = summary;
        this.parentSHA = SHA;
        createDate();
        this.treeSHA = createTree();
        write();
        System.out.println("Content of Index Before Reading into Tree: " + Index.reader("index"));

        Tree tree = new Tree();
        try (FileReader fw = new FileReader("index");
             BufferedReader br = new BufferedReader(fw)) {
            String line;
            while ((line = br.readLine()) != null) {
                tree.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading from index: " + e.getMessage());
        }

        if (!this.parentSHA.isEmpty()) {
            tree.add("tree : " + this.parentSHA);
        }

        tree.save();
        this.treeSHA = tree.getSha();

        try (FileWriter fwIndexClear = new FileWriter("index")) {
            fwIndexClear.write("");
        } catch (IOException e) {
            System.out.println("Error clearing the index");
        }
        write();

        if (!this.parentSHA.isEmpty()) {
            File previousCommitFile = new File("./objects/" + this.parentSHA);
            if (previousCommitFile.exists()) {
                updateThirdLineOfFile(previousCommitFile, this.commitSHA);
            } else {
                System.out.println("Previous commit file not found");
            }
        }
    }

    private void saveCommitToObjectsFolder(String commitContents) throws Exception {
        TestUtils.writeStringToFile("./objects/" + commitSHA, commitContents);
    }

    public String getSHA() {
        return commitSHA;
    }
    
    public String getParentSHA() {
        return parentSHA;
    }

    public String getTreeSHA() {
        return treeSHA;
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

        StringBuilder sbWithBlank3rdLine = new StringBuilder("");
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

    private void updateThirdLineOfFile(File file, String newLineContent) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());

        if (lines.size() < 3) {
            throw new IOException("File has less than three lines");
        }

        lines.set(2, newLineContent); // 2 bc 0 index
        Files.write(file.toPath(), lines);
    }
}
