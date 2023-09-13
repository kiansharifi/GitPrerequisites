package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class Blob {
    private String sha1;

    public Blob(String TestFile) throws IOException {
        writeToObjects(TestFile);
    }

    public String reader(String TestFile) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader breader = new BufferedReader(new FileReader(TestFile));
        while (breader.ready()) {
            String s = breader.readLine();
            output.append(s);
        }
        breader.close();
        return output.toString();
    }

    public String encryptPassword(String TestFile) throws IOException {
        String passwordString = reader(TestFile);
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

    public void writeToObjects(String in) throws IOException {
        String hash = encryptPassword(in);
        sha1 = hash;
        String directoryPath = "objects";
        String fileName = hash;
        String filePath = directoryPath + File.separator + fileName;

        // Ensure the directory exists
        Path directoryPathing = Paths.get(directoryPath);
        Files.createDirectories(directoryPathing);

        // Create and write to the file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(reader(in));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSha1() {
        return sha1;
    }

}