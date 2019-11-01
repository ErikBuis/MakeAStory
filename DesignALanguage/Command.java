/*
 * Command.java:
 * -
 */

package DesignALanguage;

import java.util.Arrays;

public abstract class Command {
    public abstract String getName();
    public abstract Syntax[] getSyntaxes();

    public final Syntax getValidSyntax(String[] args) {
        for (Syntax syntax : getSyntaxes()) {
            if (syntax.isValid(args))
                return syntax;
        }

        throw new SyntaxNotFoundException("None of the possible syntaxes matched with the command given. " + this
        + "Given arguments: " + Arrays.toString(args));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Possible syntaxes:\n");

        for (Syntax syntax : getSyntaxes()) {
            builder.append(String.format("%s; %s\n", getName(), syntax));
        }

        return builder.toString();
    }
}
