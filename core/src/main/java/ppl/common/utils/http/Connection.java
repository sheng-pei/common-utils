package ppl.common.utils.http;

import ppl.common.utils.http.response.Response;

import java.io.OutputStream;

/**
 * Convention is same as URLConnection and not HttpClient like interface.
 */
public interface Connection {
    void disconnect();
    void write(Entity entity);
    OutputStream openOutputStream();
    Response getResponse();
}
