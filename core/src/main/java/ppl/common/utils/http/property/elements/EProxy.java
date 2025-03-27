package ppl.common.utils.http.property.elements;

import ppl.common.utils.http.Client;
import ppl.common.utils.http.Name;
import ppl.common.utils.http.property.Element;
import ppl.common.utils.http.request.Request;
import ppl.common.utils.http.request.RequestInitializer;

import java.net.Proxy;

@Name(Client.PROXY)
public class EProxy implements Element<Proxy>, RequestInitializer {

    private final Proxy proxy;

    public EProxy(Object proxy) {
        if (!(proxy instanceof Proxy)) {
            throw new IllegalArgumentException(String.format(
                    "Unsupported value type: '%s' for '%s'.",
                    proxy.getClass(), EProxy.class.getCanonicalName()));
        }
        this.proxy = (Proxy) proxy;
    }

    @Override
    public Proxy get() {
        return proxy;
    }

    @Override
    public void init(Request.Builder request) {
        request.use(proxy);
    }

}
