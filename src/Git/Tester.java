package src.Git;

import java.io.IOException;

public class Tester {
    public static void main(String[] args) throws IOException {
        Tree tree = new Tree();
        tree.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");
        tree.add("blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt");
        tree.save();
    }

}