package ppl.common.utils.http;

import ppl.common.utils.http.property.Properties;
import ppl.common.utils.http.request.Request;

import javax.net.ssl.SSLContext;
import java.net.Proxy;
import java.nio.charset.Charset;

public abstract class AbstractClient implements Client {

    private transient Proxy proxy;
    private transient SSLContext sslContext;
    private transient Charset charset;
    private transient String userAgent;
    private final Properties properties;

    protected AbstractClient(Properties properties) {
        //TODO, If cookie is used, cookie container must be created here.
        if (!properties.hasProperty(Client.CHARSET)) {
            properties = properties.copy()
                    .putProperty(Client.CHARSET, Charset.defaultCharset())
                    .build();
        }
        this.properties = properties;
    }

    public Proxy getProxy() {
        return this.proxy = (Proxy) ensure(Client.PROXY, this.proxy);
    }

    public Charset getCharset() {
        return this.charset = (Charset) ensure(Client.CHARSET, this.charset);
    }

    public String getUserAgent() {
        return this.userAgent = (String) ensure(Client.USER_AGENT, this.userAgent);
    }

    public SSLContext getSSLContext() {
        return this.sslContext = (SSLContext) ensure(Client.SSL_CONTEXT, this.sslContext);
    }

    private Object ensure(String name, Object origin) {
        if (origin != null) {
            return origin;
        }
        return properties.getProperty(name);
    }

    @Override
    public Object getProperty(String name) {
        switch (name) {
            case Client.USER_AGENT:
                return getUserAgent();
            case Client.CHARSET:
                return getCharset();
            case Client.SSL_CONTEXT:
                return getSSLContext();
            case Client.PROXY:
                return getProxy();
        }
        return properties.getProperty(name);
    }

    @Override
    public Connection connect(Request request) {
        Request.Builder builder = request.copy();
        properties.getRequestInitializer()
                .forEach(i -> i.init(builder));
        return connectImpl(builder.build());
    }

    protected abstract Connection connectImpl(Request request);
}
