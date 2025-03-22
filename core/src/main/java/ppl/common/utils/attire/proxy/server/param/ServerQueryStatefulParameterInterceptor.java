package ppl.common.utils.attire.proxy.server.param;

import ppl.common.utils.Arrays;
import ppl.common.utils.attire.proxy.AbstractStatefulParameterInterceptor;
import ppl.common.utils.cache.Cache;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.http.request.Request;
import ppl.common.utils.string.Strings;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class ServerQueryStatefulParameterInterceptor extends AbstractStatefulParameterInterceptor<Request.Builder> implements RequestParameterInterceptor {

    private transient final Cache<Method, String[][]> methodCache = new ConcurrentReferenceValueCache<>();

    private static String[][] queriesOf(Method method) {
        Parameter[] parameters = method.getParameters();
        List<String[]> names = new ArrayList<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.isAnnotationPresent(Query.class)) {
                QueryPojo query = new QueryPojo(parameter.getAnnotation(Query.class));
                if (query.names().length == 0) {
                    if (!parameter.isNamePresent()) {
                        throw new IllegalArgumentException(Strings.format(
                                "Name for query argument of position [{}] not specified, " +
                                        "and parameter name information not available via reflection. " +
                                        "Ensure that the compiler uses the '-parameters' flag.", i));
                    }
                    names.add(new String[]{parameter.getName()});
                } else {
                    names.add(query.names());
                }
            } else {
                names.add(Arrays.zeroString());
            }
        }
        return names.toArray(Arrays.zero(String[].class));
    }

    @Override
    protected Request.Builder handleImpl(Method method, Object[] parameters, Request.Builder collector) {
        String[][] names = methodCache.getIfPresent(method);
        if (names == null) {
            names = queriesOf(method);
            methodCache.putIfAbsent(method, names);
        }

        for (int i = 0; i < names.length; i++) {
            String[] ns = names[i];
            if (ns.length > 0) {
                useShare(parameters[i], o -> {
                    for (String n : ns) {
                        collector.appendQuery(n, o);
                    }
                });
            }
        }
        return collector;
    }
}
