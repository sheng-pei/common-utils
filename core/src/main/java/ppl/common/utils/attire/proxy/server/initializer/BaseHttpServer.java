package ppl.common.utils.attire.proxy.server.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.http.request.Method;
import ppl.common.utils.http.request.Request;
import ppl.common.utils.http.url.URL;
import ppl.common.utils.net.HierURI;
import ppl.common.utils.string.Strings;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;
import java.util.regex.Pattern;

public class BaseHttpServer implements HttpServer {

    private static final Logger log = LoggerFactory.getLogger(BaseHttpServer.class);

    private static final long PERIOD = 30 * 60 * 1000;
    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9_-]*");

    private final String name;
    private final String version;
    private final String base;
    private volatile boolean available;

    private transient Timer healthChecker;

    public BaseHttpServer(String name, String version, String base) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(version);
        Objects.requireNonNull(base);
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid server name.");
        }

        this.name = name;
        this.version = version;
        this.base = URL.create(base)
                .truncateToPath()
                .toString();
        this.available = true;
    }

    @Override
    public void enableHealthCheck(Function<HttpServer, Boolean> checker) {
        Objects.requireNonNull(checker);
        Timer t = new Timer(true);
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    BaseHttpServer.this.available = checker.apply(BaseHttpServer.this);
                } catch (Throwable t) {
                    log.warn("Health check error.", t);
                }
            }
        }, PERIOD, PERIOD);
        this.healthChecker = t;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public String base() {
        return base;
    }

    @Override
    public Request.Builder request(Method method, String uri) {
        return Request.builder(method, url(uri));
    }

    @Override
    public Request.Builder get(String uri) {
        return Request.builder(Method.GET, url(uri));
    }

    @Override
    public Request.Builder post(String uri) {
        return Request.builder(Method.POST, url(uri));
    }

    @Override
    public Request.Builder put(String uri) {
        return Request.builder(Method.PUT, url(uri));
    }

    @Override
    public Request.Builder delete(String uri) {
        return Request.builder(Method.DELETE, url(uri));
    }

    private URL url(String uri) {
        String url = this.base;
        if (Strings.isNotEmpty(uri)) {
            HierURI hUri = HierURI.create(uri);
            if (hUri.isAbsolute()) {
                throw new IllegalArgumentException("Absolute uri is not allowed.");
            }

            if (hUri.getAuthority() != null) {
                throw new IllegalArgumentException("Invalid uri, authority is not allowed.");
            }

            if (url.charAt(url.length() - 1) != '/') {
                url = url + "/";
            }

            int s = Strings.indexOfNot('/', uri);
            if (s >= 0) {
                url = url + uri.substring(s);
            }
        }
        return URL.create(url);
    }

    @Override
    public void close() {
        if (this.healthChecker != null) {
            this.healthChecker.cancel();
        }
    }
}
