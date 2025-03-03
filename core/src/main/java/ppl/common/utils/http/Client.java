package ppl.common.utils.http;

import ppl.common.utils.http.property.Properties;

public interface Client extends Connector {
    String CHARSET = "charset";
    String PROXY = "proxy";
    String SSL_CONTEXT = "sslContext";
    String USER_AGENT = "userAgent";

    Object getProperty(String name);

    interface Creator {
        Client create(Properties properties);
    }

    static Properties.Builder propertiesBuilder() {
        return Properties.newBuilder();
    }
}
