package ppl.common.utils.command.argument.collector;

import ppl.common.utils.command.argument.Collector;

import java.util.ArrayList;
import java.util.List;

public class ListCollector<V> implements Collector<V, List<V>> {
    private final List<V> list;

    ListCollector() {
        this.list = new ArrayList<>();
    }

    @Override
    public List<V> collect(V v) {
        this.list.add(v);
        return this.list;
    }
}
