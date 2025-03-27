package ppl.common.utils.argument.argument.value.collector;

import java.util.Optional;

public class OptionalCollector<T> extends AbstractOneCollector<T, Optional<T>> {
    OptionalCollector() {
        this(Type.ONLY_ONE);
    }

    OptionalCollector(Type type) {
        super(type, false, Optional::ofNullable);
    }
}
