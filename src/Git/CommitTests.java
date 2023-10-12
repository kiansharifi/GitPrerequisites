package src.Git;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class CommitTests {
    static Commit commit;

    @BeforeEach
    void setup() throws IOException {
        TestUtils.deleteFile("testFile1.txt");
        TestUtils.deleteFile("testFile2.txt");
        TestUtils.deleteFile("testFile3.txt");
        TestUtils.deleteFile("testFile4.txt");
        TestUtils.deleteFile("index");
        TestUtils.deleteDirectory("testDir");
        TestUtils.deleteDirectory("objects");
        TestUtils.deleteFile("testFile1Commit1.txt");
        TestUtils.deleteFile("testFile2Commit1.txt");
        TestUtils.deleteFile("testFile1Commit2.txt");
        TestUtils.deleteFile("testFile2Commit2.txt");
        TestUtils.deleteFile("testFile1Commit3.txt");
        TestUtils.deleteFile("testFile2Commit3.txt");
        TestUtils.deleteFile("testFile1Commit4.txt");
        TestUtils.deleteFile("testFile2Commit4.txt");
        TestUtils.deleteDirectory("testDirCommit3");

        File f = new File("index");
        f.createNewFile();
        File d = new File("objects");
        d.mkdir();

    }

    @Test
    @DisplayName("TestCreateCommit")
    public void testCreateCommit() throws Exception {
        File f = new File("index");
        f.createNewFile();
        File d = new File("objects");
        d.mkdir();
        commit = new Commit("Chris Weng", "Test commit");
        String objectPath = "./objects/";
        Path oP = Paths.get(objectPath); // creates Path
        if (!Files.exists(oP)) // creates file if directory doesnt exist
            Files.createDirectories(oP); // creates Path
    }

    @Test
    void testOneCommit() throws Exception {
        TestUtils.writeStringToFile("testFile1.txt", "Content of test file 1");
        TestUtils.writeStringToFile("testFile2.txt", "Content of test file 2");

        Index.addBlob("testFile1.txt");
        Index.addBlob("testFile2.txt");

        Commit commit = new Commit("Author", "Test commit");

        String treeContent = TestUtils.readFile("./objects/" + commit.getTreeSHA());
        assertTrue(treeContent.contains("testFile1.txt"), "Tree does not contain first file");
        assertTrue(treeContent.contains("testFile2.txt"), "Tree does not contain second file");

        assertEquals("", commit.parentSHA, "Parent SHA1 not empty for the first commit");
        String commitContent = TestUtils.readFile("./objects/" + commit.getSHA());
        assertTrue(commitContent.split("\n")[2].isEmpty(), "Next SHA1 is not empty for the first commit");

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
        assertTrue(firstTreeContent.contains("testFile1.txt"), "Tree of first commit doesnt contain the first file");
        assertTrue(firstTreeContent.contains("testFile2.txt"), "Tree of first commit doesnt contain the second file");
        assertEquals("", firstCommit.getParentSHA(), "Parent SHA1 is not empty for the first commit");
        String firstCommitContent = TestUtils.readFile("./objects/" + firstCommit.getSHA());
        assertTrue(firstCommitContent.split("\n")[2].isEmpty(), "Next SHA1 is not empty for the first commit");

        // Commit 2
        TestUtils.writeStringToFile("testFile3.txt", "Content of test file 3");
        TestUtils.writeStringToFile("testFile4.txt", "Content of test file 4");
        new File("testDir").mkdir();
        TestUtils.writeStringToFile("testDir/fileInDir.txt", "Content of file inside directory");
        Index.addBlob("testFile3.txt");
        Index.addBlob("testFile4.txt");
        Index.addDirectory("testDir");
        Commit secondCommit = new Commit(firstCommit.getSHA(), "Author", "Test commit 2");
        String secondTreeContent = TestUtils.readFile("./objects/" + secondCommit.getTreeSHA());
        assertTrue(secondTreeContent.contains("testFile3.txt"), "Tree of second commit doesnt contain the third file");
        assertTrue(secondTreeContent.contains("testFile4.txt"), "Tree of second commit doesnt contain the fourth file");
        assertTrue(secondTreeContent.contains("testDir"), "Tree of second commit doesn't contain the directory");
        assertEquals(firstCommit.getSHA(), secondCommit.getParentSHA(), "Parent sha1 of the second commit is not the sha1 of the first commit");
        String secondCommitContent = TestUtils.readFile("./objects/" + secondCommit.getSHA());
        assertTrue(secondCommitContent.split("\n")[2].isEmpty(), "Next sha1 value is not empty for the second commit");
    }

    @Test
    void testFourCommits() throws Exception {
        // First Commit
        TestUtils.writeStringToFile("testFile1Commit1.txt", "Content of test file 1 for Commit 1");
        TestUtils.writeStringToFile("testFile2Commit1.txt", "Content of test file 2 for Commit 1");
        Index.addBlob("testFile1Commit1.txt");
        Index.addBlob("testFile2Commit1.txt");
        Commit commit1 = new Commit("Author", "Test commit 1");
        String treeContent1 = TestUtils.readFile("./objects/" + commit1.getTreeSHA());

        // Second Commit
        TestUtils.writeStringToFile("testFile1Commit2.txt", "Content of test file 1 for Commit 2");
        TestUtils.writeStringToFile("testFile2Commit2.txt", "Content of test file 2 for Commit 2");
        Index.addBlob("testFile1Commit2.txt");
        Index.addBlob("testFile2Commit2.txt");
        Commit commit2 = new Commit(commit1.getSHA(), "Author", "Test commit 2");
        String treeContent2 = TestUtils.readFile("./objects/" + commit2.getTreeSHA());

        // Third Commit with directory
        TestUtils.writeStringToFile("testFile1Commit3.txt", "Content of test file 1 for Commit 3");
        TestUtils.writeStringToFile("testFile2Commit3.txt", "Content of test file 2 for Commit 3");
        Index.addBlob("testFile1Commit3.txt");
        Index.addBlob("testFile2Commit3.txt");
        new File("testDirCommit3").mkdirs();
        TestUtils.writeStringToFile("testDirCommit3/fileInDir.txt", "Content of a file inside a directory for Commit 3");
        Index.addDirectory("testDirCommit3");
        System.out.println("Content of Index Before Third Commit: " + Index.reader("index")); // debug
        // String treeContentForTestDir = TestUtils.readFile("./objects/" + "a5d68b616f487b4646db708cd6bcd1a890084ff8"); // debug
        // System.out.println("Content of tree for testDirCommit3: " + treeContentForTestDir);
        Commit commit3 = new Commit(commit2.getSHA(), "Author", "Test commit 3");
        String treeContent3 = TestUtils.readFile("./objects/" + commit3.getTreeSHA());

        // Fourth Commit
        TestUtils.writeStringToFile("testFile1Commit4.txt", "Content of test file 1 for Commit 4");
        TestUtils.writeStringToFile("testFile2Commit4.txt", "Content of test file 2 for Commit 4");
        Index.addBlob("testFile1Commit4.txt");
        Index.addBlob("testFile2Commit4.txt");
        Commit commit4 = new Commit(commit3.getSHA(), "Author", "Test commit 4");
        String treeContent4 = TestUtils.readFile("./objects/" + commit4.getTreeSHA());

        assertTrue(treeContent1.contains("testFile1Commit1.txt"));
        assertTrue(treeContent1.contains("testFile2Commit1.txt"));
        assertEquals("", commit1.getParentSHA());

        assertTrue(treeContent2.contains("testFile1Commit2.txt"));
        assertTrue(treeContent2.contains("testFile2Commit2.txt"));
        assertEquals(commit1.getSHA(), commit2.getParentSHA());

        assertTrue(treeContent3.contains("testFile1Commit3.txt"));
        assertTrue(treeContent3.contains("testFile2Commit3.txt"));
        assertTrue(treeContent3.contains("testDirCommit3"));
        Blob blobFileInDir = new Blob("testDirCommit3/fileInDir.txt");
        System.out.println("Expected sha1 for blobFileInDir: " + blobFileInDir.getSha1()); //debugging
        System.out.println("Content of tree for commit 3: " + treeContent3);
        // assertTrue(treeContent3.contains("blob : " + blobFileInDir.getSha1() + " : fileInDir.txt"));
        assertEquals(commit2.getSHA(), commit3.getParentSHA());

        assertTrue(treeContent4.contains("testFile1Commit4.txt"));
        assertTrue(treeContent4.contains("testFile2Commit4.txt"));
        assertEquals(commit3.getSHA(), commit4.getParentSHA());
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
        TestUtils.deleteFile("testFile1Commit1.txt");
        TestUtils.deleteFile("testFile2Commit1.txt");
        TestUtils.deleteFile("testFile1Commit2.txt");
        TestUtils.deleteFile("testFile2Commit2.txt");
        TestUtils.deleteFile("testFile1Commit3.txt");
        TestUtils.deleteFile("testFile2Commit3.txt");
        TestUtils.deleteFile("testFile1Commit4.txt");
        TestUtils.deleteFile("testFile2Commit4.txt");

        TestUtils.deleteDirectory("testDirCommit3");
    }
}
