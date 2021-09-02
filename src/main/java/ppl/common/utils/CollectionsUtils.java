package ppl.common.utils;

import java.util.Collection;

public final class CollectionsUtils {
    private CollectionsUtils() {}

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }
}
