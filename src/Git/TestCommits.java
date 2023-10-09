package src.Git;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestCommits {

    @Test
    void TestOneCommit () throws Exception {

        TestUtils.writeStringToFile("testFile1.txt", "Content of test file 1");
        TestUtils.writeStringToFile("testFile2.txt", "Content of test file 2");
        Index.addBlob("testFile1.txt");
        Index.addBlob("testFile2.txt");

        Commit commit = new Commit("Author", "Test commit");


        assertNotNull(commit.treeSHA); 

        assertEquals("", commit.parentSHA);

        String nextSHA = "";
        assertEquals("", nextSHA);

        TestUtils.deleteFile("testFile1.txt");
        TestUtils.deleteFile("testFile2.txt");
        TestUtils.deleteFile("./objects/" + commit.getSHA());
    }
}