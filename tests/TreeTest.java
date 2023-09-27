package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import src.Git.Index;
import src.Git.TestUtils;
import src.Git.Tree;

public class TreeTest {
    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        TestUtils.writeStringToFile("test_file", "test file contents");
        TestUtils.deleteFile("index");
        TestUtils.deleteDirectory("objects");
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        TestUtils.deleteFile("test_file");
        TestUtils.deleteFile("index");
        TestUtils.deleteDirectory("objects");
    }

    // Tests adding a tree
    @Test
    void testAdd() {
        Tree tree = new Tree();
        tree.add("blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt");

        assertEquals("Incorrect file contents", "blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt\n",
                tree.getFileContents());
    }

    // Tests byte array to hex string conversion
    @Test
    void testByteToHex() throws IOException {
        Tree tree = new Tree();

        byte[] empty = {};
        assertEquals("Incorrect behavior for empty array", "", tree.byteToHex(empty));

        byte[] nonEmpty = { 0x00, 0x01, 0x02, 0x03 };
        assertEquals("Incorrect behavior for non-empty array", "00010203", tree.byteToHex(nonEmpty));
    }

    // Tests file reader
    @Test
    void testGetFileContents() {
        Tree tree = new Tree();
        tree.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");
        tree.add("blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt");
        tree.remove("bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");

        assertEquals("Incorrect file contents", "blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt\n",
                tree.getFileContents());
    }

    // Tests SHA1 encryption
    @Test
    void testGetSha1() throws IOException {
        Tree tree = new Tree();

        assertEquals("SHA1 hash of file not correct", tree.getSha1(Index.reader("test_file")),
                "cbaedccfded0c768295aae27c8e5b3a0025ef340");
    }

    @Test
    void testRemove() {
        Tree tree = new Tree();
        tree.add("blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt");
        tree.remove("file1.txt");

        assertEquals("Incorrect file contents", "", tree.getFileContents());
    }

    @Test
    void testSave() throws IOException {
        Index.initialize();

        Tree tree = new Tree();
        tree.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");
        tree.add("blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt");

        tree.save();

        Path path = Paths.get("objects/" + tree.getSha1(tree.getFileContents().stripTrailing()));

        assertEquals("Incorrect file contents",
                "tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b\nblob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt",
                Files.readString(Path.of(path.toString())));
    }

    //tests addDirectory with basic test
    @Test
    void addDirectoryBasicTest() throws IOException, NoSuchAlgorithmException {
        Tree tree = new Tree();
        Path tempDirectory = Files.createTempDirectory("test1");
        tempDirectory.toFile().deleteOnExit();

        for (int i = 0; i < 3; i++) {
            File file = new File(tempDirectory.toFile(), "file" + i + ".txt");
            FileWriter fw = new FileWriter(file);
            fw.write("content" + i);
            fw.close();
            file.deleteOnExit();
        }

        tree.addDirectory(tempDirectory.toString());
        tree.save();
        String sha1 = tree.getSha1(tree.getFileContents());
        assertNotNull(sha1);
        assertFalse(sha1.isEmpty());
    }

    //tests addDirectory with advanced test
    @Test
    void addDirectoryAdvancedTest() throws IOException, NoSuchAlgorithmException {
        Tree tree = new Tree();
        Path tempDirectory = Files.createTempDirectory("advancedTest");
        tempDirectory.toFile().deleteOnExit();

        for (int i = 0; i < 3; i++) {
            File file = new File(tempDirectory.toFile(), "file" + i + ".txt");
            FileWriter fw = new FileWriter(file);
            fw.write("content" + i);
            fw.close();
            file.deleteOnExit();
        }

        for (int i = 1; i <= 2; i++) {
            Path folder = Files.createDirectory(tempDirectory.resolve("folder" + i));
            folder.toFile().deleteOnExit();
            if (i == 1) {
                File fileInFolder = new File(folder.toFile(), "fileInFolder.txt");
                FileWriter fw = new FileWriter(fileInFolder);
                fw.write("contentInFolder");
                fw.close();
                fileInFolder.deleteOnExit();
            }
        }

        tree.addDirectory(tempDirectory.toString());
        tree.save();
        String sha1 = tree.getSha1(tree.getFileContents());
        assertNotNull(sha1);
        assertFalse(sha1.isEmpty());
    }
}