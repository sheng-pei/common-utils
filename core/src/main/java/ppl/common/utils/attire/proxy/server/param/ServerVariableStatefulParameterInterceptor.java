package ppl.common.utils.attire.proxy.server.param;

import ppl.common.utils.attire.proxy.AbstractStatefulParameterInterceptor;
import ppl.common.utils.cache.Cache;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.string.Strings;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ServerVariableStatefulParameterInterceptor extends AbstractStatefulParameterInterceptor<Map<String, String>> implements VariableParameterInterceptor {

    private transient final Cache<Method, String[]> methodCache = new ConcurrentReferenceValueCache<>();

    private static String[] variablesOf(Method method) {
        Parameter[] parameters = method.getParameters();
        String[] names = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.isAnnotationPresent(Variable.class)) {
                VariablePojo variable = new VariablePojo(parameter.getAnnotation(Variable.class));
                if (variable.name().isEmpty()) {
                    if (!parameter.isNamePresent()) {
                        throw new IllegalArgumentException(Strings.format(
                                "Name for variable argument of position [{}] not specified, " +
                                        "and parameter name information not available via reflection. " +
                                        "Ensure that the compiler uses the '-parameters' flag.", i));
                    }
                    names[i] = parameter.getName();
                } else {
                    names[i] = variable.name();
                }
            } else {
                names[i] = null;
            }
        }
        return names;
    }

    @Override
    protected Map<String, String> handleImpl(Method method, Object[] parameters, Map<String, String> collector) {
        String[] names = methodCache.getIfPresent(method);
        if (names == null) {
            names = variablesOf(method);
            methodCache.putIfAbsent(method, names);
        }

        Map<String, String> ret = collector == null ? new HashMap<>() : new HashMap<>(collector);
        for (int i = 0; i < names.length; i++) {
            if (names[i] != null) {
                String name = names[i];
                useShare(parameters[i], o -> {
                    ret.put(name, o == null ? null : Objects.toString(o));
                });
            }
        }
        return Collections.unmodifiableMap(ret);
    }
}
