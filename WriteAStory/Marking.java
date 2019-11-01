/*
 * Marking.java:
 * -
 */

package WriteAStory;

public final class Marking {
    private final String chapterName;
    private final int sectionIndex;
    private final int paragraphIndex;

    /* Main constructor. All other constructors chain to this one.
     * No chaptername (null) points to the end of the story.
     * A paragraphINdex of -1 points to the question at the end of the specified section.
     */
    public Marking(String chapterName, int sectionIndex, int paragraphIndex) {
        this.chapterName = chapterName;
        this.sectionIndex = sectionIndex;
        this.paragraphIndex = paragraphIndex;
    }

    /* This constructor points to the end of the story. */
    public Marking() {
        this(null, 0, 0);
    }

    public String getChapterName() {
        return this.chapterName;
    }

    public int getSectionIndex() {
        return this.sectionIndex;
    }

    public int getParagraphIndex() {
        return this.paragraphIndex;
    }

    /* This method is used to check if the user has already read a section. If the user didn't read all paragraphs in
     * the section (but at least one), the section is still counted. This is why the paragraphs are not checked for
     * equality.
     */
    public boolean equals(Marking marking) {
        return this.chapterName == marking.chapterName
        && this.sectionIndex == marking.sectionIndex;
    }

    @Override
    public String toString() {
        return this.chapterName + "; " + this.sectionIndex;
    }
}