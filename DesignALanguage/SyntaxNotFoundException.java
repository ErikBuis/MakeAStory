/* Written by Erik Buis
 *
 * SyntaxNotFoundException.java:
 * -
 */

package DesignALanguage;

public class SyntaxNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    SyntaxNotFoundException(String message) {
        super(message);
    }
}
