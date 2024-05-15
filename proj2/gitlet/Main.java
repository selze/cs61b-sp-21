package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Weitao
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init": {
                Repository repo = new Repository();
                repo.setUpRepository();
                break;
            }
            case "add": {
                Repository.checkRepositroy();
                validateNumArgs(args, 2, "Incorrect operands.");
                Repository repo = new Repository();
                repo.addFile(args[1]);
                break;
            }
            case "commit": {
                Repository.checkRepositroy();
                validateNumArgs(args, 2, "Please enter a commit message.");
                Repository repo = new Repository();
                repo.commit(args[1]);
                break;
            }
            case "log": {
                Repository.checkRepositroy();
                Repository repo = new Repository();
                repo.log();
                break;
            }
            case "rm": {
                Repository.checkRepositroy();
                validateNumArgs(args, 2, "Incorrect operands.");
                Repository repo = new Repository();
                repo.remove(args[1]);
                break;
            }
            case "global-log": {
                Repository.checkRepositroy();
                Repository repo = new Repository();
                repo.global_log();
                break;
            }
            case "find": {
                Repository.checkRepositroy();
                validateNumArgs(args, 2, "Incorrect operands.");
                Repository repo = new Repository();
                repo.find(args[1]);
                break;
            }
            case "status": {
                Repository.checkRepositroy();
                Repository repo = new Repository();
                repo.status();
                break;
            }
            case "checkout": {
                Repository.checkRepositroy();
                Repository repo = new Repository();
                if (args.length == 2) {
                    repo.checkoutBranch(args[1]);
                } else if (args.length == 3 && args[1].equals("--")) {
                    repo.checkoutFile(args[2]);
                } else if (args.length == 4 && args[2].equals("--")) {
                    repo.checkout(args[1], args[3]);
                } else {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                break;
            }
            case "branch": {
                Repository.checkRepositroy();
                validateNumArgs(args, 2, "Incorrect operands.");
                Repository repo = new Repository();
                repo.branch(args[1]);
                break;
            }
            case "rm-branch": {
                Repository.checkRepositroy();
                validateNumArgs(args, 2, "Incorrect operands.");
                Repository repo = new Repository();
                repo.rm_branch(args[1]);
                break;
            }
            case "reset": {
                Repository.checkRepositroy();
                validateNumArgs(args, 2, "Incorrect operands.");
                Repository repo = new Repository();
                repo.reset(args[1]);
                break;
            }
            case "merge": {
                Repository.checkRepositroy();
                Repository repo = new Repository();
                repo.reset(args[1]);
                break;
            }
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    public static void validateNumArgs(String[] args, int n, String message) {
        if (args.length != n) {
            System.out.println(message);
            System.exit(0);
        }
    }

}
