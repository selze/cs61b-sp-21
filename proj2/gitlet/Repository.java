package gitlet;

import java.io.File;
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
    String currentBranch;
    HashMap<String, String> addStage = new HashMap<>();
    LinkedList<String> removeStage = new LinkedList<>();


    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** File containing the reference of current HEAD. */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    /** branches directory. */
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");

    /** staging area */
    public static final File INDEX_DIR = join(GITLET_DIR, "index");
    public static final File ADD_STAGE = join(INDEX_DIR, "add_stage");
    public static final File REMOVE_STAGE = join(INDEX_DIR, "remove_stage");

    public static final File BLOBS_MAP = join(GITLET_DIR, "blobs_map");

    public Repository() {
        if (GITLET_DIR.exists()) {
            currentBranch = readObject(HEAD, String.class);
            loadStageArea();
            blobs = readObject(BLOBS_MAP, LinkedList.class);
        }
    }


    private void loadStageArea() {
        addStage = readObject(ADD_STAGE, HashMap.class);
        removeStage = readObject(REMOVE_STAGE, LinkedList.class);

    }

    private void saveStageArea() {
        writeObject(ADD_STAGE, addStage);
        writeObject(REMOVE_STAGE, removeStage);
    }

    public void setUpRepository() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            INDEX_DIR.mkdir();
            BRANCHES_DIR.mkdir();
            Commit initial = new Commit("initial commit", new Date(0) , new TreeMap<>(), null, null);
            writeCommit(initial);
            String hash = writeCommit(initial);
            setUpBranch("master", hash);
            currentBranch = "master";
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
    }

    public String writeCommit(Commit commit) {
        String commitID = commit.toHash();
        File f = join(COMMITS_DIR, commitID);
        writeObject(f, commit);
        return commitID;
    }


    public static void checkRepositroy() {
        if (GITLET_DIR.exists()) {
            return;
        }
        System.out.println("Not in an initialized Gitlet directory.");
        System.exit(0);
    }

    /** Check if the file exists in CWD. */
    private static void checkFile(String name) {
        File file = join(CWD, name);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
    }


    private void setUpBranch(String name, String commitHash) {
        File branch = join(BRANCHES_DIR, name);
        writeContents(branch, commitHash);
    }

    private void setHEAD(String name) {
        currentBranch = name;
    }

    private Commit getCurrentCommit() {
        File f = join(BRANCHES_DIR, currentBranch);
        String commitID = readContentsAsString(f);
        File commitFile = join(COMMITS_DIR, commitID);
        return readObject(commitFile, Commit.class);
    }


    private static String getFileHash(File file) {
        return sha1(readContentsAsString(file));
    }

    public void addFile(String name) {
        checkFile(name);
        File fileToBeAdded = join(CWD, name);
        String fileHash = getFileHash(fileToBeAdded);
        Commit currentCommit = getCurrentCommit();
        if (currentCommit.hasVersion(name, fileHash)) {
            addStage.remove(name);
        } else {
            addStage.put(name, fileHash);
            addBlob(fileToBeAdded, fileHash);
        }
        saveStageArea();
    }

    /** add the blob to the blob directory */
    private void addBlob(File f, String fileHash) {
        if (blobs.contains(fileHash)) return;
        File blobFile = join(BLOBS_DIR, fileHash);
        writeContents(blobFile, readContentsAsString(f));
        blobs.add(fileHash);
    }


    public void commit(String message) {
        if (addStage.isEmpty() && removeStage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit previousCommit = getCurrentCommit();
        String parent1 = previousCommit.toHash();
        TreeMap<String, String> files = previousCommit.getFiles();
        for (String file : addStage.keySet()) {
            files.put(file, addStage.get(file));
        }
        for (String file : removeStage) {
            files.remove(file);
        }
        Commit commit = new Commit(message, new Date(), files, parent1, null);
        String commitID = commit.toHash();
        setUpBranch(currentBranch, commitID);
        addStage.clear();
        removeStage.clear();
        saveStageArea();
    }

    private boolean isTrackedByCurrentCommit(String name, File file) {
        String fileID = getFileHash(file);
        Commit headCommit = getCurrentCommit();
        return headCommit.hasVersion(name, fileID);
    }

    public void remove(String name) {
        File CWDFile = join(CWD, name);
        Boolean flag = isTrackedByCurrentCommit(name, CWDFile);
        if (CWDFile.exists() && flag) {
            addStage.remove(name);
            removeStage.add(name);
            restrictedDelete(CWDFile);
        } else if (!addStage.containsKey(name) && !flag) {
            System.out.println("No reason to remove the file");
            System.exit(0);
        } else {
            addStage.remove(name);
        }
        saveStageArea();
    }

    private void printCommit(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.toHash());
        if (commit.getParent2() != null) {
            System.out.println("Merge: " + commit.getParent1().substring(0, 7) + " " + commit.getParent2().substring(0, 7));
        }
        System.out.println("Date: " + commit.getTimestamp());
        System.out.println(commit.getMessage());
        System.out.println();
    }

    public void log() {
        Commit p = getCurrentCommit();
        String parentID = p.getParent1();
        printCommit(p);
        while(parentID != null) {
            File f = join(COMMITS_DIR, parentID);
            p = readObject(f, Commit.class);
            parentID = p.getParent1();
            printCommit(p);
        }
    }

    private Commit getCommit(String ID) {
        File f = join(COMMITS_DIR, ID);
        if (!f.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        return readObject(f, Commit.class);
    }

    private Commit getBranchCommit(String name) {
        File f = join(BRANCHES_DIR, name);
        String ID = readContentsAsString(f);
        return getCommit(ID);
    }

    public void global_log() {
        LinkedList<String> files = (LinkedList<String>) plainFilenamesIn(COMMITS_DIR);
        for (String file : files) {
            printCommit(getCommit(file));
        }
    }

    public void find(String message) {
        LinkedList<String> files = (LinkedList<String>) plainFilenamesIn(COMMITS_DIR);
        int flag = 0;
        for (String file : files) {
            Commit p = getCommit(file);
            if (p.getMessage().equals(message)) {
                System.out.println(p.toHash());
                flag += 1;
            }
        }
        if (flag == 0) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    private void printBranches() {
        System.out.println("=== Branches ===");
        LinkedList<String> branches = (LinkedList<String>) plainFilenamesIn(BRANCHES_DIR);
        for (String branch : branches) {
            if (branch.equals(currentBranch)) {
                System.out.print("*");
            }
            System.out.println(branch);
        }
        System.out.println();
    }

    private void printStagedFiles() {
        System.out.println("=== Staged Files ===");
        for (String file : addStage.keySet()) {
            System.out.println(file);
        }
        System.out.println();
    }

    private void printRemovedFiles() {
        System.out.println("=== Removed Files ===");
        for (String file : removeStage) {
            System.out.println(file);
        }
        System.out.println();
    }


    public void status() {
        printBranches();
        printStagedFiles();
        printRemovedFiles();
        System.out.println();
    }

    private boolean hasBranch(String name) {
        File f = join(BRANCHES_DIR, name);
        return f.exists();
    }

    public void branch(String name) {
        if (hasBranch(name)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        setUpBranch(name, getCurrentCommit().toHash());
    }

    public void rm_branch(String name) {
        if (!hasBranch(name)) {
            System.out.println("A branch with that name does not exists.");
            System.exit(0);
        }
        if (name.equals(currentBranch)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        File f = join(BRANCHES_DIR, name);
        f.delete();
    }

    public void checkoutFile(String name) {
        Commit currentCommit = getCurrentCommit();
        if (!currentCommit.hasFile(name)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File CWDFile = join(CWD, name);
        String fileID = currentCommit.getFiles().get(name);
        File f = join(BLOBS_DIR, fileID);
        writeContents(CWDFile, readContentsAsString(f));
    }


    public void checkout(String commitID, String name) {
        Commit commit = getCommit(commitID);
        if (!commit.hasFile(name)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File CWDFile = join(CWD, name);
        String fileID = commit.getFiles().get(name);
        File f = join(BLOBS_DIR, fileID);
        writeContents(CWDFile, readContentsAsString(f));
    }

    public void checkoutBranch(String branch) {
        if (!hasBranch(branch)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (currentBranch.equals(branch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        checkoutBranchHelper(getBranchCommit(branch).toHash());
        currentBranch = branch;
    }

    private void checkoutBranchHelper(String ID) {
        Commit commit = getCommit(ID);
        LinkedList<String> files = (LinkedList<String>) plainFilenamesIn(CWD);
        for (String file : files) {
            File f = join(CWD, file);
            if (!isTrackedByCurrentCommit(file, f)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (String file : files) {
            File f = join(CWD, file);
            restrictedDelete(f);
        }
        for (String name : commit.getFiles().keySet()) {
            File CWDFile = join(CWD, name);
            File f = join(BLOBS_DIR, commit.getFiles().get(name));
            writeContents(CWDFile, readContentsAsString(f));
        }
        addStage.clear();
        removeStage.clear();
        saveStageArea();
    }

    public void reset(String commitID) {
        checkoutBranchHelper(commitID);
        setUpBranch(currentBranch, commitID);
    }

    public void merge(String branch) {

    }
}
