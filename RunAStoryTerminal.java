/*
 * RunAStoryTerminal.java:
 * -
 */

import java.util.ArrayList;
import java.util.Scanner;

import CompileAStory.CompileAStory;
import WriteAStory.*;

public class RunAStoryTerminal {
    private static final Scanner scanTerminal = new Scanner(System.in);
    /* Compile the story and store it in a story instance. */
    private static final Story story = CompileAStory.compileFullStory();
    /* This ArrayList contains all sections that the user read so far, so that optifread and optifnotread can check if
     * the user actually read a paragraph. It is also useful when the user reads a section containing a logbookPart,
     * because it would be automatically added to this ArrayList.
     */
    private static ArrayList<Marking> sectionsRead = new ArrayList<Marking>();

    public static void main(String[] args) {
        /* Start running the first section. Then run the section at the marking returned. */
        Marking running = story.getStartingPosition();

        do {
            /* Get the section that has to be run from the marking runFrom. */
            Section currentSection = story.getChapter(running.getChapterName()).getSection(running.getSectionIndex());

            /* Print all the paragraphs and give the user the option to type in a command (e.g. "logbook" to see the
             * current state of the logbook) after each paragraph.
             */
            printParagraphs(currentSection, running.getParagraphIndex());

            /* Add the current position to the ArrayList containing all markings of sections read by the user so far. */
            sectionsRead.add(running);

            /* Let the user choose one of the options, so that he can be redirected to another point in the story. */
            Option[] availableOptions = getAvailableOptions(currentSection);
            running = letUserChooseOption(availableOptions);

        /* Check if the new marking points to the end of the story (the chapter name is then equal to null). */
        } while (running.getChapterName() != null);

        System.out.println("THE END\n\n\n\n");
        scanTerminal.nextLine();
        System.out.println("CREDITS:\n");
        System.out.println("Author: " + story.getAuthor());
        System.out.println("Programmer: Erik Buis");
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
            readUserInput();
        }
    }

    private static void readUserInput() {
        switch (scanTerminal.nextLine()) {
            case "help":
                /* If the user typed "help", then all available commands in this switch-case-statement must be
                 * shown to him (including a short explanation).
                 */
                System.out.println("Available commands:");
                System.out.println("help:    Displays this text.");
                System.out.println("logbook: Displays the logbook text accumulated so far.");
                break;
            case "logbook":
                /* If the user typed "logbook", then the current logbook must be shown to him. */
                for (Marking sectionRead : sectionsRead) {
                    /* Print all logbook parts accumulated so far. */
                    story.getChapter(sectionRead.getChapterName()).getSection(sectionRead.getSectionIndex())
                    .getLogbookPart().printLines();
                    System.out.println();
                }
                break;
            default:
                return;
        }

        /* Ask for input again, because the user didn't click enter yet. */
        readUserInput();
    }

    private static Option[] getAvailableOptions(Section currentSection) {
        ArrayList<Option> availableOptions = new ArrayList<Option>();

        for (int i = 0; i < currentSection.getOptionAmount(); i++) {
            Option option = currentSection.getOption(i);

            /* Add the option if it has been read and is an instance of OptionIfRead or it has not been read and is an
             * instance of OptionIfNotRead.
             */
            if (option instanceof OptionIfRead) {
                Marking ifRead = ((OptionIfRead) option).getIfRead();

                for (Marking sectionRead : sectionsRead) {
                    /* If the section has been read, add it to the available options. */
                    if (ifRead.equals(sectionRead)) {
                        availableOptions.add(option);
                        break;
                    }
                }
            } else if (option instanceof OptionIfNotRead) {
                Marking ifNotRead = ((OptionIfNotRead) option).getIfNotRead();
                boolean isRead = false;

                for (Marking sectionRead : sectionsRead) {
                    /* If the section has been read, mark it as read and jump out of the loop. */
                    if (ifNotRead.equals(sectionRead)) {
                        isRead = true;
                        break;
                    }
                }

                /* Add the option if the section hasn't been read before. */
                if (!isRead)
                    availableOptions.add(option);
            } else {
                availableOptions.add(option);
            }
        }

        return availableOptions.toArray(new Option[0]);
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
            differences[i] = getDifferenceLevenshtein(reference, possibleMatches[i]);
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
                    dp[i - 1][j - 1] + x.charAt(i - 1) == y.charAt(j - 1) ? 0 : 1,
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