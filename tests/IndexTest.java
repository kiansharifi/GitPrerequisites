package tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import src.Git.Blob;
import src.Git.Index;
import src.Git.TestUtils;

public class IndexTest {
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

    // Tests adding a blob, checks index and objects folder
    @Test
    void testAddBlob() throws IOException {
        Index.initialize();

        String sha1 = "";

        Index.addBlob("test_file");
        Blob blob = new Blob("test_file");
        sha1 = blob.getSha1();

        // Check blob exists in the objects folder
        Path path = Paths.get("objects/" + sha1);
        assertTrue("Blob file to add not found", Files.exists(path));

        File index = new File("index");

        // Read file contents
        String indexFileContents = "test_file : " + sha1;
        assertEquals("File contents of index not updated", indexFileContents,
                Files.readString(Path.of(index.getPath())));

        String originalFileContents = blob.reader("test_file");
        assertEquals("File contents of Blob don't match file contents pre-blob creation", originalFileContents,
                Files.readString(Path.of(path.toString())));
    }

    // Checks index and objects folder after initializing
    @Test
    void testInitialize() throws IOException {
        Index.initialize();

        File file = new File("index");
        Path path = Paths.get("objects");

        assertTrue("Index does not exist", file.exists());
        assertTrue("Objects folder does not exist", Files.exists(path));
    }

    // Tests file reader
    @Test
    void testReader() throws IOException {
        String file_contents = Index.reader("test_file");

        assertEquals("Reader does not return contents of file", file_contents, "test file contents");
    }

    // Tests removing a blob
    @Test
    void testRemoveBlob() throws IOException {
        Index.initialize();
        setupRemove();

        Index.removeBlob("test_file");

        File index = new File("index");
        String indexFileContents = "";
        assertEquals("File contents of index not updated", indexFileContents,
                Files.readString(Path.of(index.getPath())));

    }

    void setupRemove() throws IOException {
        FileWriter fw = new FileWriter("objects/cbaedccfded0c768295aae27c8e5b3a0025ef340");
        fw.write("test file contents");
        fw.close();

        FileWriter fw2 = new FileWriter("index");
        fw2.write("test_file : cbaedccfded0c768295aae27c8e5b3a0025ef340");
        fw2.close();
    }
}
