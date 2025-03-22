package ppl.common.utils.attire.proxy.server.param;

import ppl.common.utils.attire.proxy.AbstractStatefulParameterInterceptor;
import ppl.common.utils.cache.Cache;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.http.Connection;
import ppl.common.utils.http.entity.JsonEntity;

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
                Integer bodyIndex = null;
                for (int i = 0; i < parameters.length; i++) {
                    if (!isUsed(parameters[i])) {
                        if (bodyIndex == null) {
                            bodyIndex = i;
                        } else {
                            throw new IllegalStateException("Too many json body.");
                        }
                    }
                }

                if (bodyIndex == null) {
                    object = new JsonPojo(jsonAnnotation);
                } else {
                    JsonPojo[] pojo = new JsonPojo[parameters.length];
                    pojo[bodyIndex] = new JsonPojo(jsonAnnotation);
                    object = pojo;
                }
            }
            methodCache.putIfAbsent(method, object);
        }

        if (JSON_UNSUPPORTED != object) {
            if (object instanceof JsonPojo[]) {
                JsonPojo[] pojo = (JsonPojo[]) object;
                for (int i = 0; i < pojo.length; i++) {
                    if (pojo[i] != null) {
                        collector.write(new JsonEntity(pojo[i].getCharset(), parameters[i]));
                        break;
                    }
                }
            }
        }
        return null;
    }
}
