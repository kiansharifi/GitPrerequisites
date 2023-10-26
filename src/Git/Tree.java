package src.Git;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Scanner;

public class Tree {

    String fileContents = "";
    private String sha = "";
    private static String foundTree = "";

    public String getFileContents() {
        return fileContents;
    }

    public void add(String contents) {
        String[] parts = contents.split(" : ");
        String shaOfFile = parts[1];
        String optionalFileName;

        if (parts.length > 2) {
            optionalFileName = parts[2];
        } else {
            optionalFileName = "";
        }

        if (!fileContents.contains("blob : " + shaOfFile)
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

        String path = "objects/" + getSha1(fileContents); // for debugging
        String savedContent = new String(Files.readAllBytes(Paths.get(path)));
        System.out.println("Tree Content After Saving: " + savedContent);

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
        // Shou
        File[] directoryThings = directory.listFiles();

        for (File file : directoryThings) {
            if (file.isFile()) {
                Blob blob = new Blob(file.getPath());
                String sha1 = blob.getSha1();
                add("blob : " + sha1 + " : " + file.getName());
            } else if (file.isDirectory()) {
                Tree childTree = new Tree();
                childTree.addDirectory(file.getPath());
                childTree.save();
                add("tree : " + childTree.getSha() + " : " + file.getName());
            }
        }

        save();
        return getSha();
    }

    public static void main(String[] args) throws Exception {
        Tree tree = new Tree();
        tree.addDirectory("mark");
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

    public static String findDeletedFile(String deletedFileName, String treeSha) throws Exception {
        FileReader reader = new FileReader("objects/" + treeSha);
        try (Scanner scan = new Scanner(reader)) {
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if (line.split(" : ")[0].equals("tree")) {
                    String a = findDeletedFile(deletedFileName, line.split(" : ")[1]);
                    if (!a.isEmpty())
                        return a;
                } else if (line.contains(deletedFileName)) {
                    foundTree = treeSha;
                }
            }
        }
        reader.close();
        return foundTree;
    }

    public static void markFileAsDeleted(String filename) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter("index", true))) {
            pw.println("*deleted* " + filename);
            pw.close();
        }
    }

    public static String deleteFile(String deletedFileName) throws Exception {
        markFileAsDeleted(deletedFileName);

        FileReader frHead = new FileReader("HEAD");
        Scanner frScan = new Scanner(frHead);
        String latestCommit = frScan.nextLine();
        frScan.close();
        frHead.close();

        FileReader latestTree = new FileReader("objects/" + latestCommit);
        Scanner latestScan = new Scanner(latestTree);
        String latestTreeSha = latestScan.nextLine();
        latestScan.close();
        latestTree.close();

        return findDeletedFile(deletedFileName, latestTreeSha);
    }

    public static void replaceSHA1InTree(String treeSha, String originalSHA1, String newSHA1) throws IOException {
        Path treePath = Paths.get("objects/" + treeSha);
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(treePath), charset);
        content = content.replace(originalSHA1, newSHA1);
        Files.write(treePath, content.getBytes(charset));
    }

    public static void markFileAsEdited(String filename) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter("index", true))) {
            pw.println("*edited* " + filename);
            pw.close();
        }
    }

    public static String editFile(String editedFileName, String newSHA1) throws Exception {
        markFileAsEdited(editedFileName);

        FileReader frHead = new FileReader("HEAD");
        Scanner frScan = new Scanner(frHead);
        String latestCommit = frScan.nextLine();
        frScan.close();
        frHead.close();

        FileReader latestTree = new FileReader("objects/" + latestCommit);
        Scanner latestScan = new Scanner(latestTree);
        String latestTreeSha = latestScan.nextLine();
        latestScan.close();
        latestTree.close();
        String treeWithOriginalFile = findDeletedFile(editedFileName, latestTreeSha);

        Tree tree = new Tree();
        String originalSHA1 = tree.getSha1(editedFileName);

        replaceSHA1InTree(treeWithOriginalFile, originalSHA1, newSHA1);

        return treeWithOriginalFile;
    }

    private String getTreeSHAFromCommit(String commitSHA) throws IOException {
        String commitContents = TestUtils.readFile("objects/" + commitSHA);
        String[] lines = commitContents.split("\n");
        if (lines.length > 0) {
            return lines[0];
        }
        return "";
    }

    private void checkoutBlob(String blobSHA, String name, String currentPath) throws IOException {
        byte[] blobContent = Files.readAllBytes(Paths.get("objects/" + blobSHA));
        Files.write(Paths.get(currentPath + File.separator + name), blobContent);
    }

    private void checkoutTree(String treeSHA, String currentPath) throws IOException {
        String treeContents = TestUtils.readFile("objects/" + treeSHA);
        String[] entries = treeContents.split("\n");
        for (String entry : entries) {
            String[] parts = entry.split(" : ");
            String type = parts[0];
            String sha = parts[1];
            String name = "";
            if (parts.length > 2) {
                name = parts[2];
            }
            
            if (type.equals("blob")) {
                checkoutBlob(sha, name, currentPath);
            }
            else if (type.equals("tree")) {
                String newPath = currentPath + File.separator + name;
                File newDir = new File(newPath);
                newDir.mkdirs();
                checkoutTree(sha, newPath); //recursive
            }
        }
    }

    public void checkout(String commitSHA) throws IOException {
        File commitFile = new File("objects/" + commitSHA);
        if (!commitFile.exists())
        {
            System.out.println("commit doesn't exist");
            return;
        }
        String treeSHA = getTreeSHAFromCommit(commitSHA);
        checkoutTree(treeSHA, "");
    }

}
