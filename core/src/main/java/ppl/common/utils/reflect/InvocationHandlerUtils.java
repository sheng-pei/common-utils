package ppl.common.utils.reflect;

import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class InvocationHandlerUtils {
    public static boolean equals(Object proxy, Object parameter, BiPredicate<Object, Object> predicate) {
        if (proxy == null && parameter == null) {
            return true;
        }

        if (proxy == null || parameter == null) {
            return false;
        }

        boolean tIsProxy = Proxy.isProxyClass(parameter.getClass());
        boolean pIsProxy = Proxy.isProxyClass(proxy.getClass());
        if (!tIsProxy && !pIsProxy) {
            return Objects.equals(proxy, parameter);
        }

        if (!tIsProxy || !pIsProxy) {
            return false;
        }

        return predicate.test(proxy, parameter);
    }

    public static int hashCode(Object proxy, Function<Object, Integer> mapperFunction) {
        if (proxy != null && Proxy.isProxyClass(proxy.getClass())) {
            return mapperFunction.apply(proxy);
        }
        return Objects.hashCode(proxy);
    }

    public static String toString(Object proxy, Function<Object, String> mapperFunction) {
        if (proxy != null && Proxy.isProxyClass(proxy.getClass())) {
            return mapperFunction.apply(proxy);
        }
        return Objects.toString(proxy);
    }
}
