/* Written by Erik Buis
 *
 * StoryCommands.java:
 * -
 */

package CompileAStory;

import java.util.Arrays;

import DesignALanguage.*;
import WriteAStory.*;

public abstract class StoryCommand extends Command {
    public abstract void compile(Syntax syntax, String[] args, Story story, String currentChapterName);

    /* This method returns an instance of the class Marking, in which the location of a particular paragraph is stored.
     * When the marking points to the first paragraph in a section, the beginning of a section is referenced. The args
     * command must only contain the arguments that matter for the marking instance to be constructed, not including
     * the other arguments. A sub-array of the complete array can be constructed with the method Arrays.copyOfRange.
     */
    static final Marking getMarking(String[] args, Story story, String currentChapterName) {
        /* If the option has no arguments, it points to the end of the story. */
        if (args.length == 0)
            return new Marking();

        /* If the computer is here, the marking didn't point to the end of the story. The computer must set the chapter
         * name to be stored in the to be returned marking if the first argument is not a section index (because then,
         * it must be a chapter name).
         */
        boolean chapterIsGiven = false;
        String chapterName = currentChapterName;
        int sectionIndex = 0;
        int paragraphIndex = 0;

        /* Check if the pointer contains a chapter name (the first argument mustn't be a section index). */
        if (!args[0].matches("\\d+")) {
            chapterName = args[0];

            /* To check if the given chapter name exists, the story instance must be checked. If this check returns
             * null, the chapter doesn't exist.
             */
            if (story.getChapter(chapterName) == null)
                throw new StoryCompileException("An undefined chapter name was given.");

            chapterIsGiven = true;
        }

        /* The pointer always contains a section index, so this doesn't have to be checked. */
        sectionIndex = Integer.parseInt(args[chapterIsGiven ? 1 : 0]);

        // TODO Vraag Tijmen wat ik moet doen in het geval dat een section nog niet geladen is. Ik wilde eerst een exception laten genereren, maar dit kan niet omdat deze nog niet geladen is op het moment dat een option ernaar verwijst.
        // /* Check if the given section index exists in the chapter given by chapterName. */
        // if (story.getChapter(chapterName).getSectionAmount() <= sectionIndex)
        //     throw new StoryCompileException("An undefined section index was given.");

        /* Check if the pointer contains a paragraph index. */
        if (args.length > (chapterIsGiven ? 2 : 1)) {
            paragraphIndex = Integer.parseInt(args[chapterIsGiven ? 2 : 1]);

            // /* Check if the given paragraph index exists in the chapter given by chapterName. */
            // if (story.getChapter(chapterName).getSection(sectionIndex).getParagraphAmount() <= paragraphIndex)
            //     throw new StoryCompileException("An undefined paragraph index was given.");
        }

        /* If the computer got here, no exceptions have been thrown. The marking can be safely returned. */
        return new Marking(chapterName, sectionIndex, paragraphIndex);
    }
}

/* This command adds a section to the current chapter (ArrayList with Section instances) and checks if the real section
 * number is the same as the number given by the author. This check is only here for the author, so that he can keep
 * track of the numbers of sections more easily in his own code.
 */
final class SCsection extends StoryCommand {
    @Override
    public String getName() {
        return "!section";
    }

    @Override
    public Syntax[] getSyntaxes() {
        return new Syntax[] {
            new Syntax(
                new Variable(Variable.Type.INTEGER, "sectionIndex")
            )
        };
    }

    @Override
    public void compile(Syntax syntax, String[] args, Story story, String currentChapterName) {
        int authorSectionIndex = Integer.parseInt(args[0]);
        int realSectionIndex = story.getChapter(currentChapterName).getSectionAmount();

        if (authorSectionIndex == realSectionIndex) {
            /* The following command internallt calls the constructor of the class Section, which adds one paragraph.
             * This prevents the author having to call !next after every initiation of a new section.
             */
            story.getChapter(currentChapterName).addEmptySection();
        } else {
            throw new StoryCompileException(String.format("The section number isn't specified correctly. It"
            + " should have been %d.", realSectionIndex));
        }
    }
}

/* This command adds a paragraph to the current section. During runtime, the user has to press enter to let the
 * computer display the next paragraph.
 */
final class SCnext extends StoryCommand {
    @Override
    public String getName() {
        return "!next";
    }

    @Override
    public Syntax[] getSyntaxes() {
        return new Syntax[] {
            new Syntax()
        };
    }

