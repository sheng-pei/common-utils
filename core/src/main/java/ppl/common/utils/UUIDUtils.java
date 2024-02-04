package ppl.common.utils;

import java.util.UUID;

public final class UUIDUtils {

    private UUIDUtils() { }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String rawUuid() {
        return UUID.randomUUID().toString();
    }
}
