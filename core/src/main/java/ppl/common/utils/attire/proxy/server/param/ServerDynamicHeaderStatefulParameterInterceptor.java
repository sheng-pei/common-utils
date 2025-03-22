package ppl.common.utils.attire.proxy.server.param;

import ppl.common.utils.Arrays;
import ppl.common.utils.attire.proxy.AbstractStatefulParameterInterceptor;
import ppl.common.utils.cache.Cache;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.http.request.Request;

import java.lang.reflect.Method;
import java.util.Objects;

public class ServerDynamicHeaderStatefulParameterInterceptor extends AbstractStatefulParameterInterceptor<Request.Builder> implements RequestParameterInterceptor {

    private transient final Cache<Method, int[]> methodCache = new ConcurrentReferenceValueCache<>();

    private static int[] dynamicHeadersOf(Method method) {
        Class<?>[] classes = method.getParameterTypes();
        int[] ret = new int[classes.length];
        Arrays.fill(ret, -1);
        for (int i = 0; i < classes.length; i++) {
            if (DHeaders.class.isAssignableFrom(classes[i])) {
                ret[i] = i;
            }
        }
        return ret;
    }

    @Override
    protected Request.Builder handleImpl(Method method, Object[] parameters, Request.Builder collector) {
        int[] dynamicHeaderIndices = methodCache.getIfPresent(method);
        if (dynamicHeaderIndices == null) {
            dynamicHeaderIndices = dynamicHeadersOf(method);
            methodCache.putIfAbsent(method, dynamicHeaderIndices);
        }

        for (int i = 0; i < parameters.length; i++) {
            int dynamicHeaderIndex = dynamicHeaderIndices[i];
            if (dynamicHeaderIndex >= 0) {
                useExclusive(parameters[i], o -> {
                    if (Objects.nonNull(o)) {
                        DHeaders headers = (DHeaders) o;
                        headers.getHeaders().forEach(collector::appendHeader);
                    }
                });
            }
        }
        return collector;
    }
}
