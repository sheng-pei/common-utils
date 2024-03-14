package ppl.common.utils.argument.argument;

public abstract class ArgumentBuilder {

    protected String name;

    public ArgumentBuilder(String name) {
        this.name = name;
    }

    public final <A extends Argument> A build() {
        return create(name);
    }

    protected abstract <A extends Argument> A create(String name);

}
