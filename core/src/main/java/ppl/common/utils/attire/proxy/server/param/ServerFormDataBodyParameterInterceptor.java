package ppl.common.utils.attire.proxy.server.param;

import ppl.common.utils.attire.proxy.AbstractStatefulParameterInterceptor;
import ppl.common.utils.cache.Cache;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.http.Connection;
import ppl.common.utils.http.Entity;
import ppl.common.utils.http.entity.FormDataEntity;
import ppl.common.utils.http.entity.PlainTextEntity;
import ppl.common.utils.http.entity.SmartFileEntity;
import ppl.common.utils.pair.Pair;
import ppl.common.utils.string.Strings;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.List;

public class ServerFormDataBodyParameterInterceptor extends AbstractStatefulParameterInterceptor<Connection> implements BodyParameterInterceptor {

    private static final Object FORM_DATA_UNSUPPORTED = new Object();

    private transient final Cache<Method, Object> methodCache = new ConcurrentReferenceValueCache<>();

    @Override
    protected Connection handleImpl(Method method, Object[] parameters, Connection collector) {
        Object object = methodCache.getIfPresent(method);
        if (object == null) {
            Class<?> clazz = method.getDeclaringClass();
            FormData formDataAnnotation = method.getAnnotation(FormData.class);
            if (formDataAnnotation == null) {
                formDataAnnotation = clazz.getAnnotation(FormData.class);
            }

            if (formDataAnnotation == null) {
                object = FORM_DATA_UNSUPPORTED;
            } else {
                FormDataPojo formDataPojo = new FormDataPojo(formDataAnnotation);
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
                object = Pair.create(formDataPojo, pojo);
            }
            methodCache.putIfAbsent(method, object);
        }

        if (FORM_DATA_UNSUPPORTED != object) {
            FormDataEntity entity = null;
            @SuppressWarnings("unchecked")
            Pair<FormDataPojo, ParamPojo[]> pair = (Pair<FormDataPojo, ParamPojo[]>) object;
            for (int i = 0; i < pair.getSecond().length; i++) {
                if (pair.getSecond()[i] != null) {
                    if (entity == null) {
                        entity = new FormDataEntity();
                        entity.addField("_charset_", new PlainTextEntity(
                                StandardCharsets.ISO_8859_1,
                                pair.getFirst().charset().toString()));
                    }

                    if (parameters[i] instanceof File) {
                        addFile(entity, (File) parameters[i], pair.getSecond()[i]);
                    } else if (parameters[i] instanceof Entity) {
                        addEntity(entity, (Entity) parameters[i], pair.getSecond()[i]);
                    } else if (parameters[i] instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Object> list = (List<Object>) parameters[i];
                        long fileCount = list.stream().filter(o -> o instanceof File).count();
                        if (fileCount > 0 && fileCount < list.size()) {
                            throw new IllegalArgumentException("Not file list.");
                        }
                        if (fileCount > 0) {
                            @SuppressWarnings({"rawtypes", "unchecked"})
                            List<File> files = (List) list;
                            addFiles(entity, files, pair.getSecond()[i]);
                        } else {
                            addScalar(entity, list, pair.getSecond()[i], pair.getFirst().charset());
                        }
                    } else if (parameters[i] != null) {
                        addScalar(entity, parameters[i], pair.getSecond()[i], pair.getFirst().charset());
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

    private static void addFile(FormDataEntity entity, File file, ParamPojo pojo) {
        entity.addField(pojo.name(), new SmartFileEntity(file));
    }

    private static void addFiles(FormDataEntity entity, List<? extends File> files, ParamPojo pojo) {
        for (File file : files) {
            addFile(entity, file, pojo);
        }
    }

    private static void addEntity(FormDataEntity entity, Entity param, ParamPojo pojo) {
        entity.addField(pojo.name(), param);
    }

    private static void addScalar(FormDataEntity entity, Object object, ParamPojo pojo, Charset charset) {
        entity.addField(pojo.name(), new PlainTextEntity(charset, Objects.toString(object)));
    }
}
