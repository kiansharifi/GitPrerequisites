package src.Git;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

class TestCommits {

    @BeforeEach
    void setup() throws IOException {
        TestUtils.deleteFile("testFile1.txt");
        TestUtils.deleteFile("testFile2.txt");
        TestUtils.deleteFile("testFile3.txt");
        TestUtils.deleteFile("testFile4.txt");
        TestUtils.deleteFile("index");
        TestUtils.deleteDirectory("testDir");
        TestUtils.deleteDirectory("objects");
    }

    @Test
    void testOneCommit() throws Exception {
        TestUtils.writeStringToFile("testFile1.txt", "Content of test file 1");
        TestUtils.writeStringToFile("testFile2.txt", "Content of test file 2");

        Index.addBlob("testFile1.txt");
        Index.addBlob("testFile2.txt");

        Commit commit = new Commit("Author", "Test commit");

        String treeContent = TestUtils.readFile("./objects/" + commit.getTreeSHA());
        assertTrue(treeContent.contains("testFile1.txt"), "Tree does not contain the first file.");
        assertTrue(treeContent.contains("testFile2.txt"), "Tree does not contain the second file.");

        assertEquals("", commit.parentSHA, "Parent SHA1 is not empty for the first commit.");
        String commitContent = TestUtils.readFile("./objects/" + commit.getSHA());
        assertTrue(commitContent.split("\n")[2].isEmpty(), "Next SHA1 is not empty for the first commit.");

    }

    @Test
    void testTwoCommits() throws Exception {
        // Commit 1
        TestUtils.writeStringToFile("testFile1.txt", "Content of test file 1");
        TestUtils.writeStringToFile("testFile2.txt", "Content of test file 2");
        Index.addBlob("testFile1.txt");
        Index.addBlob("testFile2.txt");
        Commit firstCommit = new Commit("Author", "Test commit 1");
        String firstTreeContent = TestUtils.readFile("./objects/" + firstCommit.getTreeSHA());
        assertTrue(firstTreeContent.contains("testFile1.txt"), "Tree of first commit does not contain the first file.");
        assertTrue(firstTreeContent.contains("testFile2.txt"), "Tree of first commit does not contain the second file.");
        assertEquals("", firstCommit.getParentSHA(), "Parent SHA1 is not empty for the first commit.");
        String firstCommitContent = TestUtils.readFile("./objects/" + firstCommit.getSHA());
        assertTrue(firstCommitContent.split("\n")[2].isEmpty(), "Next SHA1 is not empty for the first commit.");

        // Commit 2
        TestUtils.writeStringToFile("testFile3.txt", "Content of test file 3");
        TestUtils.writeStringToFile("testFile4.txt", "Content of test file 4");
        new File("testDir").mkdir();
        TestUtils.writeStringToFile("testDir/fileInDir.txt", "Content of file inside directory");        Index.addBlob("testFile3.txt");
        Index.addBlob("testFile4.txt");
        Index.addDirectory("testDir");
        Commit secondCommit = new Commit(firstCommit.getSHA(), "Author", "Test commit 2");
        String secondTreeContent = TestUtils.readFile("./objects/" + secondCommit.getTreeSHA());
        assertTrue(secondTreeContent.contains("testFile3.txt"), "Tree of second commit does not contain the third file.");
        assertTrue(secondTreeContent.contains("testFile4.txt"), "Tree of second commit does not contain the fourth file.");
        assertTrue(secondTreeContent.contains("testDir"), "Tree of second commit does not contain the directory.");
        assertEquals(firstCommit.getSHA(), secondCommit.getParentSHA(), "Parent SHA1 of the second commit is not the SHA1 of the first commit.");
        String secondCommitContent = TestUtils.readFile("./objects/" + secondCommit.getSHA());
        assertTrue(secondCommitContent.split("\n")[2].isEmpty(), "Next SHA1 is not empty for the second commit.");
    }

    @AfterEach
    void tearDown() throws IOException {
        TestUtils.deleteFile("testFile1.txt");
        TestUtils.deleteFile("testFile2.txt");
        TestUtils.deleteFile("testFile3.txt");
        TestUtils.deleteFile("testFile4.txt");
        TestUtils.deleteFile("index");
        TestUtils.deleteDirectory("testDir");
        TestUtils.deleteDirectory("objects");
    }
}