    @Override
    public void compile(Syntax syntax, String[] args, Story story, String currentChapterName) {
        story.getChapter(currentChapterName).getLastSection().addEmptyParagraph();
    }
}

/* This command returns an instance of the class Option, so that to can be added to the current section or used in a
 * subclass of Option, such as in compileOptifread or compileOptifnotread. The command !opt must have an option name as
 * its first argument, after which it must specify a destination, which represents the position to which the user must
 * be send when he chooses that particular option.
 */
final class SCopt extends StoryCommand {
    @Override
    public String getName() {
        return "!opt";
    }

    @Override
    public Syntax[] getSyntaxes() {
        return new Syntax[] {
            /* For sending the reader to end of the story. */
            new Syntax(
                new Variable(Variable.Type.STRING, "optionName")
            ),
            /* For sending the reader to a section in the current chapter. */
            new Syntax(
                new Variable(Variable.Type.STRING, "optionName"),
                new Variable(Variable.Type.INTEGER, "sectionIndex")
            ),
            /* For sending the reader to a specific paragraph in a section in the current chapter. */
            new Syntax(
                new Variable(Variable.Type.STRING, "optionName"),
                new Variable(Variable.Type.INTEGER, "sectionIndex"),
                new Variable(Variable.Type.INTEGER, "paragraphIndex")
            ),
            /* For sending the reader to a section in another chapter. */
            new Syntax(
                new Variable(Variable.Type.STRING, "optionName"),
                new Variable(Variable.Type.STRING, "chapterName"),
                new Variable(Variable.Type.INTEGER, "sectionIndex")
            ),
            /* For sending the reader to a specific paragraph in a section in another chapter. */
            new Syntax(
                new Variable(Variable.Type.STRING, "optionName"),
                new Variable(Variable.Type.STRING, "chapterName"),
                new Variable(Variable.Type.INTEGER, "sectionIndex"),
                new Variable(Variable.Type.INTEGER, "paragraphIndex")
            )
        };
    }

    @Override
    public void compile(Syntax syntax, String[] args, Story story, String currentChapterName) {
        /* Add a new option containing the compiled marking to the current section. */
        story.getChapter(currentChapterName).getLastSection().addOption(
            new Option(args[0], super.getMarking(Arrays.copyOfRange(args, 1, args.length), story, currentChapterName))
        );
    }
}

/* This method adds an instance of OptionIfRead to the current section. During runtime, this option will only be added
 * to the list of available options if the user read the specified section in the story. (If it is not added, the user
 * will not be able to see it.) The specified section is given in the first 1 or 2 arguments, after which the syntax of
 * !opt must be used.
 */
final class SCoptifread extends StoryCommand {
    @Override
    public String getName() {
        return "!optifread";
    }

    @Override
    public Syntax[] getSyntaxes() {
        /* The following code copies all syntaxes from SCopt, so they can be used in this class and in SCoptifnotread.
         * However, the following code also adds one or two variables in front of all variables listed in each
         * individual syntax of SCopt.
         */
        Syntax[] optSyntaxes = new SCopt().getSyntaxes();
        Syntax[] syntaxes = new Syntax[optSyntaxes.length * 2];

        for (int i = 0; i < optSyntaxes.length; i++) {
            Syntax optSyntax = optSyntaxes[i];
            Variable[] variablesIfSectionRead = new Variable[optSyntax.getVariableAmount() + 1];
            Variable[] variablesIfSectionInChapterRead = new Variable[optSyntax.getVariableAmount() + 2];

            /* The following variables represent the section index that must be checked for being read by the user. */
            variablesIfSectionRead[0] = new Variable(Variable.Type.INTEGER, "sectionIndex");
            variablesIfSectionInChapterRead[0] = new Variable(Variable.Type.STRING, "chapterName");
            variablesIfSectionInChapterRead[1] = new Variable(Variable.Type.INTEGER, "sectionIndex");

            for (int j = 0; j < optSyntax.getVariableAmount(); j++) {
                variablesIfSectionRead[1 + j] = optSyntax.getVariable(j);
                variablesIfSectionInChapterRead[2 + j] = optSyntax.getVariable(j);
            }

            syntaxes[i] = new Syntax(variablesIfSectionRead);
            syntaxes[i + 1] = new Syntax(variablesIfSectionInChapterRead);
        }

        return syntaxes;
    }

