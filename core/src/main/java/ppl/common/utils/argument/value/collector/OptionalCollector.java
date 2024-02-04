package ppl.common.utils.argument.value.collector;

import java.util.Optional;

public class OptionalCollector<T> extends AbstractOneCollector<T, Optional<T>> {
    OptionalCollector() {
        this(null, false);
    }

    OptionalCollector(Type type) {
        this(type, false);
    }

    OptionalCollector(Type type, boolean required) {
        super(type, required, Optional::ofNullable);
    }
}
