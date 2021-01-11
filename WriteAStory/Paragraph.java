/* Written by Erik Buis
 *
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

    public boolean printLines() {
        for (String line : lines) {
            System.out.println(line);
        }

        return lines.isEmpty();
    }

    public void addLine(String line) {
        this.lines.add(line);
    }
}
