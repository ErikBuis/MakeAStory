/* Written by Erik Buis
 *
 * Section.java:
 * -
 */

package WriteAStory;

import java.util.ArrayList;

public final class Section {
    /* All paragraphs in this section. During runtime, each paragraph will be run individually. When the user presses
     * enter, the next paragraph is shown.
     */
    private ArrayList<Paragraph> paragraphs;
    /* The arraylist with all options that the user can choose at the end of this section. It is an ArrayList (not a
     * HashMap), because using a HashMap would ruin the order in which the options were specified.
     */
    private ArrayList<Option> options;
    /* This is a part of the logbook that is added if and only if the user read this section. It will be added to the
     * logbook of the user during runtime.
     */
    private Paragraph logbookPart;

    /* Main constructor. This constructor adds one empty paragraph to the list, so it isn't needed to call !next at the
     * beginning of every section.
     */
    public Section() {
        this.paragraphs = new ArrayList<Paragraph>();
        this.paragraphs.add(new Paragraph());
        this.options = new ArrayList<Option>();
        this.logbookPart = new Paragraph();
    }

    /* This method returns the paragraph at a specified index. */
    public Paragraph getParagraph(int index) {
        return this.paragraphs.get(index);
    }

    /* This method returns the size of the ArrayList paragraphs. */
    public int getParagraphAmount() {
        return this.paragraphs.size();
    }

    /* This method adds an empty paragraph. */
    public void addEmptyParagraph() {
        this.paragraphs.add(new Paragraph());
    }

    /* This method adds a line to the last paragraph added to this section. */
    public void addLineToLastParagraph(String line) {
        this.paragraphs.get(this.paragraphs.size() - 1).addLine(line);
    }


    /* This method returns one of the options saved in options. */
    public Option getOption(int index) {
        return this.options.get(index);
    }

    /* This method returns the total number of options in this options ArrayList. */
    public int getOptionAmount() {
        return this.options.size();
    }

    /* This method adds an option to the ArrayList with options. */
    public void addOption(Option option) {
        this.options.add(option);
    }


    /* This method returns the logbookPart of this section for concatenation of all logbook parts. */
    public Paragraph getLogbookPart() {
        return this.logbookPart;
    }

    /* This method adds text to the logbook when this section is read by the user. A newline is added automatically. */
    public void addTextToLogbook(String line) {
        this.logbookPart.addLine(line);
    }
}
