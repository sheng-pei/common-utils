package ppl.common.utils.config.convert;

import ppl.common.utils.config.ConvertException;
import ppl.common.utils.string.Strings;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Converter<C> {

    private static final String INCOMPATIBLE_TYPE_MESSAGE = "Incompatible with {}";
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final Converter CAST_CONVERTER = new Converter("cast", c -> true, (o, c) -> {
        Class clazz = (Class) c;
        return clazz.cast(o);
    });

    public static <T> Converter<T> castConverter() {
        @SuppressWarnings("unchecked")
        Converter<T> converter = (Converter<T>) CAST_CONVERTER;
        return converter;
    }

    private final String name;
    private final Function<Class<?>, Boolean> acceptFunc;
    private final BiFunction<Object, Class<C>, C> convertFunc;

    public Converter(String name, Function<Class<?>, Boolean> acceptFunc, BiFunction<Object, Class<C>, C> convertFunc) {
        Objects.requireNonNull(name, "Name is null.");
        Objects.requireNonNull(acceptFunc, "AcceptFunction is null.");
        Objects.requireNonNull(convertFunc, "ConvertFunction is null.");
        this.name = name;
        this.acceptFunc = acceptFunc;
        this.convertFunc = convertFunc;
    }

    public Converter(Class<C> targetClass, Function<Class<?>, Boolean> acceptFunc, BiFunction<Object, Class<C>, C> convertFunc) {
        Objects.requireNonNull(targetClass, "TargetClass is null.");
        Objects.requireNonNull(acceptFunc, "AcceptFunction is null.");
        Objects.requireNonNull(convertFunc, "ConvertFunction is null.");
        this.name = targetClass.getCanonicalName();
        this.acceptFunc = acceptFunc;
        this.convertFunc = convertFunc;
    }

    public String name() {
        return this.name;
    }

    public final boolean accept(Class<?> targetClass) {
        return this.acceptFunc.apply(targetClass);
    }

    public final C convert(Object obj, Class<C> targetClass) {
        try {
            return this.convertFunc.apply(obj, targetClass);
        } catch (Throwable t) {
            throw new ConvertException(Strings.format(INCOMPATIBLE_TYPE_MESSAGE, targetClass.getName()), t);
        }
    }

}