package ppl.common.utils.reflect.resolvable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.stream.Stream;

class WildcardTypeResolvableTest {
    private static List<?> any;
    private static List<? extends Number> extendsNumber;
    private static List<? super Number> superNumber;
    private static List<? extends Object> extendsObject;
    private static List<? super Object> superObject;

    @ParameterizedTest
    @MethodSource("wildcardTypeResolvableProvider")
    void testGetKind(WildcardTypeResolvable resolvable, BoundKind kind, Resolvable[] bounds) {
        Assertions.assertEquals(kind, resolvable.getKind());
        Assertions.assertArrayEquals(bounds, resolvable.getBounds());
    }

    private static Stream<Arguments> wildcardTypeResolvableProvider() {
        WildcardTypeResolvable anyResolvable;
        WildcardTypeResolvable extendsNumberResolvable;
        WildcardTypeResolvable superNumberResolvable;
        WildcardTypeResolvable extendsObjectResolvable;
        WildcardTypeResolvable superObjectResolvable;
        Class<?> clazz = WildcardTypeResolvableTest.class;
        try {
            ParameterizedType anyPt = (ParameterizedType) clazz
                    .getDeclaredField("any")
                    .getGenericType();
            ParameterizedType extendsNumberPt = (ParameterizedType) clazz
                    .getDeclaredField("extendsNumber")
                    .getGenericType();
            ParameterizedType superNumberPt = (ParameterizedType) clazz
                    .getDeclaredField("superNumber")
                    .getGenericType();
            ParameterizedType extendsObjectPt = (ParameterizedType) clazz
                    .getDeclaredField("extendsObject")
                    .getGenericType();
            ParameterizedType superObjectPt = (ParameterizedType) clazz
                    .getDeclaredField("superObject")
                    .getGenericType();
            anyResolvable = Resolvables.getWildcardTypeResolvable(
                    (WildcardType) anyPt.getActualTypeArguments()[0]);
            extendsNumberResolvable = Resolvables.getWildcardTypeResolvable(
                    (WildcardType) extendsNumberPt.getActualTypeArguments()[0]);
            superNumberResolvable = Resolvables.getWildcardTypeResolvable(
                    (WildcardType) superNumberPt.getActualTypeArguments()[0]);
            extendsObjectResolvable = Resolvables.getWildcardTypeResolvable(
                    (WildcardType) extendsObjectPt.getActualTypeArguments()[0]);
            superObjectResolvable = Resolvables.getWildcardTypeResolvable(
                    (WildcardType) superObjectPt.getActualTypeArguments()[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Stream.of(
                Arguments.of(anyResolvable, BoundKind.UPPER,
                        new Resolvable[]{Resolvables.getClassResolvable(Object.class)}),
                Arguments.of(extendsNumberResolvable, BoundKind.UPPER,
                        new Resolvable[]{Resolvables.getClassResolvable(Number.class)}),
                Arguments.of(superNumberResolvable, BoundKind.LOWER,
                        new Resolvable[]{Resolvables.getClassResolvable(Number.class)}),
                Arguments.of(extendsObjectResolvable, BoundKind.UPPER,
                        new Resolvable[]{Resolvables.getClassResolvable(Object.class)}),
                Arguments.of(superObjectResolvable, BoundKind.LOWER,
                        new Resolvable[]{Resolvables.getClassResolvable(Object.class)})
        );
    }

}
