package gitlet;

import java.io.File;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

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
    String currentBranch = "";
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

    @SuppressWarnings("unchecked")
    public Repository() {
        if (GITLET_DIR.exists()) {
            currentBranch = readContentsAsString(HEAD);
            loadStageArea();
            blobs = readObject(BLOBS_MAP, LinkedList.class);
        }
    }

    @SuppressWarnings("unchecked")
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
            COMMITS_DIR.mkdir();
            BLOBS_DIR.mkdir();
            Commit initial = new Commit("initial commit", new Date(0) , new TreeMap<>(), "", "");
            String hash = writeCommit(initial);
            setUpBranch("master", hash);
            currentBranch = "master";
            setHEAD("master");
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
    }

    public void exit() {
        saveStageArea();
        saveBlobMap();
        setHEAD(currentBranch);
    }

    private void saveBlobMap() {
        writeObject(BLOBS_MAP, blobs);
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
        writeContents(HEAD, name);
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
        if (removeStage.contains(name)) {
            removeStage.remove(name);
        }
    }

    /** add the blob to the blob directory */
    private void addBlob(File f, String fileHash) {
        if (blobs.contains(fileHash)) return;
        File blobFile = join(BLOBS_DIR, fileHash);
        writeContents(blobFile, readContentsAsString(f));
        blobs.add(fileHash);
    }

    public void remove(String name) {
        File f = join(CWD, name);
        boolean isTracked = getCurrentCommit().hasFile(name);
        boolean isStaged = addStage.containsKey(name);
        if (isStaged) {
            addStage.remove(name);
        }
        if (isTracked) {
            restrictedDelete(f);
            removeStage.add(name);
        }
        if (!isTracked && !isStaged) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }


    public void commit(String message) {
        if (addStage.isEmpty() && removeStage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit previousCommit = getCurrentCommit();
        commitHelper(message, previousCommit, null);
    }

    private void commitHelper(String message, Commit commit1, Commit commit2) {
        TreeMap<String, String> files = commit1.getFiles();
        String parent1 = commit1.toHash();
        String parent2 = "";
        if (commit2 != null) {
            files.putAll(commit2.getFiles());
            parent2 = commit2.toHash();
        }
        for (String file : addStage.keySet()) {
            files.put(file, addStage.get(file));
        }
        for (String file : removeStage) {
            files.remove(file);
        }
        Commit newCommit = new Commit(message, new Date(), files, parent1, parent2);
        String commitID = writeCommit(newCommit);
        setUpBranch(currentBranch, commitID);
        addStage.clear();
        removeStage.clear();
    }


    private boolean isTrackedByCurrentCommit(String name, File file) {
        String fileID = getFileHash(file);
        Commit headCommit = getCurrentCommit();
        return headCommit.hasFile(name);
    }



    private void printCommit(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.toHash());
        if (!commit.getParent2().isEmpty()) {
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
        while(!parentID.isEmpty()) {
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
        List<String> files = plainFilenamesIn(COMMITS_DIR);
        for (String file : files) {
            printCommit(getCommit(file));
        }
    }

    public void find(String message) {
        List<String> files = plainFilenamesIn(COMMITS_DIR);
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
        List<String> branches = plainFilenamesIn(BRANCHES_DIR);
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
        List<String> sortedKeys = addStage.keySet().stream().sorted().collect(Collectors.toList());
        for (String file : sortedKeys) {
            System.out.println(file);
        }
        System.out.println();
    }

    private void printRemovedFiles() {
        System.out.println("=== Removed Files ===");
        List<String> sorted = removeStage.stream().sorted().collect(Collectors.toList());
        for (String file : removeStage) {
            System.out.println(file);
        }
        System.out.println();
    }

    private void printModifiedButNotStaged() {
        System.out.println("=== Modifications Not Staged For Commit ===");
    }

    private void printUntrackedFiles() {
        System.out.println("=== Untracked Files ===");
    }


    public void status() {
        printBranches();
        printStagedFiles();
        printRemovedFiles();
        printModifiedButNotStaged();
        printUntrackedFiles();
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



    private String hasCommitAbbreviate(String ID) {
        List<String> commits = plainFilenamesIn(COMMITS_DIR);
        for (String commit : commits) {
            if (commit.substring(0, 7).equals(ID.substring(0, 7))) {
                return commit;
            }
        }
        return null;
    }

    public void checkout(String ID, String name) {
        String commitID;
        if (ID.length() != 40) {
            commitID = hasCommitAbbreviate(ID);
            if (commitID == null) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
        } else {
            commitID = ID;
        }
        Commit commit = getCommit(commitID);
        checkoutFileHelper(commit, name);
    }

    private void checkoutFileHelper(Commit commit, String name) {
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
        setHEAD(branch);
    }

    private void checkoutBranchHelper(String ID) {
        Commit commit = getCommit(ID);
        List<String> files = plainFilenamesIn(CWD);
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

    public void reset(String ID) {
        String commitID;
        if (ID.length() != 40) {
            commitID = hasCommitAbbreviate(ID);
            if (commitID == null) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
        } else {
            commitID = ID;
        }
        checkoutBranchHelper(commitID);
        setUpBranch(currentBranch, commitID);
    }

    private class CommitGraph {
        private Map<String, List<String>> adjList = new HashMap<>();

        public void addCommit(String commit, List<String> parents) {
            adjList.put(commit, parents);
        }

        public String findLatestCommonAncestor(String start1, String start2) {
            Set<String> visited1 = new HashSet<>();
            Set<String> visited2 = new HashSet<>();
            Queue<String> queue1 = new LinkedList<>();
            Queue<String> queue2 = new LinkedList<>();


            visited1.add(start1);
            visited2.add(start2);
            queue1.offer(start1);
            queue2.offer(start2);

            while (!(queue1.isEmpty()) && !(queue2.isEmpty())) {
                String result = processQueue(queue1, visited1, visited2);
                if (result != null) return result;
                result = processQueue(queue2, visited2, visited1);
                if (result != null) return result;
            }
            return null;
        }

        private String processQueue(Queue<String> queue, Set<String> visited, Set<String> othervisited) {
            if (!queue.isEmpty()) {
                String current = queue.poll();
                List<String> parents = adjList.getOrDefault(current, Collections.emptyList());
                for (String parent : parents) {
                    if (othervisited.contains(parent)) {
                        return parent;
                    }
                    if (visited.add(parent)) {
                        queue.offer(parent);
                    }
                }
            }
            return null;
        }
    }

    private String findLatestCommonAncestor(String commit1, String commit2) {
        CommitGraph graph = new CommitGraph();
        List<String> commitsNames = plainFilenamesIn(COMMITS_DIR);
        for (String commit : commitsNames) {
            Commit c = getCommit(commit);
            List<String> parents = new ArrayList<>();
            String parent1 = c.getParent1();
            String parent2 = c.getParent2();
            if (!parent1.isEmpty()) parents.add(parent1);
            if (!parent2.isEmpty()) parents.add(parent2);
            graph.addCommit(commit, parents);
        }
        return graph.findLatestCommonAncestor(commit1, commit2);
    }

    public void merge(String branch) {
        boolean conflictFlag = false;
        if (!addStage.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        } else if (!hasBranch(branch)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        } else if (branch.equals(currentBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        Commit currentCommit = getCurrentCommit();
        Commit branchCommit = getBranchCommit(branch);
        String ancestor = findLatestCommonAncestor(currentCommit.toHash(), branchCommit.toHash());

        if (ancestor.equals(branchCommit.toHash())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        } else if (ancestor.equals(currentBranch)) {
            checkoutBranch(branch);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        Commit splitCommit = getCommit(ancestor);

        List<String> totalFiles = new ArrayList<>();

        totalFiles.addAll(branchCommit.getFiles().keySet());
        totalFiles.addAll(currentCommit.getFiles().keySet());
        totalFiles.addAll(splitCommit.getFiles().keySet());

        for (String file : totalFiles) {
            boolean isInSplit = splitCommit.hasFile(file);
            boolean isInCurrent = currentCommit.hasFile(file);
            boolean isInBranch = branchCommit.hasFile(file);
            //4
            if (!isInSplit && isInCurrent && !isInBranch) continue;
            //5
            if (!isInSplit && !isInCurrent && isInBranch) {
                checkoutFileHelper(branchCommit, file);
                addStage.put(file, branchCommit.getFiles().get(file));
                continue;
            }
            if (isInSplit) {
                String fileHashInSplit = splitCommit.getFiles().get(file);
                boolean isModifiedInCurrent = isModified(currentCommit, file,fileHashInSplit);
                boolean isModifiedInBranch = isModified(branchCommit, file, fileHashInSplit);
                if (!isInCurrent && !isInBranch) continue;
                if (isInCurrent && isInBranch) {
                    //1
                    if (isModifiedInBranch && !isModifiedInCurrent) {
                        checkoutFileHelper(branchCommit, file);
                        addStage.put(file, branchCommit.getFiles().get(file));
                        continue;
                    }
                    //2
                    if (!isModifiedInBranch && isModifiedInCurrent) {
                        continue;
                    }
                    //8
                    if (isModifiedInBranch && isModifiedInCurrent) {
                        String fileHashInBranch = branchCommit.getFiles().get(file);
                        String fileHashInCurrent = currentCommit.getFiles().get(file);
                        if (fileHashInCurrent.equals(fileHashInBranch)) continue;
                        File CWDFile = join(CWD,file);
                        File currentFile = join(BLOBS_DIR, fileHashInCurrent);
                        File branchFile = join(BLOBS_DIR, fileHashInBranch);
                        writeContents(CWDFile, "<<<<<<< HEAD\n", readContentsAsString(currentFile),
                                "=======\n", readContentsAsString(branchFile), ">>>>>>>");
                        addFile(file);
                        conflictFlag = true;
                    }
                }
                //7
                if (!isInCurrent && isInBranch) {
                    removeStage.add(file);
                    continue;
                }
                //6
                if (isInCurrent && !isInBranch) {
                    if (isModifiedInBranch && !isModifiedInCurrent) {
                        checkoutFileHelper(branchCommit, file);
                        removeStage.add(file);
                        continue;
                    }
                }
            }
        }

        commitHelper("Merged " + branch + " into " + currentBranch + ".", currentCommit, branchCommit);
        if (conflictFlag) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private boolean isModified(Commit commit,String fileName, String fileHash) {
        return commit.hasVersion(fileName, fileHash);
    }

}
