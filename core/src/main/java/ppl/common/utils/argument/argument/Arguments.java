package ppl.common.utils.argument.argument;

import java.util.List;

public interface Arguments<S, R extends Argument> {
    List<R> getArguments();

    R getByKey(S s);

    R getByName(String name);
}
