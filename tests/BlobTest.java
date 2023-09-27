package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
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

public class BlobTest {
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

    // Tests byte array to hex string conversion
    @Test
    void testByteToHex() throws IOException {
        Blob blob = new Blob("test_file");

        byte[] empty = {};
        assertEquals("Incorrect behavior for empty array", "", blob.byteToHex(empty));

        byte[] nonEmpty = { 0x00, 0x01, 0x02, 0x03 };
        assertEquals("Incorrect behavior for non-empty array", "00010203", blob.byteToHex(nonEmpty));
    }

    // Tests SHA1 encryption
    @Test
    void testEncryptPassword() throws IOException {
        // SHA1 of "test file contents" is cbaedccfded0c768295aae27c8e5b3a0025ef340
        Blob blob = new Blob("test_file");

        assertEquals("SHA1 hash of file not correct", blob.encryptPassword("test_file"),
                "cbaedccfded0c768295aae27c8e5b3a0025ef340");
    }

    // Tests SHA1 getter
    @Test
    void testGetSha1() throws IOException {
        Blob blob = new Blob("test_file");

        blob.encryptPassword("test_file");

        assertEquals("getSha1() doesn't return last hash of file", blob.getSha1(),
                "cbaedccfded0c768295aae27c8e5b3a0025ef340");
    }

    // Tests file reader
    @Test
    void testReader() throws IOException {
        Blob blob = new Blob("test_file");
        String file_contents = blob.reader("test_file");

        assertEquals("Reader does not return contents of file", file_contents, "test file contents");
    }

    // Tests file writer to objects folder
    @Test
    void testWriteToObjects() throws IOException {
        Index.initialize();

        Blob blob = new Blob("test_file");
        blob.writeToObjects("test_file");

        Path path = Paths.get("objects/" + blob.getSha1());
        assertTrue("Blob file not added to objects", Files.exists(path));

        String originalFileContents = blob.reader("test_file");
        assertEquals("File contents are changed", originalFileContents,
                Files.readString(Path.of(path.toString())));
    }
}
