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
        File file = new File("objects/" + getSha1(fileContents));
        FileWriter fw = new FileWriter(file);
        fw.write(fileContents);
        fw.close();
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

    public void addDirectory(String directoryPath) throws IOException, NoSuchAlgorithmException {
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory() || !directory.canRead()) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }

        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                String content = reader(file);
                String sha1 = getSha1(content);
                add("blob : " + sha1 + " : " + file.getName());
            } else if (file.isDirectory()) {
                Tree childTree = new Tree();
                childTree.addDirectory(file.getAbsolutePath());
                childTree.save();
                String sha1 = childTree.getSha1(childTree.getFileContents());
                add("tree : " + sha1 + " : " + file.getName());
            }
        }
    }

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
