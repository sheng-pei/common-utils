package ppl.common.utils.command.argument.map;

import ppl.common.utils.command.argument.Mapper;
import ppl.common.utils.command.argument.Nullable;

public class Required<V> implements Mapper<V, V> {
    @Nullable
    @Override
    public V map(V v) {
        if (v == null) {
            throw new MapperException("The value is required.");
        }
        return v;
    }
}
