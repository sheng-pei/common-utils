package ppl.common.utils.hdfs.selector;

import ppl.common.utils.filesystem.Path;
import ppl.common.utils.http.Client;
import ppl.common.utils.http.Connection;
import ppl.common.utils.http.request.Request;
import ppl.common.utils.http.response.Response;
import ppl.common.utils.http.url.URL;
import ppl.common.utils.string.Strings;

import java.util.Arrays;
import java.util.function.Supplier;

class StatusProbeSelector implements Selector {
    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private final Client client;
    private final URL[] nameNodes;
    private final URL[] healthProbes;
    private volatile int active;
    private volatile int nextActive;

    private StatusProbeSelector(Client client, String healthProbe, URL[] nameNodes) {
        String constHealthProbe = removePrefixSeparator(healthProbe);
        URL[] healthProbes = healthProbeUrls(nameNodes, constHealthProbe);

        this.nameNodes = nameNodes;
        this.active = this.nextActive = probe(healthProbes);
        this.client = client;
        this.healthProbes = healthProbes;
    }

    private URL[] healthProbeUrls(URL[] nameNodes, String healthProbe) {
        return Arrays.stream(nameNodes)
                .map(u -> URL.create(u.toString() + "/" + healthProbe))
                .toArray(URL[]::new);
    }

    private String removePrefixSeparator(String healthProbe) {
        int idx = Strings.indexOfNot(Path.C_SEPARATOR, healthProbe);
        if (idx < 0) {
            idx = healthProbe.length();
        }
        return healthProbe.substring(idx);
    }

    private int probe(URL[] healthProbes) {
        for (URL url : healthProbes) {
            Request req = Request.get(url).build();
            Connection conn = client.connect(req);
            Response res = conn.getResponse();
        }
        return -1;
    }

    private boolean isActive(URL healthProbe) {
        return false;
    }

    @Override
    public int maxAttempts(int maxAttempts) {
        return maxAttempts <=0 ? DEFAULT_MAX_ATTEMPTS : maxAttempts;
    }

    public URL init(Path path) {
//        return this.nameNodes[active].resolve(path);
        return null;
    }

    public URL next(Path path) {
//        if (!invalidate) {
//            return getUrl(bases[active], path);
//        }
//        return getUrl(bases[nextActive()], path);
        return null;
    }

    @Override
    public void finish() {}

    static class SupplierImpl implements Supplier<Selector> {
        private final StatusProbeSelector selector;

        public SupplierImpl(Client client, String healthProbe, String... nameNodes) {
            this.selector = null;
        }

        @Override
        public Selector get() {
            return selector;
        }
    }

}
