package ppl.common.utils.argument.argument;

public interface Arguments<S, R extends Argument> {
    R getByKey(S s);

    R getByName(String name);
}
