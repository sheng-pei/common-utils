package ppl.common.utils.http.clients.base;

import ppl.common.utils.ArrayUtils;
import ppl.common.utils.http.Connection;
import ppl.common.utils.http.NetworkException;
import ppl.common.utils.http.header.Header;
import ppl.common.utils.http.header.HeaderFactory;
import ppl.common.utils.http.header.HeaderValue;
import ppl.common.utils.http.request.Method;
import ppl.common.utils.http.response.Response;
import ppl.common.utils.http.response.ResponseCode;
import ppl.common.utils.pair.Pair;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.stream.Collectors;

public class BaseConnection implements Connection {

    private static final InputStream EMPTY_INPUT_STREAM = new ByteArrayInputStream(ArrayUtils.zeroByte());

    private final HttpURLConnection connection;

    private BaseConnection(HttpURLConnection connection) {
        this.connection = connection;
    }

    @Override
    public void disconnect() {
        this.connection.disconnect();
    }

    @Override
    public OutputStream openOutputStream() {
        if (Method.GET.name().equalsIgnoreCase(connection.getRequestMethod())) {
            throw new IllegalStateException("Get request has no body.");
        }

        try {
            connection.setDoOutput(true);
            return connection.getOutputStream();
        } catch (IOException e) {
            throw new NetworkException("Couldn't open output stream.", e);
        }
    }

    @Override
    public Response getResponse() {
        ResponseCode code;
        try {
            code = ResponseCode.enumOf(connection.getResponseCode());
        } catch (IOException e) {
            try {
                code = ResponseCode.enumOf(connection.getResponseCode());
            } catch (IOException e1) {
                throw new NetworkException("Error when get response.", e);
            }
        }
        return new Response(code) {
            private List<Header<HeaderValue>> headers;

            @Override
            public InputStream openInputStream() {
                try {
                    InputStream is = (getCode().isError() ? connection.getErrorStream() : connection.getInputStream());
                    if (is == null) {
                        is = EMPTY_INPUT_STREAM;
                    }
                    return is;
                } catch (IOException e) {
                    throw new NetworkException("Couldn't open input stream.", e);
                }
            }

            @Override
            public List<Header<HeaderValue>> getHeaders() {
                if (headers == null) {
                    headers = connection.getHeaderFields().entrySet().stream()
                            .filter(e -> e.getKey() != null)
                            .flatMap(e -> e.getValue().stream()
                                    .map(v -> Pair.create(e.getKey(), v)))
                            .map(p -> HeaderFactory.def().create(p.getFirst(), p.getSecond()))
                            .collect(Collectors.toList());
                }
                return headers;
            }
        };
    }

    public static Connection create(HttpURLConnection connection) {
        return new BaseConnection(connection);
    }
}
