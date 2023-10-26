package src.Git;

import java.io.IOException;

public class STierTester {
    public static void main(String[] args) throws Exception {
        Index.initialize();
        Index.addBlob("testFile3");
        Commit initialCommit = new Commit("", "Kian", "first commit");
        Index.markFileAsDeleted("testFile3");
        Index.initialize();
        Commit newCommit = new Commit(initialCommit.getSHA(), "bob", "delete testFile3");
        System.out.println("deleted file");

        Index.initialize();
        Index.addBlob("TestFile4");
        initialCommit = new Commit("", "Mark", "first commit");
        Index.markFileAsEdited("TestFile4");
        Index.initialize();
        Commit newCommit2 = new Commit(initialCommit.getSHA(), "Kian", "Edit TestFile4");
        System.out.println("done");
    }

}
