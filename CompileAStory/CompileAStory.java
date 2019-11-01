/*
* CompileAStory.java:
* -
*/

package CompileAStory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

import DesignALanguage.*;
import WriteAStory.*;

public final class CompileAStory {
    /* All commands runnable in the WriteAStory language. */
    private static ArrayList<StoryCommand> storyCommands = getStoryCommands();

    /* This is the method that can be called from outside this class. It returns an instance of the class Story, which
     * contains all data that is needed to run the full program and read the whole story. All chapters out of all given
     * files will be read.
     */
    public static Story compileFullStory() {
        /* Get an input directory. */
        File inputDir = inputStoryName();

        /* Read the properties of the given story contained in the main.properties file. */
        Properties prop = readStoryProperties(inputDir);

        /* Get an ordered array with strings of the names of chapters that must be read. */
        String[] chapterNames = prop.getProperty("chapters").split(",");

        /* Initiate the story. If one or more of the properties aren't found, a StoryCompileException will be thrown. */
        Story story = initStory(prop, chapterNames[0]);

        for (String chapterName : chapterNames) {
            /* Add the name of every chapter to the story. */
            story.addChapter(chapterName);
        }

        for (String chapterName : chapterNames) {
            /* Read the contents of each chapter. */
            loadChapterContents(story, chapterName, new File(inputDir, chapterName + ".txt"));
        }

        return story;
    }

    private static File inputStoryName() {
        Scanner scanTerminal = new Scanner(System.in);
        System.out.println("Welcome to the compilation program of Write A Story!");

        do {
            System.out.println("\nPlease pick the story you want to compile and/or read. This has to be an existing"
            + " directory in SavedStories. Type \"/all\" to see all available stories.");

            String input = scanTerminal.nextLine();

            /* The following statement is a switch-case statement, because it will be easier to add new input options
            * later when it is needed. A / (forward slash) is used for each special option, because it will never be
            * able to interfere with any possible directory name.
            */
            switch (input) {
                case "/all":
                    printAvailableStories();
                    break;
                default:
                    /* Get file object of directory. */
                    File storyDir = new File("SavedStories", input);

                    /* Check if the given directory exists. */
                    if (storyDir.exists() && storyDir.isDirectory()) {
                        scanTerminal.close();
                        return storyDir;
                    } else {
                        System.out.println("The given directory does not exist.");
                    }

                    break;
            }
        } while (true);
    }

    /* This method iterates over all files in the SavedStories dir and prints the names of the dirs in there. */
    private static void printAvailableStories() {
        for (File file : new File("SavedStories").listFiles()) {
            if (file.isDirectory()) {
                System.out.println(file.getName());
            }
        }
    }

    /* This method reads all properties out of the main.properties file and returns an instance of Properties. */
    private static Properties readStoryProperties(File inputDir) {
        Properties prop = new Properties();

        try {
            prop.load(new FileInputStream(new File(inputDir, "main.properties")));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("This story directory doesn't contain a main.properties file.");
        } catch (IOException e) {
            throw new IllegalArgumentException("The input of the main.properties file is not valid.");
        }

        return prop;
    }

    /* This method initiates an instance of the class Story that compileFullStory is building. When the title or author
     * of the story isn't mentioned in the main.properties file, this method will throw a StoryCompileException.
     */
    private static Story initStory(Properties prop, String nameOfFirstChapter) {
        String title = prop.getProperty("title");
        String author = prop.getProperty("author");

        if (title == null) {
            throw new StoryCompileException("The title of the story isn't given in the main.properties file.");
        } else if (author == null) {
            throw new StoryCompileException("The author of the story isn't given in the main.properties file.");
        } else {
            return new Story(title, author, nameOfFirstChapter);
        }
    }

    private static void loadChapterContents(Story story, String currentChapterName, File chapterFile) {
        Scanner scanChapter;

        /* Try to load the file in the scanner. If it doesn't exist, the main.properties file didn't specify the right
         * chapter name(s). The program should then give a StoryCompileException.
         */
        try {
            scanChapter = new Scanner(chapterFile);
        } catch(FileNotFoundException e) {
            throw new StoryCompileException(String.format("The chapter \"%s\" listed in main.properties doesn't exist"
            + " or doesn't have a \".txt\" extension.", currentChapterName));
        }

        /* Initiate a variable that contains the current line number being read, so that it can be used in the exception
         * message if processLine would throw a StoryCompileException. lineNr starts at 1, because the line number in a
         * .txt file also starts at 1.
         */
        int lineNr = 0;

        while (scanChapter.hasNextLine()) {
            lineNr++;

            /* Catch a possible StoryCompileException, so that the file and line number from where the exception was
             * thrown are added to the exception message. This is useful for the author, because he can debug his story
             * without much searching work.
             */
            try {
                processLine(story, currentChapterName, scanChapter.nextLine());
            } catch (StoryCompileException e) {
                scanChapter.close();
                throw new StoryCompileException(String.format("%s, line %d: %s", chapterFile.getName(), lineNr,
                e.getMessage()));
            }
        }

        scanChapter.close();
    }

    /* This method processes one line from the current chapter being compiled. If the line is recognised as a command,
     * it will split the line and throw a StoryCompileException if something goes wrong (for example, if the syntax
     * given to the command isn't correct). The method loadChapterContents will then add the line number to the
     * exception that this method has thrown if it throws one.
     */
    private static void processLine(Story story, String currentChapterName, String line) throws StoryCompileException {
        /* Check if the line is a command (begins with the char '!') or not. */
        if (line.charAt(0) == '!') {
            /* Split the line at every ';' and trim each argument by removing unnecessary space characters. */
            String[] splitLine = line.split("\\s*;\\s*");
            /* Use the command and args parts of the split string seperately. */
            StoryCommand command = getCommandByName(splitLine[0]);
            String[] args = Arrays.copyOfRange(splitLine, 1, splitLine.length - 1);

            /* Process the command argument of the line. The explanation about each command is given above the class in
             * the file StoryCommand.java that is processing that particular command. First, a valid syntax must be found. If none exists, a StoryCompileException must be thrown.
             */
            Syntax validSyntax;

            try {
                validSyntax = command.getValidSyntax(args);
            } catch (SyntaxNotFoundException e) {
                throw new StoryCompileException(e.getMessage());
            }

            /* Finally, compile the command that the author wants to execute. */
            command.compile(validSyntax, args, story, currentChapterName);
        } else {
            /* The line is not a command, so it is a part of the story. It must therefore be added to the last
             * paragraph added to this section.
             */
            story.getChapter(currentChapterName).getLastSection().addLineToLastParagraph(line);
        }
    }

    /* Add all StoryCommands needed by making each command an anonimous class, and adding it as an instance of
     * StoryCommand.
     */
    private static ArrayList<StoryCommand> getStoryCommands() {
        ArrayList<StoryCommand> storyCommands = new ArrayList<StoryCommand>();

        storyCommands.add(new SCsection());
        storyCommands.add(new SCnext());
        storyCommands.add(new SCopt());
        storyCommands.add(new SCoptifread());
        storyCommands.add(new SCoptifnotread());
        storyCommands.add(new SCauthor());
        storyCommands.add(new SClogbook());

        return storyCommands;
    }

    /* This method returns the command that has the name given to this method. */
    private static StoryCommand getCommandByName(String name) {
        for (StoryCommand storyCommand : storyCommands) {
            if (storyCommand.getName() == name) {
                return storyCommand;
            }
        }

        throw new StoryCompileException("The name of the command given on this line doesn't exist.");
    }
}
