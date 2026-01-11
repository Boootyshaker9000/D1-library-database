package models;

/**
 * Enum representing the physical condition of a book.
 * Contains predefined states like New, Used, Damaged, and Restored.
 */
public enum BookCondition {
    NEW("New"),
    USED("Used"),
    DAMAGED("Damaged"),
    RESTORED("Restored");

    private final String label;

    /**
     * Constructs a BookCondition with a display label.
     *
     * @param label the string representation of the condition
     */
    BookCondition(String label) {
        this.label = label;
    }

    /**
     * Returns the string representation of the condition.
     *
     * @return the label string
     */
    @Override
    public String toString() {
        return label;
    }
}