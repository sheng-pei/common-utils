package ppl.common.utils.http.property.elements;

import ppl.common.utils.http.Client;
import ppl.common.utils.http.Name;
import ppl.common.utils.http.property.Element;
import ppl.common.utils.http.request.Request;
import ppl.common.utils.http.request.RequestInitializer;

import javax.net.ssl.SSLContext;

@Name(Client.SSL_CONTEXT)
public class ESSLContext implements Element<SSLContext>, RequestInitializer {
    private final SSLContext sslContext;

    public ESSLContext(Object sslContext) {
        if (!(sslContext instanceof SSLContext)) {
            throw new IllegalArgumentException(String.format(
                    "Unsupported value type: '%s' for '%s'.",
                    sslContext.getClass(), ESSLContext.class.getCanonicalName()));
        }

        this.sslContext = (SSLContext) sslContext;
    }

    @Override
    public SSLContext get() {
        return sslContext;
    }

    @Override
    public void init(Request.Builder request) {
        request.use(sslContext);
    }

}
