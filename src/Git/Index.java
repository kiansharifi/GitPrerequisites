package src.Git;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

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

    public static void addDirectory(String pathOfDirectory) throws Exception {
        initialize();
        File f = new File(pathOfDirectory);
        Tree tree = new Tree();

        tree.addDirectory(pathOfDirectory);
        tree.save(); // do I need to save

        FileWriter writer = new FileWriter("index", true);
        String entry = "tree : " + tree.getSha() + " : " + f.getName();
        if (!reader("index").contains(entry)) {
            if (!reader("index").isEmpty()) {
                writer.write("\n");
            }
            writer.write(entry);

        }
        writer.close();
    }

    public static void main(String[] args) throws Exception {
        Index i = new Index();
        i.addDirectory("mark");
    }

    public static void markFileAsDeleted(String filename) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter("index", true))) { // true for append mode
            pw.println("*deleted* " + filename);
        }
    }

    public static void markFileAsEdited(String filename) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter("index", true))) { // true for append mode
            pw.println("*edited* " + filename);
        }
    }

}