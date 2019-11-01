/*
 * OptionIfRead.java:
 * -
 */

package WriteAStory;

public final class OptionIfRead extends Option {
    private Marking ifRead;

    public OptionIfRead(Marking ifRead, String name, Marking destination) {
        super(name, destination);
        this.ifRead = ifRead;
    }

    public Marking getIfRead() {
        return this.ifRead;
    }
}