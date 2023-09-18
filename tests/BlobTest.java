package tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import src.Git.Blob;
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

    @Test
    void testByteToHex() throws IOException {
        Blob blob = new Blob("test_file");

        byte[] empty = {};
        assertEquals("Correct behavior for empty array", "", blob.byteToHex(empty));

    }

    @Test
    void testEncryptPassword() {

    }

    @Test
    void testGetSha1() {

    }

    @Test
    void testReader() {

    }

    @Test
    void testWriteToObjects() {

    }
}
