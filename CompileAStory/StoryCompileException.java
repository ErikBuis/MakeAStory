/* Written by Erik Buis
 *
 * StoryCompileException.java:
 * -
 */

package CompileAStory;

public final class StoryCompileException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public StoryCompileException(String message, Throwable e) {
        super(message, e);
    }

    public StoryCompileException(String message) {
        super(message);
    }
}
