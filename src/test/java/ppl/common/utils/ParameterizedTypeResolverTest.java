package ppl.common.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ParameterizedTypeResolverTest {

    private interface Interface1<X> {
    }

    private interface Interface2<X> {

    }
    private static class Target<X, Y> {
    }

    private static class Extend<X, Y> extends Target<Y, X> implements Interface1<X>, Interface2<Y> {
        private Target<Y, X> field;
        private Target<Y, X> method(Target<X, Y> target) {return null;}
        private class Inner extends Target<Y, X> {

        }
        private class InnerG<X> extends Target<Y, X> {

        }
    }

    private Extend<String, Integer> field;

    private Extend<String, Integer> method(Extend<String, Integer> target) {
        return null;
    }

    @Test
    void resolveField() throws Exception {
        Field field = ParameterizedTypeResolverTest.class.getDeclaredField("field");
        TypeResolver typeResolver = TypeResolver.make(field);
        Class<?>[] expected = new Class<?>[] {String.class, Integer.class};
        assertArrayEquals(expected, typeResolver.getActualTypeParameters());
    }

    @Test
    void resolveSuper() throws Exception {
        Field field = ParameterizedTypeResolverTest.class.getDeclaredField("field");
        TypeResolver typeResolver = TypeResolver.make(field);
        TypeResolver superTypeResolver = typeResolver.getSuperType();
        Class<?>[] expected = new Class<?>[] {Integer.class, String.class};
        assertArrayEquals(expected, superTypeResolver.getActualTypeParameters());
    }

    @Test
    void resolveInterface() throws Exception {
        Field field = ParameterizedTypeResolverTest.class.getDeclaredField("field");
        TypeResolver typeResolver = TypeResolver.make(field);
        TypeResolver[] interfaceTypeResolvers = typeResolver.getInterfaceTypes();
        Class<?>[] expected = new Class[] {String.class, Integer.class};
        assertArrayEquals(expected,
                Arrays.stream(interfaceTypeResolvers)
                        .map(TypeResolver::getActualTypeParameters)
                        .map(a -> a[0])
                        .toArray(Type[]::new));
    }

    @Test
    void resolveGenericClassField() throws Exception {
        Field field = ParameterizedTypeResolverTest.class.getDeclaredField("field");
        TypeResolver typeResolver = TypeResolver.make(field);
        TypeResolver fieldResolver = typeResolver.getFieldType("field");
        Class<?>[] expected = new Class<?>[] {Integer.class, String.class};
        assertArrayEquals(expected, fieldResolver.getActualTypeParameters());
    }

    @Test
    void resolveGenericClassMethodParameter() throws Exception {
//        Field field = ParameterizedTypeResolverTest.class.getDeclaredField("mem");
//        ParameterizedTypeResolver resolver = ParameterizedTypeResolver.make((ParameterizedType) field.getGenericType());
//        Method methodField = Mem.class.getDeclaredMethod("method", Target.class);
//        ParameterizedType target = (ParameterizedType) methodField.getGenericParameterTypes()[0];
//        Class<?>[] expected = new Class<?>[] {String.class, Integer.class};
//        assertArrayEquals(expected, resolver.resolveVariables(target.getActualTypeArguments()));
    }

    @Test
    void resolveMethodParameter() throws Exception {
        Method method = ParameterizedTypeResolverTest.class.getDeclaredMethod("method", Extend.class);
        Type[] paramTypes = method.getGenericParameterTypes();
        ParameterizedType parameterizedType = (ParameterizedType) paramTypes[0];
        Class<?>[] expected = new Class<?>[] {String.class, Integer.class};
        assertArrayEquals(expected, parameterizedType.getActualTypeArguments());
    }

    @Test
    void resolveMethodReturn() throws Exception {
        Method method = ParameterizedTypeResolverTest.class.getDeclaredMethod("method", Extend.class);
        ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
        Class<?>[] expected = new Class<?>[] {String.class, Integer.class};
        assertArrayEquals(expected, parameterizedType.getActualTypeArguments());
    }

    @Test
    void resolveGenericClassMethodReturn() throws Exception {
//        Field field = ParameterizedTypeResolverTest.class.getDeclaredField("mem");
//        ParameterizedTypeResolver resolver = ParameterizedTypeResolver.make((ParameterizedType) field.getGenericType());
//        Method methodField = Mem.class.getDeclaredMethod("method", Target.class);
//        ParameterizedType target = (ParameterizedType) methodField.getGenericReturnType();
//        Class<?>[] expected = new Class<?>[] {Integer.class, String.class};
//        assertArrayEquals(expected, resolver.resolveVariables(target.getActualTypeArguments()));
    }

    @Test
    void resolveGenericClassInnerSuper() throws Exception {
//        Field field = ParameterizedTypeResolverTest.class.getDeclaredField("mem");
//        ParameterizedTypeResolver resolver = ParameterizedTypeResolver.make((ParameterizedType) field.getGenericType());
//        ParameterizedType target = (ParameterizedType) Mem.Inner.class.getGenericSuperclass();
//        Class<?>[] expected = new Class<?>[] {Integer.class, String.class};
//        assertArrayEquals(expected, resolver.resolveVariables(target.getActualTypeArguments()));
    }


}