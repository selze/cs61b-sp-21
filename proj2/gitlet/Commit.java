package gitlet;


import java.io.File;
import javax.swing.*;
import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *
 *  @author Weitao
 */
public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    static final File COMMIT_FOLDER = join(".gitlet", "commits");

    /** The message of this Commit. */
    private String message;

    /** The time of this Commit. */
    private String time;

    private TreeMap<String, String> files;
    private String parent1;
    private String parent2;


    public Commit(String message, String time, TreeMap<String, String> files, String parent1, String parent2) {
        this.message = message;
        this.time = time;
        this.files = files;
        this.parent1 = parent1;
        this.parent2 = parent2;
    }

    public TreeMap<String, String> getFiles() {
        return this.files;
    }

    /** Return the commit with specific ID. */
    public static Commit fromFile(String ID) {
        File f = join(COMMIT_FOLDER, ID);
        return readObject(f, Commit.class);
    }

    /** Check if the commit has the specified reference to file. */
    public boolean hasFile(String hash) {
        for (String file : files.keySet()) {
            if (files.get(file).equals(hash)) {
                return true;
            }
        }
        return false;
    }

    public void addFile(String name, String ID) {
        files.put(name, ID);
    }

}
