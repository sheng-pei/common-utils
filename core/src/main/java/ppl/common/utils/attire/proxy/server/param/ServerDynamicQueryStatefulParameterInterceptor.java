package ppl.common.utils.attire.proxy.server.param;

import ppl.common.utils.attire.proxy.AbstractStatefulParameterInterceptor;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.http.request.Request;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class ServerDynamicQueryStatefulParameterInterceptor extends AbstractStatefulParameterInterceptor<Request.Builder> implements RequestParameterInterceptor {

    private transient final ConcurrentReferenceValueCache<Method, int[]> methodCache = new ConcurrentReferenceValueCache<>();

    private static int[] dynamicQueriesOf(Method method) {
        Class<?>[] classes = method.getParameterTypes();
        int[] ret = new int[classes.length];
        Arrays.fill(ret, -1);
        for (int i = 0; i < classes.length; i++) {
            if (DQueries.class.isAssignableFrom(classes[i])) {
                ret[i] = i;
            }
        }
        return ret;
    }

    @Override
    protected Request.Builder handleImpl(Method method, Object[] parameters, Request.Builder collector) {
        int[] dynamicQueryIndices = methodCache.getIfPresent(method);
        if (dynamicQueryIndices == null) {
            dynamicQueryIndices = dynamicQueriesOf(method);
            methodCache.putIfAbsent(method, dynamicQueryIndices);
        }

        for (int i = 0; i < parameters.length; i++) {
            int dynamicQueryIndex = dynamicQueryIndices[i];
            if (dynamicQueryIndex >= 0) {
                useExclusive(parameters[i], dqs -> {
                    if (Objects.nonNull(dqs)) {
                        DQueries dQueries = (DQueries) dqs;
                        dQueries.getQueries().forEach(collector::appendQuery);
                    }
                });
            }
        }
        return collector;
    }
}
