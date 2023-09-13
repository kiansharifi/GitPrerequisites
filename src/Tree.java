package src;

import java.io.FileWriter;
import java.util.Scanner;

public class Tree {

    String fileContents = "";

    public String getFileContents() {
        return fileContents;
    }

    public void addEntry(String contents) {
        String[] parts = contents.split(" : ");
        String shaOfFile = parts[1];
        String optionalFileName = parts.length > 1 ? parts[2] : "";

        if (!fileContents.contains(shaOfFile)
                && (optionalFileName.isEmpty() || (!fileContents.contains(optionalFileName)))) {
            fileContents += contents + "\n";
        }
    }

    public void removeEntry(String entry) {
        String newFileContents = "";

        Scanner inContents = new Scanner(fileContents);

        while (inContents.hasNextLine()) {
            String line = inContents.nextLine();
            if (!line.contains(entry)) {
                newFileContents += line + "\n";
            }
        }

        inContents.close();

        fileContents = newFileContents;
    }

}
