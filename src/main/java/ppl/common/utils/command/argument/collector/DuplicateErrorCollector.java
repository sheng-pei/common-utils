package ppl.common.utils.command.argument.collector;

import ppl.common.utils.command.argument.Collector;
import ppl.common.utils.command.argument.Nullable;

public class DuplicateErrorCollector<V> implements Collector<V, V> {
    private boolean specified;

    DuplicateErrorCollector() {}

    @Nullable
    @Override
    public V collect(V v) {
        if (this.specified) {
            throw new CollectorException("Only one is allowed in command line.");
        }
        this.specified = true;
        return v;
    }
}
