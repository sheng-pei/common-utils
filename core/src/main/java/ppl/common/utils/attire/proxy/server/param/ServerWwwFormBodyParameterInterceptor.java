package ppl.common.utils.attire.proxy.server.param;

import ppl.common.utils.attire.proxy.AbstractStatefulParameterInterceptor;
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
                ParamPojo[] pojo = new ParamPojo[parameters.length];

                int[] remainParameters = remainParameters(parameters);
                Parameter[] ps = method.getParameters();
                for (int i = 0; i < remainParameters.length; i++) {
                    if (remainParameters[i] < 0 && ps[i].isAnnotationPresent(Param.class)) {
                        remainParameters[i] = i;
                    }
                }
                for (int i : remainParameters) {
                    if (i >= 0) {
                        if (ps[i].isAnnotationPresent(Param.class)) {
                            pojo[i] = new ParamPojo(ps[i].getAnnotation(Param.class));
                        } else {
                            pojo[i] = ParamPojo.DEFAULT_PARAM;
                        }

                        ParamPojo p = pojo[i].changeNameIfAbsent(ps[i].getName());
                        if (p != null) {
                            if (!ps[i].isNamePresent()) {
                                throw new IllegalArgumentException(Strings.format(
                                        "Name for param argument of position [{}] not specified, " +
                                                "and parameter name information not available via reflection. " +
                                                "Ensure that the compiler uses the '-parameters' flag.", i));
                            }
                            pojo[i] = p;
                        }
                    }
                }
                object = pojo;
            }
            methodCache.putIfAbsent(method, object);
        }

        if (WWW_FORM_UNSUPPORTED != object) {
            WwwFormEntity entity = null;
            ParamPojo[] pojo = (ParamPojo[]) object;
            for (int i = 0; i < pojo.length; i++) {
                if (pojo[i] != null) {
                    if (entity == null) {
                        entity = new WwwFormEntity();
                    }

                    if (parameters[i] != null) {
                        if (Types.isWrapper(parameters[i].getClass())) {
                            entity.addField(pojo[i].name(), Objects.toString(parameters[i]));
                        } else if (Types.isString(parameters[i])) {
                            entity.addField(pojo[i].name(), (String) parameters[i]);
                        } else {
                            throw new IllegalArgumentException(Strings.format(
                                    "Non base type is not supported by '{}'", Mime.X_WWW_FORM_URLENCODED));
                        }
                    }
                }
            }

            if (entity != null) {
                collector.write(entity);
                return collector;
            }
        }
        return null;
    }
}
