package ppl.common.utils.config.convert;

import ppl.common.utils.config.ConvertException;
import ppl.common.utils.StringUtils;

import java.util.Objects;
import java.util.function.BiFunction;

public class Converter<C> {

    private static final String INCOMPATIBLE_TYPE_MESSAGE = "Incompatible with {}";

    private final String name;
    private final BiFunction<Object, Class<?>, C> convertFunc;

    public Converter(String name, BiFunction<Object, Class<?>, C> convertFunc) {
        Objects.requireNonNull(name, "Name is null.");
        Objects.requireNonNull(convertFunc, "ConvertFunction is null.");
        this.name = name;
        this.convertFunc = convertFunc;
    }

    public Converter(Class<?> targetClass, BiFunction<Object, Class<?>, C> convertFunc) {
        Objects.requireNonNull(targetClass, "TargetClass is null.");
        Objects.requireNonNull(convertFunc, "ConvertFunction is null.");
        this.name = targetClass.getCanonicalName();
        this.convertFunc = convertFunc;
    }

    public String name() {
        return this.name;
    }

    public C convert(Object obj) {
        try {
            return this.convertFunc.apply(obj, null);
        } catch (Throwable t) {
            throw new ConvertException(StringUtils.format(INCOMPATIBLE_TYPE_MESSAGE, this.name));
        }
    }

    public C convert(Object obj, Class<C> targetClass) {
        try {
            return this.convertFunc.apply(obj, targetClass);
        } catch (Throwable t) {
            throw new ConvertException(StringUtils.format(INCOMPATIBLE_TYPE_MESSAGE, targetClass.getName()), t);
        }
    }

}