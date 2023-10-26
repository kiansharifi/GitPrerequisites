package src.Git;

public class STierTester {

    public static void main(String[] args) throws Exception {
        deleteFileTest();

        // Test edit file functionality
        editFileTest();
    }

    public static void deleteFileTest() throws Exception {
        System.out.println("Testing Delete File functionality:");

        // Create an initial commit with a file
        Index.initialize();
        Index.addBlob("fileToDelete.txt");
        Commit initialCommit = new Commit("", "John", "Initial commit");

        // Mark the file for deletion
        Index.markFileAsDeleted("fileToDelete.txt");

        // Create a new commit after marking the file for deletion
        Index.initialize();
        Commit newCommit = new Commit(initialCommit.getSHA(), "Alice", "Delete fileToDelete.txt");

        System.out.println("File deletion test completed.");
    }

    public static void editFileTest() throws Exception {
        System.out.println("Testing Edit File functionality:");

        // Create an initial commit with a file
        Index.initialize();
        Index.addBlob("fileEdited.txt");
        Commit initialCommit = new Commit("", "John", "Initial commit");

        // Mark the file as edited
        Index.markFileAsEdited("fileEdited.txt");

        // Create a new commit after marking the file as edited
        Index.initialize();
        Commit newCommit = new Commit(initialCommit.getSHA(), "Alice", "Edit fileEdited.txt");

        System.out.println("File edit test completed.");
    }
}
