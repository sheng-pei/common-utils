package ppl.common.utils.hdfs.agent;

import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.IOUtils;
import ppl.common.utils.StopWatch;
import ppl.common.utils.exception.IOStreamException;
import ppl.common.utils.hdfs.data.ErrorBody;
import ppl.common.utils.hdfs.data.RemoteError;
import ppl.common.utils.http.NetworkException;
import ppl.common.utils.http.header.known.ContentLength;
import ppl.common.utils.http.header.known.ContentType;
import ppl.common.utils.http.header.known.Location;
import ppl.common.utils.http.header.value.mediatype.MediaType;
import ppl.common.utils.http.header.value.mediatype.Mime;
import ppl.common.utils.http.response.Response;
import ppl.common.utils.http.response.ResponseCode;
import ppl.common.utils.json.JsonUtils;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class ResponseProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ResponseProcessor.class);
    private static final TypeReference<ErrorBody> ERROR_BODY_TYPE_REFERENCE = new TypeReference<ErrorBody>() {};

    public static RuntimeException processError(Response response) {
        ContentType contentType = response.getHeader(ContentType.class);
        MediaType value = contentType.knownValue();
        if (value == null || !Mime.JSON.equals(value.getTarget())) {
            return new UnsupportedMimeException("Only json is supported. " +
                    "Please check content type: " + contentType.toCanonicalString());
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.openInputStream()))) {
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            String body = builder.toString();
            ErrorBody errorBody = JsonUtils.read(body, ERROR_BODY_TYPE_REFERENCE);
            RemoteError remote = errorBody.getRemoteError();
            if (remote != null && "StandbyException".equals(remote.getException())) {
                return new StandbyException(remote.getMessage());
            } else {
                return new RuntimeException("Unknown error: " + body);
            }
        } catch (IOException e) {
            return new NetworkException("Error to get response body.", e);
        }
    }

    public static <B> B processJson(Response response, TypeReference<B> reference) {
        ContentLength contentLength = response.getHeader(ContentLength.class);
        if (contentLength != null && contentLength.knownValue().getValue() == 0) {
            throw new RuntimeException("Empty response body.");
        }

        ContentType contentType = response.getHeader(ContentType.class);
        if (contentType == null) {
            throw new RuntimeException("No content type is given. Has no response body?");
        }

        MediaType value = contentType.knownValue();
        if (value == null || !Mime.JSON.equals(value.getTarget())) {
            throw new UnsupportedMimeException("Only json is supported. " +
                    "Please check content type: " + contentType.toCanonicalString());
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.openInputStream()))) {
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            String body = builder.toString();
            return JsonUtils.read(body, reference);
        } catch (IOException e) {
            throw new NetworkException("Error to get response body.", e);
        }
    }

    public static void firstStep(Response response) {
        if (response.getCode() == ResponseCode.TEMPORARY_REDIRECT) {
            Location location = response.getHeader(Location.class);
            if (location == null) {
                throw new RuntimeException("Error temporary redirect. No location is found.");
            } else {
                throw new TemporaryRedirectException(location.knownValue().toCanonicalString());
            }
        } else if (response.getCode() == ResponseCode.CREATED) {
            throw new NeedAppendException();
        } else {
            throw new RuntimeException("Unknown response code: " + response.getCode());
        }
    }

    public static void writeToRequestBody(InputStream is, OutputStream os) {
        StopWatch watch = StopWatch.createStopWatch();
        try {
            IOUtils.copy(is, os, 10240);
        } catch (IOStreamException e1) {
            throw new RuntimeException("Network error.", e1);
        } finally {
            logger.warn("Transfer time: " + watch.elapse(TimeUnit.SECONDS));
        }
    }
}
