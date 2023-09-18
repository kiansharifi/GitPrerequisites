package tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

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

    @Test
    void testAdd() {

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

        assertEquals("Incorrect file contents", "blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt",
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

    }

    @Test
    void testSave() {

    }
}
