package ppl.common.utils.command.argument.map;

import ppl.common.utils.command.argument.Mapper;
import ppl.common.utils.command.argument.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

public class DefaultValue<V> implements Mapper<V, V> {

    @SuppressWarnings("rawtypes")
    private static final Predicate NULL_PREDICATE = Objects::isNull;

    private static <V> Predicate<V> nullPredicate() {
        @SuppressWarnings("unchecked")
        Predicate<V> res = (Predicate<V>) NULL_PREDICATE;
        return res;
    }

    private final V def;
    private final Predicate<V> predicate;

    public DefaultValue(V def) {
        this(def, null);
    }

    public DefaultValue(V def, Predicate<V> predicate) {
        Objects.requireNonNull(def, "Default value is required.");
        this.def = def;
        this.predicate = predicate == null ? nullPredicate() : predicate;
    }

    @Nullable
    @Override
    public V map(V v) {
        return predicate.test(v) ? def : v;
    }
}
