package ppl.common.utils.config.convert;

import ppl.common.utils.Condition;
import ppl.common.utils.enumerate.EnumUtils;
import ppl.common.utils.StringUtils;
import ppl.common.utils.TypeUtils;
import ppl.common.utils.enumerate.EnumEncoderNotSupportedException;
import ppl.common.utils.enumerate.UnknownEnumException;
import ppl.common.utils.logging.Logger;
import ppl.common.utils.logging.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Converter<C> {

    private static final Logger logger = LoggerFactory.getLogger(Converter.class);

    private static final String INCOMPATIBLE_TYPE_MESSAGE = "Incompatible with {}";

    private final String name;
    private final BiFunction<Object, Class<?>, C> convertFunc;

    public Converter(String name, BiFunction<Object, Class<?>, C> convertFunc) {
        this.name = name;
        this.convertFunc = convertFunc;
    }

    public Converter(Class<?> targetClazz, BiFunction<Object, Class<?>, C> convertFunc) {
        this.name = targetClazz.getCanonicalName();
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

    public C convert(Object obj, Class<C> targetClazz) {
        try {
            return this.convertFunc.apply(obj, targetClazz);
        } catch (Throwable t) {
            throw new ConvertException(StringUtils.format(INCOMPATIBLE_TYPE_MESSAGE, this.name), t);
        }
    }

}