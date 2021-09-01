package ppl.common.utils.config.convert;

import ppl.common.utils.StringUtils;
import ppl.common.utils.reflect.TypeCompatibleUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

enum BaseType {

    INT("int", c -> {
        return Integer.class.equals(TypeCompatibleUtils.box(c));
    }),
    LONG("long", c -> {
        return Long.class.equals(TypeCompatibleUtils.box(c));
    }),
    DOUBLE("double", c -> {
        return Double.class.equals(TypeCompatibleUtils.box(c));
    }),
    BOOL("bool", c -> {
        return Boolean.class.equals(TypeCompatibleUtils.box(c));
    }),
    STRING("string", String.class::equals),
    LIST("list", Collection.class::isAssignableFrom),
    MAP("map", Map.class::isAssignableFrom);


    private String name;
    private Function<Class<?>, Boolean> isValid;

    BaseType(String name, Function<Class<?>, Boolean> isValid) {
        this.name = name;
        this.isValid = isValid;
    }

    static String nameOf(Class<?> targetClass) {
        BaseType type = enumOf(targetClass);
        if (type == null) {
            return targetClass.getCanonicalName() == null ? "local or anonymous class" : targetClass.getCanonicalName();
        }

        return StringUtils.format("base({})", type.name);
    }

    private static BaseType enumOf(Class<?> clazz) {
        List<BaseType> types = Arrays.stream(BaseType.values())
                .filter(bt -> bt.isValid.apply(clazz))
                .collect(Collectors.toList());
        if (types.size() > 1) {
            throw new RuntimeException(
                    StringUtils.format("One class {} couldn't be multiple base type.", clazz));
        }
        return types.isEmpty() ? null : types.get(0);
    }

}
