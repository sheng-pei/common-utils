package ppl.common.utils.argument.argument;

public abstract class Argument {
    private final String name;

    protected Argument(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Argument name is required.");
        }

        this.name = name;
    }

    public String name() {
        return this.name;
    }

    public abstract String keyString();

}
