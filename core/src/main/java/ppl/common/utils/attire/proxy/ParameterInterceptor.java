package ppl.common.utils.attire.proxy;

import java.lang.reflect.Method;

public interface ParameterInterceptor<T> {
    T handle(Method method, Object[] parameters, T collector);
}
