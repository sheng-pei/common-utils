package ppl.common.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EnumUtils {
    public static <E extends Enum<E>> E mustAndEnumOf(Class<E> enumClass, Function<E, Boolean> key) {
        E result = enumOf(enumClass, key);
        if (result == null) {
            throw new UnknownEnumException(enumClass, key);
        }
        return result;
    }

    public static <E extends Enum<E>> boolean contains(Class<E> enumClass, Function<E ,Boolean> key) {
        return enumOf(enumClass, key) != null;
    }

    private static <E extends Enum<E>> E enumOf(Class<E> enumClass, Function<E, Boolean> key) {
        E[] allEnums = enumClass.getEnumConstants();
        return Arrays.stream(allEnums)
                .filter(key::apply)
                .findFirst()
                .orElse(null);
    }

    public static <E extends Enum<E>, K> Set<K> extractKeys(Class<E> enumClass, Function<E ,K> keyExtractor) {
        E[] allEnums = enumClass.getEnumConstants();

        assertEnumDuplicate(allEnums, keyExtractor);

        return Collections.unmodifiableSet(Arrays.stream(allEnums)
                .map(keyExtractor)
                .collect(Collectors.toSet()));
    }

    private static <E extends Enum<E>, K> void assertEnumDuplicate(E[] allEnums, Function<E ,K> extractor) {
        assert Arrays.stream(allEnums)
                .map(extractor)
                .distinct()
                .count() == allEnums.length
                : String.format("Enum key must be unique, but %s has duplicated key", allEnums.getClass().getComponentType().getCanonicalName());
    }
}
