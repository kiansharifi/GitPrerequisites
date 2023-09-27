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
    static void setUpBeforeClass() throws Exception {
        commit = new Commit("Chris Weng", "Test commit");
        String objectPath = "./objects/";
        Path oP = Paths.get(objectPath); // creates Path
        if (!Files.exists(oP)) // creates file if directory doesnt exist
            Files.createDirectories(oP); // creates Path
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        TestUtils.deleteFile("index");
        TestUtils.deleteDirectory("objects");
    }

    @Test
    @DisplayName("Test Constructor")
    public void testConstructor() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = new Date();
        String date = formatter.format(today);
        assertEquals(date, commit.getDate());
    }

    @Test
    @DisplayName("Test write method")
    public void testWrite() throws Exception {
        commit.write();
        File ref = new File(commit.getFileName());
        assertTrue(ref.exists());

        String contents = "\n + \n + \n + Chris Weng + \n" + commit.getDate() + " \n + Test commit";
        assertEquals(contents, commit.readFile(ref));

    }
}
