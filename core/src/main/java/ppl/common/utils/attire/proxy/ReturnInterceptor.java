package ppl.common.utils.attire.proxy;

import java.lang.reflect.Method;

public interface ReturnInterceptor<R> {
    boolean accept(R response);
    Object handle(R response, Method method);
}
