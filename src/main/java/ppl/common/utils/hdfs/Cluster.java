package ppl.common.utils.hdfs;

import ppl.common.utils.hdfs.selector.Selector;
import ppl.common.utils.hdfs.selector.Selectors;
import ppl.common.utils.http.Client;
import ppl.common.utils.http.Clients;
import ppl.common.utils.http.url.URL;
import ppl.common.utils.string.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class Cluster {

    private static final String WEB_HDFS_API = "/webhdfs/v1/";
    private final Client client;
    private final String user;
    private final Supplier<Selector> supplier;

    public Cluster(String user, String... nameNodes) {
        this(user, Selectors.nextSelector(WEB_HDFS_API, toURL(nameNodes)));
    }

    public Cluster(String user, String prefix, String... nameNodes) {
        this(user, Selectors.nextSelector(prefix, toURL(nameNodes)));
    }

    public Cluster(String user, Supplier<Selector> supplier) {
        Objects.requireNonNull(supplier, "Supplier is required.");
        this.client = Clients.create();
        this.user = user;
        this.supplier = supplier;
    }

    private static URL[] toURL(String... nameNodes) {
        List<URL> urls = new ArrayList<>();
        for (String nameNode : nameNodes) {
            URL url = URL.create(nameNode);
            if (Strings.isNotEmpty(url.path()) ||
                    Strings.isNotEmpty(url.query()) ||
                    Strings.isNotEmpty(url.fragment())) {
                throw new IllegalArgumentException("No path, query or fragment is needed.");
            }
            urls.add(url);
        }
        return urls.toArray(new URL[0]);
    }

    public Client getClient() {
        return client;
    }

    public Selector getSelector() {
        return supplier.get();
    }

    public String getUser() {
        return user;
    }
}
