/* Written by Erik Buis
 *
 * Variable.java:
 * -
 */

package DesignALanguage;

public final class Variable {
    private final Type type;
    private final String name;

    public Variable(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public enum Type {
        STRING,
        INTEGER
    }

    /* This method checks if the argument given matches the Type of this variable. If the variable type is an integer,
     * arg must be an integer too. If the variable type is a string, arg must be a string too. When the types aren't
     * equal, this is not the right syntax.
     */
    public boolean matches(String arg) {
        switch (this.type) {
            case STRING:
                return true;
            case INTEGER:
                return arg.matches("\\d+");
            default:
                /* The following default block can never be executed (because all Types are covered), but it must be
                 * added, because otherwise I would get a VSCode error (saying that this method must return a boolean).
                 */
                throw new IllegalArgumentException("The developer made a mistake. Please contact him to solve this"
                + " problem.");
        }
    }

    public Type getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.type + " " + this.name;
    }
}
