package ppl.common.utils.http;

import ppl.common.utils.http.response.Response;

import java.io.OutputStream;

/**
 * TODO, Convention is same as URLConnection and not support HttpClient.
 * Because when we open output streams to write request body, connection is already opened.
 * But when executing request in HttpClient, a complete request (contains request body)
 * is already prepared. We couldn't open connection before we has prepared request body to write.
 */
public interface Connection {
    void disconnect();
    OutputStream openOutputStream();
    Response getResponse();
}
