package ppl.common.utils.command.argument.collector;

import ppl.common.utils.command.CommandLineException;
import ppl.common.utils.command.argument.Collector;
import ppl.common.utils.command.argument.Nullable;

public class DuplicateErrorCollector<V> implements Collector<V, V> {
    private boolean specified;

    DuplicateErrorCollector() {}

    @Nullable
    @Override
    public V collect(V v) {
        if (this.specified) {
            throw new CommandLineException("Only one argument of the same name is allowed in command line.");
        }
        this.specified = true;
        return v;
    }
}
