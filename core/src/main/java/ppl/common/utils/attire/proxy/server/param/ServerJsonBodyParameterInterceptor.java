package ppl.common.utils.attire.proxy.server.param;

import ppl.common.utils.attire.proxy.AbstractStatefulParameterInterceptor;
import ppl.common.utils.cache.Cache;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.http.Connection;

import java.lang.reflect.Method;

public class ServerJsonBodyParameterInterceptor extends AbstractStatefulParameterInterceptor<Connection> implements BodyParameterInterceptor {

    private static final Object JSON_UNSUPPORTED = new Object();

    private transient final Cache<Method, Object> methodCache = new ConcurrentReferenceValueCache<>();

    @Override
    protected Connection handleImpl(Method method, Object[] parameters, Connection collector) {
        Object object = methodCache.getIfPresent(method);
        if (object == null) {
            Class<?> clazz = method.getDeclaringClass();
            Json jsonAnnotation = method.getAnnotation(Json.class);
            if (jsonAnnotation == null) {
                jsonAnnotation = clazz.getAnnotation(Json.class);
            }

            if (jsonAnnotation == null) {
                object = JSON_UNSUPPORTED;
            } else {
                int bodyIndex = -1;
                for (int i = 0; i < parameters.length; i++) {
                    if (!isUsed(parameters[i])) {
                        if (bodyIndex < 0) {
                            bodyIndex = i;
                        } else {
                            throw new IllegalStateException("Too many json body.");
                        }
                    }
                }

                JsonPojo[] pojo = new JsonPojo[parameters.length];
                if (bodyIndex >= 0) {
                    pojo[bodyIndex] = new JsonPojo(jsonAnnotation);
                }
                object = pojo;
            }
            methodCache.putIfAbsent(method, object);
        }

        if (JSON_UNSUPPORTED != object) {
            JsonPojo[] pojo = (JsonPojo[]) object;
            for (int i = 0; i < pojo.length; i++) {
                if (pojo[i] != null) {
                    collector.write(new JsonEntity(pojo[i].getCharset(), unwrap(parameters[i])));
                    return collector;
                }
            }
            return collector;
        }
        return null;
    }
}
