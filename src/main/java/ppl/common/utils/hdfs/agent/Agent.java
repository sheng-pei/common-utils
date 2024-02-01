package ppl.common.utils.hdfs.agent;

import com.fasterxml.jackson.core.type.TypeReference;
import ppl.common.utils.IOUtils;
import ppl.common.utils.exception.IOStreamException;
import ppl.common.utils.filesystem.Path;
import ppl.common.utils.hdfs.HdfsException;
import ppl.common.utils.hdfs.data.BooleanBody;
import ppl.common.utils.hdfs.data.FileStatuses;
import ppl.common.utils.hdfs.data.FileStatusesBody;
import ppl.common.utils.hdfs.retrier.Retrier;
import ppl.common.utils.hdfs.retrier.RetryStage;
import ppl.common.utils.hdfs.selector.Selector;
import ppl.common.utils.http.Client;
import ppl.common.utils.http.Connection;
import ppl.common.utils.http.NetworkException;
import ppl.common.utils.http.header.internal.NoRedirect;
import ppl.common.utils.http.request.Request;
import ppl.common.utils.http.response.Response;
import ppl.common.utils.http.response.ResponseCode;
import ppl.common.utils.http.url.URL;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Agent {

    private static final int CHUNKED_LENGTH = 10240;

    private static final TypeReference<FileStatusesBody> FILE_STATUSES_TYPE_REFERENCE =
            new TypeReference<FileStatusesBody>() {
            };

    private static final TypeReference<BooleanBody> BOOLEAN_BODY_TYPE_REFERENCE =
            new TypeReference<BooleanBody>() {
            };

    private static final List<Class<? extends Throwable>> ERROR_TO_CONTINUE;

    static {
        List<Class<? extends Throwable>> errors = new ArrayList<>();
        errors.add(StandbyException.class);
        ERROR_TO_CONTINUE = Collections.unmodifiableList(errors);
    }

    private final Client client;
    private final int maxAttempts;
    private final String user;
    private final RetryStage<Path, URL> stage;

    public Agent(Client client, Selector selector, String user, int maxAttempts) {
        this.client = client;
        this.stage = selector;
        this.user = user;
        this.maxAttempts = maxAttempts;
    }

    public void copyToRemote(Path local, Path remote) {
        checkRemote(remote);
        File file = new File(local.toString());
        try (InputStream is = Files.newInputStream(file.toPath())) {
            pCopyToRemote(is, remote);
        } catch (NoSuchFileException e) {
            throw new IllegalArgumentException("File not found: " + local, e);
        } catch (IOException e) {
            throw new NetworkException(e.getMessage(), e);
        }
    }

    public void copyToRemote(InputStream is, Path remote) {
        checkRemote(remote);
        pCopyToRemote(is, remote);
    }

    private void pCopyToRemote(InputStream is, Path remote) {
        try {
            tryCopyToRemote(remote);
        } catch (TemporaryRedirectException e) {
            new VoidRetrierImpl(
                    u -> Request.put(URL.create(e.getLocation()))
                            .chunkedLength(CHUNKED_LENGTH)
                            .build(),
                    conn -> ResponseProcessor.writeToRequestBody(is, conn),
                    r -> {
                        if (r.getCode() != ResponseCode.CREATED) {
                            throw new HdfsException("Unknown response code: " + r.getCode());
                        }
                    }).apply(remote);
        } catch (NeedAppendException e) {
            new VoidRetrierImpl(
                    u -> Request.post(createOperation(u, Op.APPEND))
                            .chunkedLength(CHUNKED_LENGTH)
                            .build(),
                    conn -> ResponseProcessor.writeToRequestBody(is, conn),
                    r -> {
                        if (r.getCode() != ResponseCode.OK) {
                            throw new HdfsException("Unknown response code: " + r.getCode());
                        }
                    }).apply(remote);
        }
    }

    private void tryCopyToRemote(Path remote) {
        new VoidRetrierImpl(
                u -> Request.put(createOperation(u, Op.CREATE))
                        .chunkedLength(CHUNKED_LENGTH)
                        .appendHeader(new NoRedirect(true))
                        .build(),
                ResponseProcessor::firstStep).apply(remote);
    }

    public void copyToLocal(Path remote, Path local) {
        checkRemote(remote);
        try (InputStream is = new RetrierImpl<>(
                u -> Request.get(createOperation(u, Op.OPEN)).build(),
                Response::openInputStream).apply(remote);
             OutputStream os = Files.newOutputStream(Paths.get(local.toString()))) {
            IOUtils.copy(is, os);
        } catch (IOException | IOStreamException e) {
            throw new NetworkException("IO error.", e);
        }
    }

    public FileStatuses listDir(Path remote) {
        checkRemote(remote);
        return new RetrierImpl<>(u -> Request.get(createOperation(u, Op.LISTSTATUS)).build(),
                r -> ResponseProcessor
                        .processJson(r, FILE_STATUSES_TYPE_REFERENCE)
                        .getFileStatuses()).apply(remote);
    }

    public boolean mkdirs(Path remote) {
        checkRemote(remote);
        return new RetrierImpl<>(u -> Request.put(createOperation(u, Op.MKDIRS)).build(),
                r -> ResponseProcessor
                        .processJson(r, BOOLEAN_BODY_TYPE_REFERENCE)
                        .isBool()).apply(remote);
    }

    public boolean delete(Path remote) {
        checkRemote(remote);
        return new RetrierImpl<>(u -> Request.delete(createOperation(u, Op.DELETE)).build(),
                r -> ResponseProcessor
                        .processJson(r, BOOLEAN_BODY_TYPE_REFERENCE)
                        .isBool()).apply(remote);
    }

    private void checkRemote(Path remote) {
        if (!remote.isAbsolute()) {
            throw new IllegalArgumentException("Invalid path, absolute path is required.");
        }
    }

    private URL createOperation(URL url, Op op) {
        return url.appendQuery("op", op.name())
                .appendQuery("user.name", user);
    }

    private class RetrierImpl<T> extends Retrier<Path, URL, T> {
        private final Function<URL, Request> request;
        private final Consumer<Connection> requestBody;
        private final Function<Response, T> response;
        private final Function<Response, ? extends RuntimeException> error;

        public RetrierImpl(Function<URL, Request> request, Function<Response, T> response) {
            this(request, os -> {
            }, response, ResponseProcessor::processError);
        }

        public RetrierImpl(Function<URL, Request> request, Consumer<Connection> requestBody, Function<Response, T> response) {
            this(request, requestBody, response, ResponseProcessor::processError);
        }

        public RetrierImpl(Function<URL, Request> request, Consumer<Connection> requestBody, Function<Response, T> response, Function<Response, ? extends RuntimeException> error) {
            super(maxAttempts, stage, ERROR_TO_CONTINUE);
            this.request = request;
            this.requestBody = requestBody;
            this.response = response;
            this.error = error;
        }

        @Override
        protected T execute(URL url) throws InterruptedException {
            Request request = this.request.apply(url);
            Connection conn = client.connect(request);
            requestBody.accept(conn);
            Response response = conn.getResponse();
            if (response.getCode().isError()) {
                throw error.apply(response);
            } else {
                return this.response.apply(response);
            }
        }
    }

    private class VoidRetrierImpl extends RetrierImpl<Void> {
        public VoidRetrierImpl(Function<URL, Request> request, Consumer<Response> response) {
            this(request, os -> {
            }, response, ResponseProcessor::processError);
        }

        public VoidRetrierImpl(Function<URL, Request> request, Consumer<Connection> requestBody, Consumer<Response> response) {
            this(request, requestBody, response, ResponseProcessor::processError);
        }

        public VoidRetrierImpl(Function<URL, Request> request, Consumer<Connection> requestBody, Consumer<Response> response, Function<Response, ? extends RuntimeException> error) {
            super(request, requestBody, r -> {
                response.accept(r);
                return null;
            }, error);
        }
    }

}
