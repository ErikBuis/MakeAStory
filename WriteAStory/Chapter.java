/* Written by Erik Buis
 *
 * Chapter.java:
 * -
 */

package WriteAStory;

import java.util.ArrayList;

public class Chapter {
    private String name;
    private ArrayList<Section> sections;

    Chapter(String name) {
        this.name = name;
        this.sections = new ArrayList<Section>();
    }

    public String getName() {
        return this.name;
    }

    public Section getSection(int index) {
        return this.sections.get(index);
    }

    public int getSectionAmount() {
        return this.sections.size();
    }

    public Section getLastSection() {
        return this.getSection(this.getSectionAmount() - 1);
    }

    public void addEmptySection() {
        this.sections.add(new Section());
    }
}
