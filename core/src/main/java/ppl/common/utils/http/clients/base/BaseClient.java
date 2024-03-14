package ppl.common.utils.http.clients.base;

import ppl.common.utils.exception.UnreachableCodeException;
import ppl.common.utils.http.AbstractClient;
import ppl.common.utils.http.Connection;
import ppl.common.utils.http.NetworkException;
import ppl.common.utils.http.header.Header;
import ppl.common.utils.http.header.HeaderValue;
import ppl.common.utils.http.header.UnknownHeaderValue;
import ppl.common.utils.http.header.internal.NoRedirect;
import ppl.common.utils.http.header.known.ContentLength;
import ppl.common.utils.http.header.known.TransferEncoding;
import ppl.common.utils.http.header.value.LongValue;
import ppl.common.utils.http.header.value.transfercoding.Coding;
import ppl.common.utils.http.header.value.transfercoding.CodingKind;
import ppl.common.utils.http.property.Properties;
import ppl.common.utils.http.request.Method;
import ppl.common.utils.http.request.Request;
import ppl.common.utils.http.url.URL;
import ppl.common.utils.string.Strings;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.util.EnumMap;
import java.util.List;

public class BaseClient extends AbstractClient {

    private static final EnumMap<Method, Boolean> DO_OUTPUT;

    static {
        EnumMap<Method, Boolean> doOutput = new EnumMap<>(Method.class);
        doOutput.put(Method.GET, false);
        DO_OUTPUT = doOutput;
    }

    public BaseClient() {
        this(Properties.newBuilder().build());
    }

    public BaseClient(Properties properties) {
        super(properties);
    }

    @Override
    protected Connection connectImpl(Request request) {
        HttpURLConnection connection = buildConnection(request);
        return BaseConnection.create(connection);
    }

    private HttpURLConnection buildConnection(Request request) {
        try {
            HttpURLConnection conn = createUrlConnection(request.getUrl(), request.getProxy());
            conn.setRequestMethod(request.getMethod().name());
            conn.setDoOutput(isDoOutput(request));
            initSSL(conn, request.getSslContext());
            processContentLength(conn, getContentLength(request));
            processNoRedirect(conn, getNoRedirect(request));
            if (isChunked(request)) {
                processChunked(conn, request.getChunkedLength());
            }
            request = request.copy()
                    .clearHeader(ContentLength.class)
                    .clearHeader(TransferEncoding.class)
                    .build();
            writeHeaders(conn, request.getHeaders());
            return conn;
        } catch (ProtocolException e) {
            throw new UnreachableCodeException(e);
        } catch (IOException e) {
            throw new NetworkException("Couldn't build URLConnection.", e);
        }
    }

    private HttpURLConnection createUrlConnection(URL url, Proxy proxy) throws IOException {
        if (proxy == null) {
            return url.open();
        } else {
            return url.open(proxy);
        }
    }

    private boolean isDoOutput(Request request) {
        return DO_OUTPUT.getOrDefault(request.getMethod(), true);
    }

    private void initSSL(HttpURLConnection conn, SSLContext sslContext) {
        if (conn instanceof HttpsURLConnection && sslContext != null) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            httpsConn.setSSLSocketFactory(sslContext.getSocketFactory());
        }
    }

    private ContentLength getContentLength(Request request) {
        ContentLength contentLength = request.getHeader(ContentLength.class);
        Method method = request.getMethod();
        if (contentLength != null && !isDoOutput(request)) {
            throw new IllegalArgumentException(Strings.format(
                    "Fixed content length is not allowed for method '%s'.", method));
        }
        return contentLength;
    }

    private NoRedirect getNoRedirect(Request request) {
        return request.getHeader(NoRedirect.class);
    }

    private boolean isChunked(Request request) {
        boolean isChunked = request.getHeaders(TransferEncoding.class)
                .stream()
                .flatMap(te -> te.value().getValues().stream())
                .map(this::checkUnknown)
                .map(hv -> (Coding) hv)
                .map(Coding::getArguments)
                .map(this::checkChunked)
                .findAny()
                .isPresent();
        Method method = request.getMethod();
        if (isChunked && !isDoOutput(request)) {
            throw new IllegalArgumentException(String.format(
                    "Chunked transfer encoding is not allowed for method '%s'.", method));
        }
        return isChunked;
    }

    private HeaderValue checkUnknown(HeaderValue hv) {
        if (hv instanceof UnknownHeaderValue) {
            throw new IllegalArgumentException(String.format(
                    "Unknown transfer encoding: '%s'.", hv.toCanonicalString()));
        }
        return hv;
    }
    private CodingKind checkChunked(CodingKind kind) {
        if (kind != CodingKind.CHUNKED) {
            throw new IllegalArgumentException(String.format(
                    "Transfer encoding: '%s' is not supported but chunked.", kind));
        }
        return kind;
    }

    private void processNoRedirect(HttpURLConnection conn, NoRedirect noRedirect) {
        if (noRedirect != null) {
            conn.setInstanceFollowRedirects(!noRedirect.knownValue().getValue());
        }
    }

    private void processContentLength(HttpURLConnection conn, ContentLength length) {
        if (length != null) {
            conn.setFixedLengthStreamingMode(((LongValue) length.value()).getValue());
        }
    }

    private void processChunked(HttpURLConnection conn, int chunkedLength) {
        conn.setChunkedStreamingMode(chunkedLength);
    }

    private void writeHeaders(HttpURLConnection conn, List<Header<HeaderValue>> headers) {
        if (headers != null) {
            headers.stream().filter(Header::isNet).forEach(h -> conn.addRequestProperty(
                    h.name().toString(),
                    h.value().toCanonicalString()));
        }
    }
}
