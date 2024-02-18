package ppl.common.utils.argument.argument;

import java.util.List;

public interface Arguments<K, S, R extends Argument<K, Object>> {
    List<R> getArguments();

    R get(S s);

    R getByName(K name);
}
