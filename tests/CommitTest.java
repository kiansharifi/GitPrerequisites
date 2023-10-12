package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

import src.Git.Commit;
import src.Git.TestUtils;

public class CommitTest {
    static Commit commit;

    @BeforeAll
    void setUpBeforeClass() throws Exception {
        File f = new File("index");
        f.createNewFile();
        File d = new File("objects");
        d.mkdir();
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        TestUtils.deleteFile("index");
        TestUtils.deleteDirectory("objects");
    }

    @Test
    @DisplayName("TestCreateCommit")
    public void testCreateCommit() throws Exception {
        File f = new File("index");
        f.createNewFile();
        File d = new File("objects");
        d.mkdir();
        commit = new Commit("Chris Weng", "Test commit"); //PROBLEM: COMMIT IS NULL. NEED TO FIX IT
        String objectPath = "./objects/";
        Path oP = Paths.get(objectPath); // creates Path
        if (!Files.exists(oP)) // creates file if directory doesnt exist
            Files.createDirectories(oP); // creates Path
    }

    // @Test
    // @DisplayName("Test Constructor")
    // public void testConstructor() {
    //     SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    //     Date today = new Date();
    //     String date = formatter.format(today);
    //     assertEquals(date, commit.getDate());
    // }

    
    //Mr. Theiss said to not worry about this. We couldn't fix from the person's code that I got it from.
    /*
    @Test
    @DisplayName("Test write method")
    public void testWrite() throws Exception {
        commit.write();
        File ref = new File(commit.getSHA());
        assertTrue(ref.exists());

        String contents = "\n + \n + \n + Chris Weng + \n" + commit.getDate() + " \n + Test commit";
        assertEquals(contents, commit.readFile(ref));

    }
    */
    
}
