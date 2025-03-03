package ppl.common.utils.attire.proxy;

import java.lang.reflect.Method;

public interface ResponseInterceptor<T> {
    boolean accept(Method method, Object response);
    T handle(Method method, Object response);
}
