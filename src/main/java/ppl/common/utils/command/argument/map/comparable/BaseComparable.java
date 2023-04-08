package ppl.common.utils.command.argument.map.comparable;

import ppl.common.utils.StringUtils;

abstract class BaseComparable<V> {

    protected final Comparable<V> canComparable(V v) {
        if (!(v instanceof Comparable)) {
            throw new IllegalArgumentException(StringUtils.format(
                    "'{}' is not comparable.", v.getClass().getCanonicalName()));
        }

        @SuppressWarnings("unchecked")
        Comparable<V> res = (Comparable<V>) v;
        return res;
    }
}
