package ppl.common.utils.argument;

import java.util.function.BiFunction;

public class Argument<K, V> {
    private final K name;
    private final BiFunction<Argument<K, V>, V, String> toCanonicalString;

    protected Argument(K name,
                       BiFunction<? extends Argument<K, V>, V, String> toCanonicalString) {
        if (name == null) {
            throw new IllegalArgumentException("Argument name is required.");
        }
        if (toCanonicalString == null) {
            throw new IllegalArgumentException("ToCanonicalString is required.");
        }

        this.name = name;
        @SuppressWarnings("unchecked")
        BiFunction<Argument<K, V>, V, String> tmp =
                (BiFunction<Argument<K, V>, V, String>) toCanonicalString;
        this.toCanonicalString = tmp;
    }

    public K getName() {
        return this.name;
    }

    public String toCanonicalString(V value) {
        return toCanonicalString.apply(this, value);
    }
}
