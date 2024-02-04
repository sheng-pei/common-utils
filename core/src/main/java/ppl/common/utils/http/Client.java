package ppl.common.utils.http;

import ppl.common.utils.http.property.Properties;
import ppl.common.utils.http.request.Request;

public interface Client {
    String CHARSET = "charset";
    String PROXY = "proxy";
    String SSL_CONTEXT = "sslContext";
    String USER_AGENT = "userAgent";

    Object getProperty(String name);

    Connection connect(Request request);

    interface Creator {
        Client create(Properties properties);
    }

    static Properties.Builder propertiesBuilder() {
        return Properties.newBuilder();
    }

}
