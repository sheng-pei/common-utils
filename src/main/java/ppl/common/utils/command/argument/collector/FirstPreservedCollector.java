package ppl.common.utils.command.argument.collector;

import ppl.common.utils.command.argument.Collector;

public class FirstPreservedCollector<V> implements Collector<V, V> {
    private V v;

    FirstPreservedCollector() {}

    @Override
    public V collect(V v) {
        if (this.v == null) {
            this.v = v;
        }
        return this.v;
    }
}
