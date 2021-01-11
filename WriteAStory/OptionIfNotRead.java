/* Written by Erik Buis
 *
 * OptionIfNotRead.java:
 * -
 */

package WriteAStory;

public final class OptionIfNotRead extends Option {
    private Marking ifNotRead;

    public OptionIfNotRead(Marking ifNotRead, String name, Marking destination) {
        super(name, destination);
        this.ifNotRead = ifNotRead;
    }

    public Marking getIfNotRead() {
        return this.ifNotRead;
    }
}
