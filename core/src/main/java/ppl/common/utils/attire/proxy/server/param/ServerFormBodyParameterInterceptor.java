//package ppl.common.utils.attire.proxy.server.param;
//
//import com.fasterxml.jackson.core.JsonEncoding;
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import ppl.common.utils.attire.proxy.AbstractStatefulParameterInterceptor;
//import ppl.common.utils.cache.Cache;
//import ppl.common.utils.cache.ConcurrentReferenceValueCache;
//import ppl.common.utils.http.Connection;
//import ppl.common.utils.http.Entity;
//import ppl.common.utils.http.header.known.ContentType;
//import ppl.common.utils.http.header.value.mediatype.MediaType;
//import ppl.common.utils.json.jackson.JsonUtils;
//
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.io.Writer;
//import java.lang.reflect.Method;
//import java.nio.charset.Charset;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//
//public class ServerFormBodyParameterInterceptor extends AbstractStatefulParameterInterceptor<Connection> implements BodyParameterInterceptor {
//
//    private static final Object FORM_UNSUPPORTED = new Object();
//
//    private transient final Cache<Method, Object> methodCache = new ConcurrentReferenceValueCache<>();
//
//    public ServerFormBodyParameterInterceptor() {
//        this(JsonUtils.defaultObjectMapper());
//    }
//
//    @Override
//    protected Connection handleImpl(Method method, Object[] parameters, Connection collector) {
//        Object object = methodCache.getIfPresent(method);
//        if (object == null) {
//            Class<?> clazz = method.getDeclaringClass();
//            Json jsonAnnotation = method.getAnnotation(Json.class);
//            if (jsonAnnotation == null) {
//                jsonAnnotation = clazz.getAnnotation(Json.class);
//            }
//
//            if (jsonAnnotation == null) {
//                object = JSON_UNSUPPORTED;
//            } else {
//                Integer bodyIndex = null;
//                for (int i = 0; i < parameters.length; i++) {
//                    if (!isUsed(parameters[i])) {
//                        if (bodyIndex == null) {
//                            bodyIndex = i;
//                        } else {
//                            throw new IllegalStateException("Too many json body.");
//                        }
//                    }
//                }
//
//                if (bodyIndex == null) {
//                    object = new JsonPojo(jsonAnnotation);
//                } else {
//                    JsonPojo[] pojo = new JsonPojo[parameters.length];
//                    pojo[bodyIndex] = new JsonPojo(jsonAnnotation);
//                    object = pojo;
//                }
//            }
//            methodCache.putIfAbsent(method, object);
//        }
//
//        if (JSON_UNSUPPORTED != object) {
//            if (object instanceof JsonPojo[]) {
//                JsonPojo[] pojo = (JsonPojo[]) object;
//                for (int i = 0; i < pojo.length; i++) {
//                    if (pojo[i] != null) {
//                        ContentType contentType = this.contentType;
//                        Charset charset = pojo[i].getCharset();
//                        if (charset != null) {
//                            MediaType mediaType = contentType.knownValue();
//                            mediaType = mediaType.setParameter("charset", charset);
//                            contentType = new ContentType(mediaType);
//                        }
//
//                        ContentType constContentType = contentType;
//                        Object constParameter = parameters[i];
//                        collector.write(new Entity() {
//                            @Override
//                            public ContentType contentType() {
//                                return constContentType;
//                            }
//
//                            @Override
//                            public void write(OutputStream os) {
//                                Charset charset = (Charset) constContentType.knownValue().getParameter("charset");
//                                try {
//                                    JsonEncoding encoding = charset == null ? JsonEncoding.UTF8 : JSON_ENCODINGS.get(charset);
//                                    if (encoding != null) {
//                                        JsonGenerator generator = mapper.createGenerator(os, encoding);
//                                        generator.writeObject(constParameter);
//                                    } else {
//                                        String string = mapper.writeValueAsString(constParameter);
//                                        Writer writer = new OutputStreamWriter(os, charset);
//                                        writer.write(string);
//                                    }
//                                } catch (Exception e) {
//                                    throw new RuntimeException("Json serializer error.", e);
//                                }
//                            }
//                        });
//                        break;
//                    }
//                }
//            }
//        }
//        return null;
//    }
//}
