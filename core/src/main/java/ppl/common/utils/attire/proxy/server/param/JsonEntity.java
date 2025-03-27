package ppl.common.utils.attire.proxy.server.param;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import ppl.common.utils.http.Entity;
import ppl.common.utils.http.header.known.ContentType;
import ppl.common.utils.http.header.value.mediatype.MediaType;
import ppl.common.utils.json.jackson.JsonUtils;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JsonEntity implements Entity {

    private static final ContentType DEFAULT_CONTENT_TYPE = new ContentType("application/json");
    private static final Map<Charset, JsonEncoding> JSON_ENCODINGS;

    static {
        Map<Charset, JsonEncoding> jsonEncodings = new HashMap<>();
        JsonEncoding[] jsonEncodings1 = JsonEncoding.values();
        for (JsonEncoding jsonEncoding : jsonEncodings1) {
            jsonEncodings.put(Charset.forName(jsonEncoding.getJavaName()), jsonEncoding);
        }
        JSON_ENCODINGS = Collections.unmodifiableMap(jsonEncodings);
    }

    private final ContentType contentType;
    private final Object content;
    private final ObjectMapper mapper;

    public JsonEntity(Object content) {
        this.contentType = DEFAULT_CONTENT_TYPE;
        this.content = content;
        this.mapper = JsonUtils.defaultObjectMapper();
    }

    public JsonEntity(Charset charset, Object content) {
        if (charset == null) {
            this.contentType = DEFAULT_CONTENT_TYPE;
        } else {
            MediaType mediaType = DEFAULT_CONTENT_TYPE.knownValue();
            mediaType.setParameter("charset", charset);
            this.contentType = new ContentType(mediaType);
        }
        this.content = content;
        this.mapper = JsonUtils.defaultObjectMapper();
    }

    public JsonEntity(Charset charset, Object content, ObjectMapper mapper) {
        Objects.requireNonNull(mapper);
        this.content = content;
        this.mapper = mapper;
        if (charset == null) {
            this.contentType = DEFAULT_CONTENT_TYPE;
        } else {
            MediaType mediaType = DEFAULT_CONTENT_TYPE.knownValue();
            mediaType.setParameter("charset", charset);
            this.contentType = new ContentType(mediaType);
        }
    }

    @Override
    public ContentType contentType() {
        return this.contentType;
    }

    @Override
    public void write(OutputStream os) {
        Charset charset = (Charset) contentType.knownValue().getParameter("charset");
        try {
            JsonEncoding encoding = charset == null ? JsonEncoding.UTF8 : JSON_ENCODINGS.get(charset);
            if (encoding != null) {
                JsonGenerator generator = mapper.createGenerator(os, encoding);
                generator.writeObject(content);
            } else {
                String string = mapper.writeValueAsString(content);
                Writer writer = new OutputStreamWriter(os, charset);
                writer.write(string);
            }
        } catch (Exception e) {
            throw new RuntimeException("Json serializer error.", e);
        }
    }
}
