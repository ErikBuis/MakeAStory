/* Written by Erik Buis
 *
 * RunAStoryTerminal.java:
 * -
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import CompileAStory.CompileAStory;
import WriteAStory.*;

public class RunAStoryTerminal {
    private static final Scanner scanTerminal = new Scanner(System.in);
    /* Compile the story and store it in a story instance. */
    private static final Story story = CompileAStory.compileFullStory(inputStoryName(scanTerminal));
    /* This ArrayList contains all sections that the user read so far, so that optifread and optifnotread can check if
     * the user actually read a paragraph. It is also useful when the user reads a section containing a logbookPart,
     * because it would be automatically added to this ArrayList.
     */
    private static ArrayList<Marking> sectionsRead = new ArrayList<Marking>();

    public static void main(String[] args) {
        System.out.println("\nHave fun reading!!!\n\n\n\n");
        String stripes = "-".repeat((80 - story.getTitle().length()) / 2);
        System.out.println(stripes + story.getTitle().toUpperCase() + stripes + "\n");
        System.out.println("Author: " + story.getAuthor());
        System.out.println("Programmer: Erik Buis");
        System.out.println("\n");

        /* Start running the first section. Then run the section at the marking returned. */
        Marking running = story.getStartingPosition();

        do {
            /* Get the section that has to be run from the marking runFrom. */
            Section currentSection = story.getChapter(running.getChapterName()).getSection(running.getSectionIndex());

            /* Print all the paragraphs and give the user the option to type in a command (e.g. "logbook" to see the
             * current state of the logbook) after each paragraph.
             */
            printParagraphs(currentSection, running.getParagraphIndex());

            /* Add the current position to the ArrayList containing all markings of sections read by the user so far. If
             * the section has already been read before, it shouldn't be added, because duplicate markings could cause
             * user commands to be not working (such as "/logbook" printing a logbookPart multiple times).
             */
            if (!hasUserRead(running))
                sectionsRead.add(running);

            /* Let the user choose one of the options, so that he can be redirected to another point in the story. */
            Option[] availableOptions = getAvailableOptions(currentSection);
            running = letUserChooseOption(availableOptions);

            System.out.println();
        /* Check if the new marking points to the end of the story (the chapter name is then equal to null). */
        } while (running.getChapterName() != null);

        System.out.println("THE END");
        scanTerminal.nextLine();
        System.out.println("\n\n\n---------------------CREDITS---------------------\n");
        System.out.println("Author: " + story.getAuthor());
        System.out.println("Programmer: Erik Buis");
        scanTerminal.nextLine();
        System.out.println("\n\n\n");
        System.out.println("THANKS FOR READING!");
    }

    private static File inputStoryName(Scanner scanTerminal) {
        System.out.println("Welcome to the run program of Write A Story!");

        do {
            System.out.println("\nPlease pick the story you want to compile and/or read. This has to be an existing"
            + " directory in SavedStories.");
            executeReaderCommand("/stories", false);

            String input = scanTerminal.nextLine();

            /* Get file object of directory. */
            File storyDir = new File("SavedStories", input);

            /* Check if the given directory exists. */
            if (storyDir.exists() && storyDir.isDirectory() && input != "" && input != "." && input != "..") {
                return storyDir;
            } else {
                System.out.println("The given directory does not exist.");
            }
        } while (true);
    }

    /* This method prints all the paragraphs in in the current section. It also calls the method readUserInput
     * internally after a paragraph has been printed.
     */
    private static void printParagraphs(Section currentSection, int startAtParagraph) {
        /* First, print all paragraphs from the given starting paragraph. */
        for (int i = startAtParagraph; i < currentSection.getParagraphAmount(); i++) {
            /* Print all lines in the current paragraph. */
            currentSection.getParagraph(i).printLines();

            /* There is a next between each paragraph, which means that the reader must click enter if he wants to
             * proceed and read the next paragraph. It is possible to type in commands if the reader wants to.
             */
            if (currentSection.getParagraphAmount() - 1 != i)
                executeReaderCommand(scanTerminal.nextLine(), true);
        }
    }

    private static void executeReaderCommand(String input, boolean askInputAgain) {
        /* The following statement is a switch-case statement, because it will be easier to add new input options later
         * when it is needed. A / (forward slash) is used for each special option, because it will never be able to
         * interfere with any possible directory name.
         */
        switch (input) {
            case "/help":
                /* If the user typed "help", then all available commands in this switch-case-statement must be
                 * shown to him (including a short explanation).
                 */
                System.out.println("Available commands:");
                System.out.println("/help:    Displays this text.");
                System.out.println("/logbook: Displays the logbook entries accumulated so far.");
                System.out.println("/stories: Displays all stories which are available to read.");
                break;
            case "/logbook":
                boolean isEmpty = true;

                /* If the user typed "logbook", then the current logbook must be shown to him. */
                for (Marking sectionRead : sectionsRead) {
                    /* Print all logbook parts accumulated so far. */
                    if (!story.getChapter(sectionRead.getChapterName()).getSection(sectionRead.getSectionIndex())
                    .getLogbookPart().printLines())
                        isEmpty = false;
                }

                if (isEmpty)
                    System.out.println("There are currently no entries in the logbook. Please come back later.");

                break;
            case "/stories":
                /* This loop iterates over all files in the SavedStories dir and prints their names. */
                System.out.println("Story directories in SavedStories:");

                for (File file : new File("SavedStories").listFiles()) {
                    if (file.isDirectory())
                        System.out.println("- " + file.getName());
                }

                break;
            default:
                askInputAgain = false;
                break;
        }

        /* Ask for input again, because he might want to enter a command again. */
        if (askInputAgain) {
            System.out.println("\nEnter another command or press enter to stop entering commands.");
            executeReaderCommand(scanTerminal.nextLine(), true);
        }
    }

    private static Option[] getAvailableOptions(Section currentSection) {
        ArrayList<Option> availableOptions = new ArrayList<Option>();

        for (int i = 0; i < currentSection.getOptionAmount(); i++) {
            Option option = currentSection.getOption(i);

            if (option instanceof OptionIfRead) {
                /* Add the option if the section has been read before. */
                if (hasUserRead(((OptionIfRead) option).getIfRead()))
                    availableOptions.add(option);
            } else if (option instanceof OptionIfNotRead) {
                /* Add the option if the section hasn't been read before. */
                if (!hasUserRead(((OptionIfNotRead) option).getIfNotRead()))
                    availableOptions.add(option);
            } else {
                availableOptions.add(option);
            }
        }

        return availableOptions.toArray(new Option[0]);
    }

    private static boolean hasUserRead(Marking checkIfRead) {
        for (Marking sectionRead : sectionsRead) {
            /* If the section has been read, mark it as read and jump out of the loop. */
            if (checkIfRead.equals(sectionRead))
                return true;
        }

        return false;
    }

    /* This method shows the user all available options and asks for his preferred choice. When the user has chosen an
     * option, the method will return the marking of that option.
     */
    private static Marking letUserChooseOption(Option[] availableOptions) {
        String[] optionNames = new String[availableOptions.length];

        /* Save all option names in optionNames (to compare with the user input later) and print them. */
        for (int i = 0; i < availableOptions.length; i++) {
            optionNames[i] = availableOptions[i].getName();
            System.out.println("- " + optionNames[i]);
        }

        /* Ask the user for his preferred choice. */
        String userInput = scanTerminal.nextLine();

        /* Return the destination of the option containing the best matching option name. */
        return availableOptions[getIndexOfBestMatchingString(userInput, optionNames)].getDestination();
    }

    /* This method returns the index of the string that best matches the reference string. By "best matches", I mean
     * that the amount of insertions, deletions and/or substitutions is the least for that particular match. The
     * difference is calculated using the levenshtein algorithm.
     */
    private static int getIndexOfBestMatchingString(String reference, String[] possibleMatches) {
        int[] differences = new int[possibleMatches.length];

        for (int i = 0; i < possibleMatches.length; i++) {
            /* Get the difference between the reference and the current string to match with using the Levenshtein
             * algorithm.
             */
            differences[i] = getDifferenceLevenshtein(reference.toLowerCase(), possibleMatches[i].toLowerCase());
        }

        /* Return the index of the string that was the least different from the reference. */
        return getIndexOfMinValue(differences);
    }

    /* The following method calculates the difference between 2 strings with the Levenshtein algorithm. It is copied
     * from the website https://www.baeldung.com/java-levenshtein-distance (section 5, looked up on 31/10/2019).
     */
    private static int getDifferenceLevenshtein(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(Math.min(
                    dp[i - 1][j - 1] + (x.charAt(i - 1) == y.charAt(j - 1) ? 0 : 1),
                    dp[i - 1][j] + 1),
                    dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }

    private static int getIndexOfMinValue(int[] values) {
        int indexOfMinValue = 0;
        int minValue = values[0];

        for (int i = 1; i < values.length; i++) {
            int value = values[i];

            if (value < minValue) {
                indexOfMinValue = i;
                minValue = value;
            }
        }

        return indexOfMinValue;
    }
}
