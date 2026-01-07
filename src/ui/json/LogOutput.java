package ui.json;

/**
 * Functional interface for logging messages to a UI component.
 */
public interface LogOutput {
    /**
     * Appends the specified text to the log output.
     *
     * @param text The text to append.
     */
    void append(String text);
}