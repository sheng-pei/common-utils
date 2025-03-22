package ppl.common.utils.attire.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Proxies {
    public static Object create(Class<?>[] interfaces, InvocationHandler handler) {
        return create(Proxies.class.getClassLoader(), interfaces, handler);
    }

    public static Object create(
            ClassLoader cl,
            Class<?>[] interfaces, InvocationHandler handler) {
        if (handler instanceof PrerequisiteInvocationHandler) {
            PrerequisiteInvocationHandler pInvocationHandler = (PrerequisiteInvocationHandler) handler;
            pInvocationHandler.assertInterfaces(interfaces);
        }
        return Proxy.newProxyInstance(cl, interfaces, handler);
    }
}
