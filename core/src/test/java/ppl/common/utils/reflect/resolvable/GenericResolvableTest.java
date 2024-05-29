package ppl.common.utils.reflect.resolvable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

class GenericResolvableTest {

    public interface U {
    }

    public interface V extends U {
    }

    public static class LL<I, J> {

        public class A<Y> {
            public class II<K> {
            }

            public class III<X> extends II<I> implements V {
            }

            public class IV<X extends II<Y>> {
            }

            public class B<X> extends III<X> {

                public B() {
                }

                public X a(X y) {
                    return null;
                }
            }

            public class C<X> extends B<III> {
            }

            public class D<X> extends IV {

            }

        }

        public static LL<Number, String> mostOuterInstanceOwner = new LL<>();
        public static LL<Number, String>.A<Integer> middleInstanceOwner = mostOuterInstanceOwner.new A<>();
        public static LL<Number, String>.A<Integer>.III<String> wholeParameterizedType = middleInstanceOwner.new III<>();
        public static LL<Number, String>.A<Integer>.C<String> someRawParameterizedType = middleInstanceOwner.new C<String>();
        public static LL<Number, String>.A<Integer>.D<String> rawParentParameterizedType = middleInstanceOwner.new D<String>();

    }

    private final GenericResolvable mostOuterOwnerType;
    private final GenericResolvable middleOwnerType;
    private final GenericResolvable wholeParameterizedType;
    private final GenericResolvable someRawParameterizedType;
    private final GenericResolvable rawParentParameterizedType;

    private GenericResolvableTest() {
        try {
            mostOuterOwnerType = Resolvables.getParameterizedTypeResolvable((ParameterizedType) LL.class
                    .getDeclaredField("mostOuterInstanceOwner").getGenericType());
            middleOwnerType = Resolvables.getParameterizedTypeResolvable((ParameterizedType) LL.class
                    .getDeclaredField("middleInstanceOwner").getGenericType());
            wholeParameterizedType = Resolvables.getParameterizedTypeResolvable((ParameterizedType) LL.class
                    .getDeclaredField("wholeParameterizedType").getGenericType());
            someRawParameterizedType = Resolvables.getParameterizedTypeResolvable((ParameterizedType) LL.class
                    .getDeclaredField("someRawParameterizedType").getGenericType());
            rawParentParameterizedType = Resolvables.getParameterizedTypeResolvable((ParameterizedType) LL.class
                    .getDeclaredField("rawParentParameterizedType").getGenericType());
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }
    }

    @Test
    void testGetType() {
        Assertions.assertEquals(LL.class, mostOuterOwnerType.getType());
        Assertions.assertEquals(LL.A.class, middleOwnerType.getType());
        Assertions.assertEquals(LL.A.III.class, wholeParameterizedType.getType());
        Assertions.assertEquals(LL.A.C.class, someRawParameterizedType.getType());
    }

    @Test
    void testGetGenericByTypeVariable() {
        GenericResolvable mostOuterOwnerTypeGeneric = (GenericResolvable) mostOuterOwnerType.getGeneric(
                Resolvables.getTypeVariableResolvable(
                        LL.class.getTypeParameters()[0]));
        GenericResolvable middleOwnerTypeGeneric = (GenericResolvable) middleOwnerType.getGeneric(
                Resolvables.getTypeVariableResolvable(
                        LL.A.class.getTypeParameters()[0]));
        GenericResolvable wholeParameterizedTypeGeneric = (GenericResolvable) wholeParameterizedType.getGeneric(
                Resolvables.getTypeVariableResolvable(LL.A.III.class.getTypeParameters()[0]));
        GenericResolvable someRawParameterizedTypeGeneric = (GenericResolvable) someRawParameterizedType.getGeneric(
                Resolvables.getTypeVariableResolvable(LL.A.C.class.getTypeParameters()[0]));
        Assertions.assertEquals(Number.class, mostOuterOwnerTypeGeneric.getType());
        Assertions.assertEquals(Integer.class, middleOwnerTypeGeneric.getType());
        Assertions.assertEquals(String.class, wholeParameterizedTypeGeneric.getType());
        Assertions.assertEquals(String.class, someRawParameterizedTypeGeneric.getType());
    }

