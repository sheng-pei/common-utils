package ppl.common.utils.attire.proxy;

import java.lang.reflect.Method;

public interface CallInitializer<T> {
    boolean accept(Class<?> proxyClass);
    boolean accept(Method method);
    T initialize(Class<?> proxyClass, Method method, Object[] args);
}
