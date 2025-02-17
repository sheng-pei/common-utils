package ppl.common.utils;

import java.util.Map;

public final class Maps {
    private Maps() {}

    public static boolean isEmpty(Map<?, ?> c) {
        return c == null || c.isEmpty();
    }
}
