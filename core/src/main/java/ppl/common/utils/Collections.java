package ppl.common.utils;

import java.util.Collection;

public final class Collections {
    private Collections() {}

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

}
