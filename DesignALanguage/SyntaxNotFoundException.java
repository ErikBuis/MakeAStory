/*
 * SyntaxNotFoundException.java:
 * -
 */

package DesignALanguage;

public class SyntaxNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    SyntaxNotFoundException(String message) {
        super(message);
    }
}
