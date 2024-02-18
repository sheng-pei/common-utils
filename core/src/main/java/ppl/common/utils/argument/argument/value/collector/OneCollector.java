package ppl.common.utils.argument.argument.value.collector;

import java.util.function.Function;

public class OneCollector<T> extends AbstractOneCollector<T, T> {
    OneCollector() {
        this(null, false);
    }

    OneCollector(Type type) {
        this(type, false);
    }

    OneCollector(Type type, boolean required) {
        super(type, required, Function.identity());
    }
}
