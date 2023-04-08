package ppl.common.utils.command.argument.collector;

import ppl.common.utils.command.argument.Collector;
import ppl.common.utils.command.argument.Nullable;

public class ReplaceCollector<V> implements Collector<V, V> {
    private V v;

    ReplaceCollector() {}

    @Nullable
    @Override
    public V collect(V v) {
        if (this.v != null && v == null) {
            v = this.v;
        }
        this.v = v;
        return this.v;
    }
}