    @Override
    public void compile(Syntax syntax, String[] args, Story story, String currentChapterName) {
        /* If the syntax begins with a variable named "secionIndex", the author only provided this argument to point to
         * the section that must be checked for it being read by the user. This statement provides the information that
         * further indexes must start at index 1.
         */
        if (syntax.getVariable(0).getName().equals("sectionIndex")) {
            story.getChapter(currentChapterName).getLastSection().addOption(
                new OptionIfRead(
                    super.getMarking(new String[] {args[0]}, story, currentChapterName),
                    args[1],
                    super.getMarking(Arrays.copyOfRange(args, 2, args.length), story, currentChapterName)
                )
            );
        /* If the first argument is not named "sectionIndex", it must be "chapterName", and the next argument must be
         * "sectionIndex". Because these arguments both provide information about the location of the section that must
         * be checked for it being read by the user, further indexes start at index 2.
         */
        } else {
            story.getChapter(currentChapterName).getLastSection().addOption(
                new OptionIfRead(
                    super.getMarking(Arrays.copyOfRange(args, 0, 2), story, currentChapterName),
                    args[2],
                    super.getMarking(Arrays.copyOfRange(args, 3, args.length), story, currentChapterName)
                )
            );
        }
    }
}

/* This method adds an instance of OptionIfNotRead to the current section. During runtime, this option will only be
 * added to the list of available options if the user didn't read the specified section in the story. (If it is not
 * added, the user will not be able to see it.) The specified section is given in the first 1 or 2 arguments, after
 * which the syntax of !opt must be used.
 */
final class SCoptifnotread extends StoryCommand {
    @Override
    public String getName() {
        return "!optifnotread";
    }

    @Override
    public Syntax[] getSyntaxes() {
        return new SCoptifread().getSyntaxes();
    }

    @Override
    public void compile(Syntax syntax, String[] args, Story story, String currentChapterName) {
        /* If the syntax begins with a variable named "secionIndex", the author only provided this argument to point to
         * the section that must be checked for it being read by the user. This statement provides the information that
         * further indexes must start at index 1.
         */
        if (syntax.getVariable(0).getName().equals("sectionIndex")) {
            story.getChapter(currentChapterName).getLastSection().addOption(
                new OptionIfNotRead(
                    super.getMarking(new String[] {args[0]}, story, currentChapterName),
                    args[1],
                    super.getMarking(Arrays.copyOfRange(args, 2, args.length), story, currentChapterName)
                )
            );
        /* If the first argument is not named "sectionIndex", it must be "chapterName", and the next argument must be
         * "sectionIndex". Because these arguments both provide information about the location of the section that must
         * be checked for it being read by the user, further indexes start at index 2.
         */
        } else {
            story.getChapter(currentChapterName).getLastSection().addOption(
                new OptionIfNotRead(
                    super.getMarking(Arrays.copyOfRange(args, 0, 2), story, currentChapterName),
                    args[2],
                    super.getMarking(Arrays.copyOfRange(args, 3, args.length), story, currentChapterName)
                )
            );
        }
    }
}

/* This method completely ignores all input it gets, so that the author can write everything (excluding semicolons!) he
 * wants in it. This is especially useful when the author wants to remember things he might forget, such as a section
 * he wants to add later or a reference to the future of the story that is important, but shouldn't be shown to the
 * reader.
 */
final class SCauthor extends StoryCommand {
    @Override
    public String getName() {
        return "!author";
    }

    @Override
    public Syntax[] getSyntaxes() {
        return new Syntax[] {
            new Syntax(
                new Variable(Variable.Type.STRING, "authorComment")
            )
        };
    }

    @Override
    public void compile(Syntax syntax, String[] args, Story story, String currentChapterName) {
    }
}

/* This method adds a paragraph of text to the logbook object in the current section. When the user reads this section,
 * the line will be added to the logbook. Warning: semicolons are not permitted, because only the first argument given
 * will be added to the logbook.
 */
final class SClogbook extends StoryCommand {
    @Override
    public String getName() {
        return "!logbook";
    }

    @Override
    public Syntax[] getSyntaxes() {
        return new Syntax[] {
            new Syntax(
                new Variable(Variable.Type.STRING, "logbookParagraph")
            )
        };
    }

    @Override
    public void compile(Syntax syntax, String[] args, Story story, String currentChapterName) {
        story.getChapter(currentChapterName).getLastSection().addTextToLogbook(args[0]);
    }
}
