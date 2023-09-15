package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import src.Git.Blob;
import src.Git.Index;
import src.Git.Tree;
import src.Git.Utils;

public class UnitTester {

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        Utils.writeStringToFile("junit_example_file_data.txt", "test file contents");
        Utils.deleteFile("index");
        Utils.deleteDirectory("objects");
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        Utils.deleteFile("junit_example_file_data.txt");
        Utils.deleteFile("index");
        Utils.deleteDirectory("objects");
    }

    @Test
    @DisplayName("[8] Test if initialize and objects are created correctly")
    void testInitialize() throws Exception {

        // Run the person's code
        Index.initialize();

        // check if the file exists
        File file = new File("index");
        Path path = Paths.get("objects");

        assertTrue(file.exists());
        assertTrue(Files.exists(path));
    }

    @Test
    @DisplayName("[15] Test if adding a tree")
    void testCreateTree() throws Exception {
        String sha1 = "3edd7b21427d8b1dc28a9633c7dcb922492af6e0";
        String fileContents = "tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b\nblob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt";
        try {
            Tree tree = new Tree();
            tree.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");
            tree.add("blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt");
            tree.save();
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }

        // Check blob exists in the objects folder
        File file_junit1 = new File("objects/" + sha1);
        assertTrue("Tree file to add not found", file_junit1.exists());

        // Read file contents
        assertEquals("File contents of Blob don't match file contents pre-blob creation", fileContents,
                Files.readString(Path.of((file_junit1.getPath()))));
    }

    @Test
    @DisplayName("[15] Test if adding a blob works.  5 for sha, 5 for file contents, 5 for correct location")
    void testCreateBlob() throws Exception {
        String sha1 = "";
        try {

            // Manually create the files and folders before the 'testAddFile'
            // MyGitProject myGitClassInstance = new MyGitProject();
            // myGitClassInstance.init();
            Index.initialize();

            // TestHelper.runTestSuiteMethods("testCreateBlob", file1.getName());
            Index.addBlob("junit_example_file_data.txt");
            Blob blob = new Blob("junit_example_file_data.txt");
            sha1 = blob.getSha1();
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }

        // Check blob exists in the objects folder
        File file_junit1 = new File("objects/" + sha1);
        assertTrue("Blob file to add not found", file_junit1.exists());

        File index = new File("index");

        // Read file contents
        String indexFileContents = "junit_example_file_data.txt : cbaedccfded0c768295aae27c8e5b3a0025ef340";
        assertEquals("File contents of Blob don't match file contents pre-blob creation", indexFileContents,
                Files.readString(Path.of(index.getPath())));
    }
}
