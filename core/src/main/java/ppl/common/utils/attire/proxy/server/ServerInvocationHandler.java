package ppl.common.utils.attire.proxy.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.Arrays;
import ppl.common.utils.attire.proxy.*;
import ppl.common.utils.attire.proxy.exceptions.*;
import ppl.common.utils.attire.proxy.server.initializer.ServerCallInitializer;
import ppl.common.utils.attire.proxy.server.param.BodyParameterInterceptor;
import ppl.common.utils.attire.proxy.server.param.RequestParameterInterceptor;
import ppl.common.utils.cache.Cache;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.http.Connection;
import ppl.common.utils.http.Connector;
import ppl.common.utils.http.NetworkException;
import ppl.common.utils.http.header.known.ContentType;
import ppl.common.utils.http.request.Request;
import ppl.common.utils.http.response.Response;
import ppl.common.utils.reflect.InvocationHandlerUtils;
import ppl.common.utils.string.Strings;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ServerInvocationHandler implements PrerequisiteInvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(ServerInvocationHandler.class);

    private final AtomicReference<String> defaultConnector;
    private final Map<String, Connector> connectors;

    private final ServerCallInitializer callInitializer;
    private final List<RequestParameterInterceptor> requestParameterInterceptors;
    private final List<BodyParameterInterceptor> bodyParameterInterceptors;
    private final List<ReturnInterceptor<Response>> returnInterceptors;

    private final Cache<Method, MethodHandle> defaultMethodCache = new ConcurrentReferenceValueCache<>();

    public ServerInvocationHandler(Builder builder) {
        if (builder.connectors.isEmpty()) {
            throw new IllegalArgumentException("Connector is required.");
        }

        this.defaultConnector = new AtomicReference<>(builder.defaultConnector);
        this.connectors = new ConcurrentHashMap<>(builder.connectors);
        this.callInitializer = new ServerCallInitializer();
        this.requestParameterInterceptors = Collections.unmodifiableList(new ArrayList<>(
                builder.parameterInterceptors));
        this.bodyParameterInterceptors = Collections.unmodifiableList(new ArrayList<>(
                builder.requestBodyInterceptors));
        this.returnInterceptors = Collections.unmodifiableList(new ArrayList<>(
                builder.returnInterceptors));
    }

    public void putConnector(String name, Connector connector) {
        Objects.requireNonNull(connector);
        if (Strings.isBlank(name)) {
            throw new IllegalArgumentException("Blank name of connector is not allowed.");
        }

        name = name.trim();
        this.connectors.put(name, connector);
    }

    public void setDefault(String name) {
        if (Strings.isBlank(name)) {
            throw new IllegalArgumentException("Blank name of connector is not allowed.");
        }

        name = name.trim();
        defaultConnector.lazySet(name);
    }

    public void unsetDefault() {
        defaultConnector.lazySet(null);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        assert assertOneInterface(proxy.getClass().getInterfaces()) : "More than one interface is proxied.";
        assert assertInterfaceAccepted(proxy.getClass().getInterfaces()[0]) : Strings.format(
                "Interface '{}' couldn't be proxy by ServerInvocationHandler. " +
                        "Please ensure the interface is annotated by @Server.", proxy.getClass().getInterfaces()[0]);

        ServerPojo serverPojo = callInitializer.serverOf(proxy.getClass());

        String name = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (name.equals("equals") && parameterTypes.length == 1 && parameterTypes[0].equals(Object.class)) {
            return InvocationHandlerUtils.equals(proxy, args[0],
                    (p, parameter) -> {
                        try {
                            return serverPojo.equals(callInitializer.serverOf(parameter.getClass()));
                        } catch (RuntimeException e) {
                            return false;
                        }
                    });
        }

        if (name.equals("hashCode") && parameterTypes.length == 0) {
            InvocationHandlerUtils.hashCode(proxy, p -> serverPojo.hashCode());
        }

        if (name.equals("toString") && parameterTypes.length == 0) {
            InvocationHandlerUtils.toString(proxy, p -> serverPojo.toString());
        }

        assert assertMethodIsNotMemberOfPrimaryProxiedInterface(proxy.getClass().getInterfaces()[0], method) :
                "Method {} is not member of primary proxied interface";

        boolean methodAccepted = callInitializer.accept(method);
        //TODO,     default方法，没有RequestLine注解，直接调用
        if (method.isDefault() && !methodAccepted) {
            MethodHandle handle = defaultMethodCache.get(method, () -> {
                Class<?> interface1 = proxy.getClass().getInterfaces()[0];
                Method superMethod = interface1.getMethod(method.getName(), method.getParameterTypes());
                MethodHandle temp = MethodHandles.lookup().unreflectSpecial(superMethod, interface1);
                temp.bindTo(proxy);
                return temp;
            });
            return handle.invoke(args);
        } else if (!method.isDefault() && !methodAccepted) {
            throw new UnsupportedOperationException(Strings.format(
                    "Abstract method: '{}', not implemented by server '{}'", method.toString(), serverPojo));
        } else {

            Object[] statefulParameters = Arrays.copyOf(args, args.length);

            RequestLinePojo requestLinePojo = callInitializer.requestLineOf(method);
            Request.Builder req;
            try {
                req = callInitializer.initialize(proxy.getClass(), method, statefulParameters);
            } catch (RuntimeException e) {
                throw new RequestInitializationException(Strings.format(
                        "Request initialization error for api '{}' of server '{}'.",
                        requestLinePojo, serverPojo), e);
            }

            try {
                for (ParameterInterceptor<Request.Builder> parameterInterceptor : requestParameterInterceptors) {
                    req = ParameterInterceptorApplier.handle(parameterInterceptor, method, statefulParameters, req);
                }
            } catch (RuntimeException e) {
                throw new RequestParameterException(Strings.format(
                        "Request parameter error for api '{}' of server '{}'.",
                        requestLinePojo, serverPojo), e);
            }

            String defaultName = defaultConnector.get();
            Connector connector = connectors.getOrDefault(serverPojo.getName(), defaultName == null ? null : connectors.get(defaultName));
            if (connector == null) {
                throw new IllegalStateException(Strings.format("No connector found for '{}'", serverPojo));
            }

            try {
                Connection conn = connector.connect(req.build());

                //TODO, body
                try {
                    for (ParameterInterceptor<Connection> interceptor : bodyParameterInterceptors) {
                        if (interceptor.handle(method, statefulParameters, conn) != null) {
                            break;
                        }
                    }
                } catch (NetworkException e) {
                    throw e;
                } catch (RuntimeException e) {
                    throw new RequestBodyException(Strings.format(
                            "Invalid request body of api '{}' of server '{}'.",
                            requestLinePojo, serverPojo), e);
                }

                Response response = conn.getResponse();

                //TODO, 响应数据处理异常处理
                try {
                    for (ReturnInterceptor<Response> returnInterceptor : returnInterceptors) {
                        if (returnInterceptor.accept(response)) {
                            return returnInterceptor.handle(response, method);
                        }
                    }
                } catch (NetworkException e) {
                    throw e;
                } catch (RuntimeException e) {
                    throw new ResponseException(Strings.format(
                            "Response content parse error for api '{}' of server '{}'.",
                            requestLinePojo, serverPojo), e);
                }

                ContentType contentType = response.getHeader(ContentType.class);
                if (contentType == null) {
                    log.warn("No content type response from server '{}'.", serverPojo);
                    return null;
                }

                throw new IllegalStateException(Strings.format(
                        "Unknown response from server '{}': Content-Type is '{}'",
                        serverPojo, contentType.toCanonicalString()));
            } catch (NetworkException e) {
                throw new CommunicationException(Strings.format(
                        "Communication error when call api '{}' of server '{}'.",
                        requestLinePojo, serverPojo), e);
            }
        }
    }

    public static boolean assertMethodIsNotMemberOfPrimaryProxiedInterface(Class<?> interface1, Method method) {
        return method.getDeclaringClass().isAssignableFrom(interface1);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void assertInterfaces(Class<?>[] interfaces) {
        Objects.requireNonNull(interfaces);
        if (!assertOneInterface(interfaces)) {
            throw new IllegalArgumentException("Only one interface is supported.");
        }
        if (!assertInterfaceAccepted(interfaces[0])) {
            throw new IllegalArgumentException(Strings.format(
                    "Interface '{}' couldn't be proxy by ServerInvocationHandler. " +
                            "Please ensure the interface is annotated by @Server.", interfaces[0]));
        }
    }

    private boolean assertOneInterface(Class<?>[] interfaces) {
        return interfaces.length == 1;
    }

    private boolean assertInterfaceAccepted(Class<?> interface1) {
        return callInitializer.accept(interface1);
    }

    public static final class Builder {
        private String defaultConnector;
        private final Map<String, Connector> connectors = new HashMap<>();

        private final List<RequestParameterInterceptor> parameterInterceptors = new ArrayList<>();
        private final List<BodyParameterInterceptor> requestBodyInterceptors = new ArrayList<>();
        private final List<ReturnInterceptor<Response>> returnInterceptors = new ArrayList<>();

        public Builder putConnector(String name, Connector connector) {
            Objects.requireNonNull(connector);
            if (Strings.isBlank(name)) {
                throw new IllegalArgumentException("Blank name of connector is not allowed.");
            }

            name = name.trim();
            this.connectors.put(name, connector);
            return this;
        }

        public Builder setDefault(String name) {
            if (Strings.isBlank(name)) {
                throw new IllegalArgumentException("Blank name of connector is not allowed.");
            }

            name = name.trim();
            if (defaultConnector != null && !Objects.equals(defaultConnector, name)) {
                log.info("Ignore, there is only one connector needed.");
            } else {
                this.defaultConnector = name;
            }
            return this;
        }

        public Builder addRequestParameterInterceptor(RequestParameterInterceptor requestParameterInterceptor) {
            this.parameterInterceptors.add(requestParameterInterceptor);
            return this;
        }

        public Builder addRequestBodyInterceptor(BodyParameterInterceptor requestBodyInterceptor) {
            this.requestBodyInterceptors.add(requestBodyInterceptor);
            return this;
        }

        public Builder addResponseInterceptor(ReturnInterceptor<Response> returnInterceptor) {
            this.returnInterceptors.add(returnInterceptor);
            return this;
        }

        public ServerInvocationHandler build() {
            return new ServerInvocationHandler(this);
        }
    }

}
