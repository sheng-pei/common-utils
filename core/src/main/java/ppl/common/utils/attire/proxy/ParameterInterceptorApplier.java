package ppl.common.utils.attire.proxy;

import java.lang.reflect.Method;

public final class ParameterInterceptorApplier {
    private ParameterInterceptorApplier() {}

    public static <T> T handle(ParameterInterceptor<T> parameterInterceptor, Method method, Object[] parameters, T collector) {
        if (parameterInterceptor instanceof AbstractStatefulParameterInterceptor) {
            return parameterInterceptor.handle(method, parameters, collector);
        } else {
            return parameterInterceptor.handle(method, AbstractStatefulParameterInterceptor.unwrap(parameters), collector);
        }
    }
}
