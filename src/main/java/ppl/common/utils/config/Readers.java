package ppl.common.utils.config;

import ppl.common.utils.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Readers {

    private Readers() {}

    public static Reader root(Object object) {
        if (object == null) {
            return new NullReaderImpl();
        }

        LinkedHashMap<Class<?>, Object> params = new LinkedHashMap<>();
        params.put(Object.class, object);
        return create(object, c -> newer(c, params));
    }

    static Reader newer(Class<?> readerClass, LinkedHashMap<Class<?>, Object> params) {

        List<Class<?>> paramClasses = new ArrayList<>(params.size());
        List<Object> paramValues = new ArrayList<>(params.size());
        params.forEach((c, v) -> {
            paramClasses.add(c);
            paramValues.add(v);
        });
        try {
            Constructor<?> constructor = readerClass.getDeclaredConstructor(paramClasses.toArray(new Class<?>[0]));
            return (Reader) constructor.newInstance(paramValues.toArray());
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                    StringUtils.format(
                            "The specified class {} couldn't instantiate. " +
                                    "For example, This class is abstract.",
                            readerClass.getCanonicalName()));
        } catch (InvocationTargetException e) {
            throw new ReaderException(
                    StringUtils.format("Failed to create {} reader", rootOrChild(paramClasses)),
                    e.getCause());
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    StringUtils.format(
                            "The specified class {} couldn't find constructor {}({})",
                            readerClass.getCanonicalName(), readerClass.getCanonicalName(), paramList(paramClasses)));
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(
                    StringUtils.format(
                            "The constructor {}({}) of the specified class {} couldn't be access.",
                            readerClass.getCanonicalName(), paramList(paramClasses), readerClass.getCanonicalName()));
        }
    }

    private static String rootOrChild(List<Class<?>> paramClasses) {
        return paramClasses.size() == 1 ? "root" : "child";
    }

    private static String paramList(List<Class<?>> paramClasses) {
        StringBuilder builder = new StringBuilder();
        paramClasses.forEach(c -> builder.append(c.getSimpleName()).append(", "));
        if (builder.length() > 2) {
            builder.setLength(builder.length() - 2);
        }
        return builder.toString();
    }

    static Reader create(Object object, Function<Class<?>, Reader> newer) {
        if (object == null) {
            return newer.apply(NullReaderImpl.class);
        }

        if (object instanceof Map) {
            return newer.apply(MapReaderImpl.class);
        }

        if (object instanceof List) {
            return newer.apply(ListReaderImpl.class);
        }

        return newer.apply(ScalarReaderImpl.class);
    }

}
