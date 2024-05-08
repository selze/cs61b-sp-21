package capers;



import java.io.*;
import java.util.Scanner;

import static capers.Utils.*;

/** A repository for Capers 
 * @author TODO
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = new File(".capers");

    /**
     * Does require filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() {
        CAPERS_FOLDER.mkdir();
        File DOG_FOLDER = join(CAPERS_FOLDER, "dogs");
        File STORY = join(CAPERS_FOLDER, "story");
        try {
            if (!DOG_FOLDER.exists()) {
                DOG_FOLDER.mkdir();
            }
            if (!STORY.exists()) {
                STORY.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        File f = join(CAPERS_FOLDER, "story");
        writeContents(f, readContentsAsString(f), text, "\n");
        try {
            Scanner scanner = new Scanner(f);
            while(scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("file not found: " + e.getMessage());
        }

    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        Dog m = new Dog(name, breed, age);
        m.saveDog();
        System.out.println(m.toString());
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        File f = join(Dog.DOG_FOLDER, name);
        Dog m = readObject(f, Dog.class);
        m.haveBirthday();
        m.saveDog();
    }
}
