package src.Git;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Scanner;

public class Tree {

    String fileContents = "";
    private String sha = "";

    public String getFileContents() {
        return fileContents;
    }

    public void add(String contents) {
        String[] parts = contents.split(" : ");
        String shaOfFile = parts[1];
        String optionalFileName = parts.length > 2 ? parts[2] : "";

        if (!fileContents.contains(shaOfFile)
                && (optionalFileName.isEmpty() || (!fileContents.contains(optionalFileName)))) {
            fileContents += contents + "\n";
        }
    }

    public void remove(String entry) {
        String newFileContents = "";

        Scanner inContents = new Scanner(fileContents);

        while (inContents.hasNextLine()) {
            String line = inContents.nextLine();
            if (!line.contains(entry)) {
                newFileContents += line + "\n";
            }
        }

        inContents.close();

        fileContents = newFileContents;
    }

    public void save() throws IOException {
        fileContents = fileContents.stripTrailing();
        sha = getSha1(fileContents);
        TestUtils.writeStringToFile("objects/" + getSha1(fileContents), fileContents);
    }

    public String getSha() {
        return sha;
    }

    public String getSha1(String input) throws IOException {
        String passwordString = input;
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(passwordString.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sha1;
    }

    public String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public String addDirectory(String directoryPath) throws Exception {
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory() || !directory.canRead()) {
            throw new Exception("Invalid directory path");
        }

        File[] directoryThings = directory.listFiles();

        if (directoryThings == null || directoryThings.length == 0) { // for empty directory test
            return "";
        }

        for (File file : directoryThings) {
            if (file.isFile()) {
                Blob blob = new Blob(file.getPath());
                String sha1 = blob.getSha1();
                add("blob : " + sha1 + " : " + file.getName());
            } else if (file.isDirectory()) {
                Tree childTree = new Tree();
                childTree.addDirectory(file.getPath());
                childTree.save();
                if (!childTree.getFileContents().isEmpty()) {
                    add("tree : " + childTree.getSha() + " : " + file.getName());
                }
            }
        }

        save();
        return getSha();
    }

    // public static void main(String[] args) throws Exception {
    //     Tree tree = new Tree();
    //     tree.addDirectory("mark");
    // }

    public String reader(File file) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader breader = new BufferedReader(new FileReader(file));
        while (breader.ready()) {
            String s = breader.readLine();
            output.append(s);
        }
        breader.close();
        return output.toString();
    }
}
