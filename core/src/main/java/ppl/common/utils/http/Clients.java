package ppl.common.utils.http;

import ppl.common.utils.http.clients.base.BaseClient;
import ppl.common.utils.http.property.Properties;

public final class Clients {
    private Clients() {
    }

    public static Client create() {
        return new BaseClient();
    }

    public static Client create(Properties properties) {
        return new BaseClient(properties);
    }

}
