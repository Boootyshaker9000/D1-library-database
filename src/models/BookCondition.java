public enum BookCondition {
    NEW("New"),
    USED("Used"),
    DAMAGED("Damaged"),
    RESTORED("Restored");

    private final String label;

    BookCondition(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}