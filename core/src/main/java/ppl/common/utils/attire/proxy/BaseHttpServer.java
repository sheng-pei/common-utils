package ppl.common.utils.attire.proxy;

import ppl.common.utils.http.request.Request;
import ppl.common.utils.http.url.URL;

import java.util.Objects;

public class BaseHttpServer implements HttpServer {

    private final URL base; //scheme host port path
    private volatile boolean available;

    private transient Thread healthChecker;

    public BaseHttpServer(String base) {
        Objects.requireNonNull(base);
        this.base = URL.create(base)
                .truncateToPath();
        this.available = true;
    }

    @Override
    public void enableHealthCheck(String healthUri) {
//        if (Strings.isBlank(healthUri)) {
//            this.healthUri = this.base;
//            return;
//        }
//
//        healthUri = healthUri.trim();
//        URI uri;
//        try {
//            uri = URI.create(healthUri);
//        } catch (IllegalArgumentException e) {
//            throw new IllegalArgumentException("Invalid health uri.", e.getCause());
//        }
//
//        if (uri.isAbsolute()) {
//            throw new IllegalArgumentException("Absolute health uri is not allowed.");
//        }
//
//        if (Strings.isNotEmpty(uri.getAuthority())) {
//            throw new IllegalArgumentException("Invalid health uri, authority is not allowed.");
//        }
//
//        int s = Strings.indexOfNot('/', healthUri);
//        if (s < 0) {
//            this.healthUrl = this.base;
//            return;
//        }
//
//        String bString = this.base.toString();
//        int e = Strings.lastIndexOfNot('/', bString);
//        this.healthUrl = URL.create(bString.substring(0, e + 1) + "/" + healthUri.substring(s));
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    private void checkHealth() {

    }

    @Override
    public String base() {
        return base.toString();
    }

    @Override
    public Request.Builder get(String uri) {
        return null;
    }

    @Override
    public Request.Builder post(String uri) {
        return null;
    }

    @Override
    public Request.Builder put(String uri) {
        return null;
    }

    @Override
    public Request.Builder delete(String uri) {
        return null;
    }

}
