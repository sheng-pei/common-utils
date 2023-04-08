package ppl.common.utils.command.argument.collector;

import ppl.common.utils.command.argument.Collector;

import java.util.HashSet;
import java.util.Set;

public class SetCollector<V> implements Collector<V, Set<V>> {
    private final Set<V> set;

    SetCollector() {
        this.set = new HashSet<>();
    }

    @Override
    public Set<V> collect(V v) {
        this.set.add(v);
        return this.set;
    }
}
