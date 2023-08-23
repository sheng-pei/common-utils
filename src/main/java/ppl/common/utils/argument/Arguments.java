package ppl.common.utils.argument;

import java.util.List;

public interface Arguments<K, S> {
    List<Argument<K, Object>> getArguments();

    Argument<K, Object> get(S s);

    Argument<K, Object> getByName(K name);
}
