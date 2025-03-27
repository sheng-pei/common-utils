package ppl.common.utils.argument.argument.value.collector;

import java.util.function.Function;

public class IdentityCollector<T> extends AbstractOneCollector<T, T> {
    IdentityCollector() {
        this(Type.ONLY_ONE, false);
    }

    IdentityCollector(Type type) {
        this(type, false);
    }

    IdentityCollector(Type type, boolean required) {
        super(type, required, Function.identity());
    }
}
