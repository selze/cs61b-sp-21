package gitlet;


import java.io.File;
import javax.swing.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

    /** The timestamp of this Commit. */
    private String timestamp;

    private TreeMap<String, String> files;
    private String parent1;
    private String parent2;

    private String ID;


    public Commit(String message, Date date, TreeMap<String, String> files, String parent1, String parent2) {
        this.message = message;
        this.timestamp = dateToTimestamp(date);
        this.files = files;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.ID = sha1(message, timestamp, files.values().toString(), parent1, parent2);
    }
    public String getMessage() {
        return message;
    }
    public String getTimestamp() {
        return timestamp;
    }

    public String getParent1() {
        return parent1;
    }

    public String getParent2() {
        return parent2;
    }

    public TreeMap<String, String> getFiles() {
        return this.files;
    }


    public boolean hasFile(String name) {
        return files.containsKey(name);
    }

    /** Check if the commit has the specified reference to file. */
    public boolean hasVersion(String name, String hash) {
        for (String file : files.keySet()) {
            if (files.get(file).equals(hash) && name.equals(file)) {
                return true;
            }
        }
        return false;
    }



    private static String dateToTimestamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }
    public String toHash() {
        return ID;
    }

    public void addFile(String name, String ID) {
        files.put(name, ID);
    }

}
