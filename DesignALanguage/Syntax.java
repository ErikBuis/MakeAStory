/*
 * Syntax.java:
 * -
 */

package DesignALanguage;

public final class Syntax {
    protected final Variable[] variables;

    public Syntax(Variable... variables) {
        this.variables = variables;
    }

    public final boolean isValid(String[] args) {
        /* If the number of arguments (minus one for the command name) is not equal to the number of variables in
         * the command, it can by definition not be right syntax. Therefore, the current syntax is skipped with
         * a continue statement.
         */
        if (args.length != this.variables.length)
            return false;

        /* Go through all variables in the current syntax to check if all of them match. */
        for (int i = 0; i < this.variables.length; i++) {
            /* If the variable type did not match the type that the user used, varValues[i] has not been given a
             * value. This means that not all variables matched, so allVariablesMatch has to be set to false.
             */
            if (!this.variables[i].matches(args[i]))
                return false;
        }

        /* If the computer is here, it didn't return false, so all variables matched the argument specifications. */
        return true;
    }

    public Variable getVariable(int index) {
        return this.variables[index];
    }

    public int getVariableAmount() {
        return this.variables.length;
    }

    /* Returns all variables in this syntax with a semicolon between them. */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < this.variables.length - 1; i++) {
            builder.append(this.variables[i] + "; ");
        }

        return builder.append(this.variables[this.variables.length - 1]).append('\n').toString();
    }
}