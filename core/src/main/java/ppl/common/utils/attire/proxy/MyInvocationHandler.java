package ppl.common.utils.attire.proxy;

import ppl.common.utils.http.Connector;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class MyInvocationHandler implements InvocationHandler {

    private final Connector connector;
    private final List<RequestBodyInterceptor> requestBodyInterceptors;
    private final List<ResponseInterceptor<?>> responseInterceptors;

    public MyInvocationHandler(
            Connector connector,
            List<RequestBodyInterceptor> requestBodyInterceptors,
            List<ResponseInterceptor<?>> responseInterceptors) {
        this.connector = connector;
        this.requestBodyInterceptors = requestBodyInterceptors;
        this.responseInterceptors = responseInterceptors;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (name.equals("equals") && parameterTypes.length == 1 && parameterTypes[0].equals(Object.class)) {
            return false;
        }
        if (name.equals("hashCode") && parameterTypes.length == 0) {
            return 0;
        }
        if (name.equals("toString") && parameterTypes.length == 0) {
            return "";
        }
        return null;
    }
}
