package ppl.common.utils;

import java.util.Collection;

public final class CollectionUtils {
    private CollectionUtils() {}

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

}
