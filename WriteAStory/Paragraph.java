/*
 * Paragraph.java:
 * -
 */

package WriteAStory;

import java.util.ArrayList;

public final class Paragraph {
    private ArrayList<String> lines;

    public Paragraph() {
        this.lines = new ArrayList<String>();
    }

    public void printLines() {
        for (String line : lines) {
            System.out.println(line);
        }
    }

    public void addLine(String line) {
        this.lines.add(line);
    }
}