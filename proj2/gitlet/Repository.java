package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Weitao
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    LinkedList<String> blobs;
    HashMap<String, Commit> commits;


    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The commits' directory. */
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    /** File containing the reference of current HEAD. */
    public static File HEAD = join(GITLET_DIR, "HEAD");

    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");

    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");
    /** staging area */
    public static final File INDEX_DIR = join(GITLET_DIR, "index");

    public Repository() {
        blobs = (LinkedList<String>) plainFilenamesIn(BLOBS_DIR);
        LinkedList<String> commitIDs = (LinkedList<String>) plainFilenamesIn(COMMITS_DIR);
        for (String commitID : commitIDs) {
            commits.put(commitID, Commit.fromFile(commitID));
        }
    }
    public static void setUpRepository() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            COMMITS_DIR.mkdir();
            INDEX_DIR.mkdir();
            BRANCHES_DIR.mkdir();
            String hash = writeCommit(new Commit("initial commit", new Date(0).toString(), new TreeMap<>(), null, null));
            setUpBranch("master", hash);
            setHEAD("master");
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
    }

    public static void checkRepositroy() {
        if (GITLET_DIR.exists()) {
            return;
        }
        System.out.println("Not in an initialized Gitlet directory.");
        System.exit(0);
    }

    /** Check if the file exists in CWD. */
    public static void checkFile(String name) {
        File file = join(CWD, name);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
    }

    public static String writeCommit(Commit newCommit) {
        String commitHash = sha1(serialize(newCommit));
        File commitFile = join(COMMITS_DIR, commitHash);
        writeObject(commitFile, newCommit);
        return commitHash;
    }

    public static void setUpBranch(String name, String commitHash) {
        File branch = join(BRANCHES_DIR, "name");
        writeContents(branch, commitHash);
    }

    public static void setHEAD(String name) {
        File branch = join(BRANCHES_DIR, name);
        if (!branch.exists()) return;
        writeObject(HEAD, branch);
    }

    /** Return the reference of the branch. */
    public static String getBranch(String name) {
        File f = join(BRANCHES_DIR, name);
        return readContentsAsString(f);
    }

    public static Commit getCurrentCommit() {
        File f = readObject(HEAD, File.class);
        String commitID = readContentsAsString(f);
        return Commit.fromFile(commitID);
    }

    public static String getCurrrentCommitHash() {
        File f = readObject(HEAD, File.class);
        return readContentsAsString(f);
    }

    private static String getFileHash(File file) {
        return sha1(readContentsAsString(file));
    }

    public static void addFile(String name) {
        checkFile(name);
        File fileToBeAdded = join(CWD, name);
        File stageAddress = join(INDEX_DIR, name);
        String fileHash = getFileHash(fileToBeAdded);
        Commit currentCommit = getCurrentCommit();
        if (currentCommit.hasFile(fileHash)) {
            stageAddress.delete();
        } else {
            writeContents(stageAddress, readContentsAsString(fileToBeAdded));
        }
    }

    /** add the blob to the blob directory */
    public static void addBlob(File f) {
        String hash = sha1(getFileHash(f));
        LinkedList<String> blobs = (LinkedList<String>) plainFilenamesIn(BLOBS_DIR);
        if (blobs != null) {
            for (String name : blobs) {
                if (name.equals(hash)) {
                    return;
                }
            }
        }
        File blobFile = join(BLOBS_DIR, hash);
        writeContents(blobFile, readContentsAsString(f));
    }

    public static void unstageFile(String name) {

    }

    public void commit(String message) {
        Commit previousCommit = getCurrentCommit();
        String parent1 = getCurrrentCommitHash();
        TreeMap<String, String> files = previousCommit.getFiles();
        ArrayList<String> stagedFiles = (ArrayList<String>) plainFilenamesIn(INDEX_DIR);
        for (String file : stagedFiles) {
            File f = join(CWD, file);
            String hash = getFileHash(f);
            files.put(file, hash);

        }

    }
}
