package ppl.common.utils.attire.proxy.server.param;

import ppl.common.utils.attire.proxy.AbstractStatefulParameterInterceptor;
import ppl.common.utils.attire.proxy.server.util.ParamUtils;
import ppl.common.utils.cache.Cache;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.http.Connection;
import ppl.common.utils.http.entity.WwwFormEntity;
import ppl.common.utils.http.header.value.mediatype.Mime;
import ppl.common.utils.reflect.Types;
import ppl.common.utils.string.Strings;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

public class ServerWwwFormBodyParameterInterceptor extends AbstractStatefulParameterInterceptor<Connection> implements BodyParameterInterceptor {

    private static final Object WWW_FORM_UNSUPPORTED = new Object();

    private transient final Cache<Method, Object> methodCache = new ConcurrentReferenceValueCache<>();

    @Override
    protected Connection handleImpl(Method method, Object[] parameters, Connection collector) {
        Object object = methodCache.getIfPresent(method);
        if (object == null) {
            Class<?> clazz = method.getDeclaringClass();
            WwwForm wwwFormAnnotation = method.getAnnotation(WwwForm.class);
            if (wwwFormAnnotation == null) {
                wwwFormAnnotation = clazz.getAnnotation(WwwForm.class);
            }

            if (wwwFormAnnotation == null) {
                object = WWW_FORM_UNSUPPORTED;
            } else {
                int[] remainParameters = remainParameters(parameters);
                Parameter[] ps = method.getParameters();
                ParamUtils.completeBodyParam(ps, remainParameters);
                object = ParamUtils.parseBodyParam(ps, remainParameters);
            }
            methodCache.putIfAbsent(method, object);
        }

        if (WWW_FORM_UNSUPPORTED != object) {
            WwwFormEntity entity = null;
            ParamPojo[] pojo = (ParamPojo[]) object;
            Object[] unwrapped = unwrap(parameters);
            for (int i = 0; i < pojo.length; i++) {
                if (pojo[i] != null) {
                    if (entity == null) {
                        entity = new WwwFormEntity();
                    }

                    if (unwrapped[i] != null) {
                        if (Types.isWrapper(unwrapped[i].getClass())) {
                            entity.addField(pojo[i].name(), Objects.toString(unwrapped[i]));
                        } else if (Types.isString(unwrapped[i])) {
                            entity.addField(pojo[i].name(), (String) unwrapped[i]);
                        } else {
                            throw new IllegalArgumentException(Strings.format(
                                    "Non base type is not supported by '{}'", Mime.X_WWW_FORM_URLENCODED));
                        }
                    }
                }
            }

            if (entity != null) {
                collector.write(entity);
            }
            return collector;
        }
        return null;
    }
}
