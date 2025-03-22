package ppl.common.utils.attire.proxy.server.initializer;

import ppl.common.utils.attire.proxy.*;
import ppl.common.utils.attire.proxy.server.*;
import ppl.common.utils.attire.proxy.server.param.ServerVariableStatefulParameterInterceptor;
import ppl.common.utils.attire.proxy.server.param.VariableParameterInterceptor;
import ppl.common.utils.cache.Cache;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.http.header.HeaderFactory;
import ppl.common.utils.http.request.Request;
import ppl.common.utils.string.Strings;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class ServerCallInitializer implements CallInitializer<Request.Builder> {

    private final Map<ServerIdentity, HttpServer> servers = new HashMap<>();
    private final Cache<Class<?>, ClassAnnotations> classCache = new ConcurrentReferenceValueCache<>();
    private final Cache<Method, MethodAnnotations> methodCache = new ConcurrentReferenceValueCache<>();

    private VariableParameterInterceptor variablesParameterInterceptor;

    public ServerCallInitializer() {
        this.variablesParameterInterceptor = new ServerVariableStatefulParameterInterceptor();
    }

    public void setVariablesParameterInterceptor(VariableParameterInterceptor variablesParameterInterceptor) {
        this.variablesParameterInterceptor = variablesParameterInterceptor;
    }

    public void addServer(HttpServer server) {
        Objects.requireNonNull(server);
        this.servers.put(new ServerIdentity(server), server);
    }

    @Override
    public boolean accept(Class<?> proxyClass) {
        Objects.requireNonNull(proxyClass);
        try {
            annotations(proxyClass);
            return true;
        } catch (RuntimeException t) {
            return false;
        }
    }

    @Override
    public boolean accept(Method method) {
        Objects.requireNonNull(method);
        try {
            annotations(method);
            return true;
        } catch (RuntimeException t) {
            return false;
        }
    }

    public ServerPojo serverOf(Class<?> proxyClass) {
        Objects.requireNonNull(proxyClass);
        return annotations(proxyClass).getServer();
    }

    public RequestLinePojo requestLineOf(Method method) {
        Objects.requireNonNull(method);
        return annotations(method).getRequestLine();
    }

    @Override
    public Request.Builder initialize(Class<?> proxyClass, Method method, Object[] parameters) {
        ServerPojo server = serverOf(proxyClass);
        HttpServer hServer = servers.get(new ServerIdentity(server));
        if (hServer == null) {
            throw new IllegalArgumentException(Strings.format("Server: {} is not configured."));
        }
        if (!hServer.isAvailable()) {
            throw new IllegalStateException(Strings.format("Server: {} is not available."));
        }

        RequestLinePojo requestLine = requestLineOf(method);
        HeadersPojo classHeaders = headersOf(proxyClass);
        HeadersPojo methodHeaders = headersOf(method);

        Map<String, String> variables = ParameterInterceptorApplier.handle(variablesParameterInterceptor, method, parameters, Collections.emptyMap());
        Request.Builder builder = hServer.request(requestLine.method(), requestLine.requestLine(variables));
        classHeaders.headers(variables).stream()
                .map(s -> HeaderFactory.def().create(s))
                .forEach(builder::appendHeader);
        methodHeaders.headers(variables).stream()
                .map(s -> HeaderFactory.def().create(s))
                .forEach(builder::appendHeader);
        return builder;
    }

    private HeadersPojo headersOf(Class<?> proxyClass) {
        Objects.requireNonNull(proxyClass);
        return annotations(proxyClass).getHeaders();
    }

    private ClassAnnotations annotations(Class<?> proxyClass) {
        ClassAnnotations annotations = classCache.getIfPresent(proxyClass);
        if (annotations == null) {
            annotations = new ClassAnnotations(proxyClass);
            classCache.putIfAbsent(proxyClass, annotations);
        }
        return annotations;
    }

    private HeadersPojo headersOf(Method method) {
        Objects.requireNonNull(method);
        return annotations(method).getHeaders();
    }

    private MethodAnnotations annotations(Method method) {
        MethodAnnotations annotations = methodCache.getIfPresent(method);
        if (annotations == null) {
            annotations = new MethodAnnotations(method);
            methodCache.putIfAbsent(method, annotations);
        }
        return annotations;
    }

    private static final class ClassAnnotations {
        private final ServerPojo server;
        private final HeadersPojo headers;

        private ClassAnnotations(Class<?> proxyClazz) {
            Objects.requireNonNull(proxyClazz);
            Class<?> interface1 = proxyClazz;
            if (Proxy.isProxyClass(proxyClazz)) {
                Class<?>[] interfaces = proxyClazz.getInterfaces();
                if (interfaces.length == 0 || !interfaces[0].isAnnotationPresent(Server.class)) {
                    throw new IllegalArgumentException("Not server proxy.");
                }
                interface1 = interfaces[0];
            } else if (!interface1.isInterface()) {
                throw new IllegalArgumentException("Proxy class or interface is required.");
            }

            this.server = serverOf(interface1);
            this.headers = headersOf(interface1);
        }

        private ServerPojo getServer() {
            return server;
        }

        private HeadersPojo getHeaders() {
            return headers;
        }

        private static ServerPojo serverOf(Class<?> interface1) {
            Server s = interface1.getAnnotation(Server.class);
            return new ServerPojo(s);
        }

        private static HeadersPojo headersOf(Class<?> interface1) {
            Headers headers = interface1.getAnnotation(Headers.class);
            return headers == null ? HeadersPojo.EMPTY : new HeadersPojo(headers);
        }
    }

    private static final class MethodAnnotations {
        private final RequestLinePojo requestLine;
        private final HeadersPojo headers;

        private MethodAnnotations(Method method) {
            Objects.requireNonNull(method);
            if (method.isAnnotationPresent(RequestLine.class)) {
                throw new IllegalArgumentException("Not RequestLine method.");
            }

            this.requestLine = requestLineOf(method);
            this.headers = headersOf(method);
        }

        private RequestLinePojo getRequestLine() {
            return requestLine;
        }

        private HeadersPojo getHeaders() {
            return headers;
        }

        private static RequestLinePojo requestLineOf(Method method) {
            RequestLine rl = method.getAnnotation(RequestLine.class);
            return new RequestLinePojo(rl);
        }

        private static HeadersPojo headersOf(Method method) {
            Headers headers = method.getAnnotation(Headers.class);
            return headers == null ? HeadersPojo.EMPTY : new HeadersPojo(headers);
        }
    }

    private static final class ServerIdentity {
        private final String name;
        private final String version;

        public ServerIdentity(HttpServer server) {
            this.name = server.name();
            this.version = server.version();
        }

        public ServerIdentity(ServerPojo server) {
            this.name = server.getName();
            this.version = server.getVersion();
        }

        @Override
        public boolean equals(Object object) {
            if (object == null || getClass() != object.getClass()) return false;
            ServerIdentity that = (ServerIdentity) object;
            return Objects.equals(name, that.name) && Objects.equals(version, that.version);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, version);
        }

        @Override
        public String toString() {
            return name + ":" + version;
        }
    }
}