    @Test
    void testGetGenericByIndex() {
        GenericResolvable mostOuterOwnerTypeGeneric = (GenericResolvable) mostOuterOwnerType.getGeneric(0);
        GenericResolvable middleOwnerTypeGeneric = (GenericResolvable) middleOwnerType.getGeneric(0);
        GenericResolvable wholeParameterizedTypeGeneric = (GenericResolvable) wholeParameterizedType.getGeneric(0);
        GenericResolvable someRawParameterizedTypeGeneric = (GenericResolvable) someRawParameterizedType.getGeneric(0);
        Assertions.assertEquals(Number.class, mostOuterOwnerTypeGeneric.getType());
        Assertions.assertEquals(Integer.class, middleOwnerTypeGeneric.getType());
        Assertions.assertEquals(String.class, wholeParameterizedTypeGeneric.getType());
        Assertions.assertEquals(String.class, someRawParameterizedTypeGeneric.getType());
    }

    @Test
    void testGetGenerics() {
        Resolvable[] mostOuterOwnerTypeGenerics = mostOuterOwnerType.getGenerics();
        Resolvable[] middleOwnerTypeGenerics = middleOwnerType.getGenerics();
        Resolvable[] wholeParameterizedTypeGenerics = wholeParameterizedType.getGenerics();
        Resolvable[] someRawParameterizedTypeGenerics = someRawParameterizedType.getGenerics();
        Assertions.assertArrayEquals(new Class<?>[] {Number.class, String.class}, Arrays.stream(mostOuterOwnerTypeGenerics)
                .map(g -> (GenericResolvable) g)
                .map(GenericResolvable::getType)
                .toArray(Class<?>[]::new));
        Assertions.assertArrayEquals(new Class<?>[] {Integer.class}, Arrays.stream(middleOwnerTypeGenerics)
                .map(g -> (GenericResolvable) g)
                .map(GenericResolvable::getType)
                .toArray(Class<?>[]::new));
        Assertions.assertArrayEquals(new Class<?>[] {String.class}, Arrays.stream(wholeParameterizedTypeGenerics)
                .map(g -> (GenericResolvable) g)
                .map(GenericResolvable::getType)
                .toArray(Class<?>[]::new));
        Assertions.assertArrayEquals(new Class<?>[] {String.class}, Arrays.stream(someRawParameterizedTypeGenerics)
                .map(g -> (GenericResolvable) g)
                .map(GenericResolvable::getType)
                .toArray(Class<?>[]::new));
    }

    @Test
    void testGetParent() {
        GenericResolvable wholeParent = (GenericResolvable) wholeParameterizedType.getParent();
        Assertions.assertEquals(LL.A.II.class, wholeParent.getType());
        Assertions.assertEquals(Number.class, ((GenericResolvable) wholeParent.getGeneric(0)).getType());
        Assertions.assertEquals(middleOwnerType, wholeParent.getOwner());
        GenericResolvable someRawParent = (GenericResolvable) someRawParameterizedType.getParent();
        GenericResolvable someRawParentGeneric = (GenericResolvable) someRawParent.getGeneric(0);
        GenericResolvable p = (GenericResolvable) someRawParentGeneric.getParent();
        Assertions.assertEquals(LL.A.II.class, p.getType());
        Assertions.assertEquals(Number.class, ((GenericResolvable) p.getGeneric(0)).getType());
    }

    @Test
    void testGetOwner() {
        Assertions.assertEquals(mostOuterOwnerType, middleOwnerType.getOwner());
        GenericResolvable someRawParent = (GenericResolvable) someRawParameterizedType.getParent();
        GenericResolvable someRawParentGeneric = (GenericResolvable) someRawParent.getGeneric(0);
        Assertions.assertEquals(middleOwnerType, someRawParentGeneric.getOwner());
        GenericResolvable rawParent = (GenericResolvable) rawParentParameterizedType.getParent();
        BoundResolvable rawParentGeneric = (BoundResolvable) rawParent.getGeneric(0);
        GenericResolvable bound = (GenericResolvable) rawParentGeneric.getBound(0);
        Assertions.assertEquals(middleOwnerType, bound.getOwner());
    }
}