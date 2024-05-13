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

    LinkedList<String> blobs = new LinkedList<>();
    HashMap<String, Commit> commits = new HashMap<>();
    String currentBranch;


    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** File containing the reference of current HEAD. */
    public static final File HEAD = join(GITLET_DIR, "HEAD");

    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");

    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");
    /** staging area */
    public static final File INDEX_DIR = join(GITLET_DIR, "index");

    public static final File BLOBS_MAP = join(GITLET_DIR, "blobs_map");
    public static final File COMMITS_MAP = join(GITLET_DIR, "commits_map");

    public Repository() {
        if (GITLET_DIR.exists()) {
            blobs = readObject(BLOBS_MAP, LinkedList.class);
            commits = readObject(COMMITS_MAP, HashMap.class);
            currentBranch = readObject(HEAD, String.class);
        }
    }
    public void setUpRepository() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            INDEX_DIR.mkdir();
            BRANCHES_DIR.mkdir();
            Commit initial = new Commit("initial commit", new Date(0).toString(), new TreeMap<>(), null, null);
            String hash = writeCommit(initial);
            setUpBranch("master", hash);
            currentBranch = "master";
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
    }

    public void exit() {
        writeObject(BLOBS_MAP, blobs);
        writeObject(COMMITS_MAP, commits);
        writeObject(HEAD, currentBranch);
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

    public String writeCommit(Commit newCommit) {
        String commitHash = sha1(serialize(newCommit));
        commits.put(commitHash, newCommit);
        return commitHash;
    }

    public static void setUpBranch(String name, String commitHash) {
        File branch = join(BRANCHES_DIR, "name");
        writeContents(branch, commitHash);
    }

    private void setHEAD(String name) {
        currentBranch = name;
    }

    /** Return the reference of the branch. */
    public static String getBranch(String name) {
        File f = join(BRANCHES_DIR, name);
        return readContentsAsString(f);
    }

    private Commit getCurrentCommit() {
        File f = join(BRANCHES_DIR, currentBranch);
        String commitID = readContentsAsString(f);
        return commits.get(commitID);
    }

    private static String getCurrrentCommitHash() {
        File f = readObject(HEAD, File.class);
        return readContentsAsString(f);
    }

    private static String getFileHash(File file) {
        return sha1(readContentsAsString(file));
    }

    public void addFile(String name) {
        checkFile(name);
        File fileToBeAdded = join(CWD, name);
        File stageAddress = join(INDEX_DIR, name);
        String fileHash = getFileHash(fileToBeAdded);
        Commit currentCommit = getCurrentCommit();
        if (currentCommit.hasVersion(fileHash, name)) {
            stageAddress.delete();
        } else {
            writeContents(stageAddress, readContentsAsString(fileToBeAdded));
        }
    }

    /** add the blob to the blob directory */
    public void addBlob(File f) {
        String hash = getFileHash(f);
        File blobFile = join(BLOBS_DIR, hash);
        writeContents(blobFile, readContentsAsString(f));
        blobs.add(hash);
    }

    public static void unstageFile(String name) {

    }

    public void commit(String message) {
        Commit previousCommit = getCurrentCommit();
        String parent1 = getCurrrentCommitHash();
        TreeMap<String, String> files = previousCommit.getFiles();
        ArrayList<String> stagedFiles = (ArrayList<String>) plainFilenamesIn(INDEX_DIR);
        for (String file : stagedFiles) {
            File f = join(INDEX_DIR, file);
            String hash = getFileHash(f);
            files.put(file, hash);
            if (!blobs.contains(hash)) {
                addBlob(f);
            }
            f.delete();
        }
        Commit commit = new Commit(message, new Date().toString(), files, parent1, null);
        String commitID = writeCommit(commit);
        setUpBranch(currentBranch, commitID);
    }

    public void remove(String name) {
        Commit headCommit = getCurrentCommit();
        File CWDFile = join(CWD, name);
        File stageFile = join(INDEX_DIR, name);
        if (CWDFile.exists()) {
            CWDFile.delete();
        }
    }
}
