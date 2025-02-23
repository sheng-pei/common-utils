package ppl.common.utils.argument.argument;

public interface Arguments<S, A extends Argument> {
    A getByKey(S s);
    A getByName(String name);
}
