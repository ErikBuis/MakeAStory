/*
 * Story.java:
 * -
 */

package WriteAStory;

import java.util.HashMap;
import java.util.Map;

public final class Story {
    private final String title;
    private final String author;
    private final Marking startingPosition;
    private Map<String, Chapter> chapters;

    public Story(String title, String author, String nameOfFirstChapter) {
        this.title = title;
        this.author = author;
        this.startingPosition = new Marking(nameOfFirstChapter, 0, 0);
        this.chapters = new HashMap<String, Chapter>();
    }

    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public Marking getStartingPosition() {
        return this.startingPosition;
    }

    public Chapter getChapter(String name) {
        return this.chapters.get(name);
    }

    public int getChapterAmount() {
        return this.chapters.size();
    }

    public void addChapter(String name) {
        this.chapters.put(name, new Chapter(name));
    }
}
