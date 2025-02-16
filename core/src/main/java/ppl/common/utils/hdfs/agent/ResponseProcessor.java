package ppl.common.utils.hdfs.agent;

import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.IOs;
import ppl.common.utils.watch.StopWatch;
import ppl.common.utils.exception.IOStreamException;
import ppl.common.utils.hdfs.HdfsException;
import ppl.common.utils.hdfs.data.ErrorBody;
import ppl.common.utils.hdfs.data.RemoteError;
import ppl.common.utils.http.Connection;
import ppl.common.utils.http.NetworkException;
import ppl.common.utils.http.header.known.ContentLength;
import ppl.common.utils.http.header.known.ContentType;
import ppl.common.utils.http.header.known.Location;
import ppl.common.utils.http.header.value.mediatype.MediaType;
import ppl.common.utils.http.header.value.mediatype.Mime;
import ppl.common.utils.http.response.Response;
import ppl.common.utils.http.response.ResponseCode;
import ppl.common.utils.json.JsonException;
import ppl.common.utils.json.jackson.JsonUtils;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class ResponseProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ResponseProcessor.class);
    private static final TypeReference<ErrorBody> ERROR_BODY_TYPE_REFERENCE = new TypeReference<ErrorBody>() {};

    public static RuntimeException processError(Response response) {
        ContentType contentType = response.getHeader(ContentType.class);
        MediaType value = contentType.knownValue();
        if (value == null || !Mime.JSON.equals(value.getArguments())) {
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
                return new HdfsException("Unknown error: " + body);
            }
        } catch (IOException e) {
            return new NetworkException("Error to get response body.", e);
        } catch (JsonException e) {
            throw new HdfsException("Invalid response body. not json.", e);
        }
    }

    public static <B> B processJson(Response response, TypeReference<B> reference) {
        ContentLength contentLength = response.getHeader(ContentLength.class);
        if (contentLength != null && contentLength.knownValue().getValue() == 0) {
            throw new HdfsException("Empty response body.");
        }

        ContentType contentType = response.getHeader(ContentType.class);
        if (contentType == null) {
            throw new HdfsException("No content type is given. Has no response body?");
        }

        MediaType value = contentType.knownValue();
        if (value == null || !Mime.JSON.equals(value.getArguments())) {
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
        } catch (JsonException e) {
            throw new HdfsException("Invalid response body. not json.", e);
        }
    }

    public static void firstStep(Response response) {
        if (response.getCode() == ResponseCode.TEMPORARY_REDIRECT) {
            Location location = response.getHeader(Location.class);
            if (location == null) {
                throw new HdfsException("Error temporary redirect. No location is found.");
            } else {
                throw new TemporaryRedirectException(location.knownValue().toCanonicalString());
            }
        } else if (response.getCode() == ResponseCode.CREATED) {
            throw new NeedAppendException();
        } else {
            throw new HdfsException("Unknown response code: " + response.getCode());
        }
    }

    public static void writeToRequestBody(InputStream is, Connection conn) {
        StopWatch watch = StopWatch.createStopWatch();
        try (OutputStream os = conn.openOutputStream()) {
            IOs.copy(is, os, 10240);
        } catch (IOException | IOStreamException e) {
            throw new NetworkException("Something error during data was written to request body.", e);
        } finally {
            logger.warn("Transfer time: " + watch.elapse(TimeUnit.SECONDS));
        }
    }
}
