package ppl.common.utils.attire.proxy.server.initializer;

import ppl.common.utils.http.request.Method;
import ppl.common.utils.http.request.Request;

import java.util.function.Function;

public interface HttpServer extends AutoCloseable {
    void enableHealthCheck(Function<HttpServer, Boolean> checker);
    boolean isAvailable();
    String name();
    String version();
    String base();
    Request.Builder request(Method method, String uri);
    Request.Builder get(String uri);
    Request.Builder post(String uri);
    Request.Builder put(String uri);
    Request.Builder delete(String uri);
}
