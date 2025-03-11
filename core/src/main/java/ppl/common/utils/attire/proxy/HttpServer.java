package ppl.common.utils.attire.proxy;

import ppl.common.utils.http.request.Request;
import ppl.common.utils.http.url.URL;

public interface HttpServer {
    void enableHealthCheck(String healthUri);
    boolean isAvailable();
    String base();
    Request.Builder get(String uri);
    Request.Builder post(String uri);
    Request.Builder put(String uri);
    Request.Builder delete(String uri);
}
