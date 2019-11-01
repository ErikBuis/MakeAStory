/*
 * Option.java:
 * -
 */

package WriteAStory;

public class Option {
    private String name;
    private Marking destination;

    /* Main constructor. This constructor makes an option to choose from, which will always be added. */
    public Option(String name, Marking destination) {
        this.name = name;
        this.destination = destination;
    }

    public String getName() {
        return this.name;
    }

    public Marking getDestination() {
        return this.destination;
    }
}
