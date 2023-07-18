package ppl.common.utils.argument;

import java.util.List;

public interface Arguments<K, S> {
    List<AbstractArgument<K, Object>> getArguments();
    AbstractArgument<K, Object> get(S s);
    AbstractArgument<K, Object> getByName(K name);
}
