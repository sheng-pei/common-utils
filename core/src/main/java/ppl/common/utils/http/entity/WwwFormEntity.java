package ppl.common.utils.http.entity;

import ppl.common.utils.http.Entity;
import ppl.common.utils.http.header.known.ContentType;
import ppl.common.utils.http.header.value.mediatype.MediaType;
import ppl.common.utils.net.URLEncoder;
import ppl.common.utils.pair.Pair;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class WwwFormEntity implements Entity {
    private static final ContentType DEFAULT_CONTENT_TYPE =
            new ContentType(MediaType.ensureKnown("application/x-www-form-urlencoded"));
    private static final URLEncoder URL_ENCODER = URLEncoder.builder()
            .setLineBreak("\r\n")
            .setUsePlus(true)
            .build();

    private final List<Pair<String, String>> fields = new ArrayList<>();

    public void addField(String name, String value) {
        this.fields.add(Pair.create(name, value));
    }

    @Override
    public ContentType contentType() {
        return DEFAULT_CONTENT_TYPE;
    }

    @Override
    public void write(OutputStream os) {
        String s = fields.stream().map(p -> {
            String name = p.getFirst();
            String value = p.getSecond();
            return URL_ENCODER.parse(name) + "=" + URL_ENCODER.parse(value);
        }).collect(Collectors.joining("&"));
        try {
            os.write(s.getBytes(StandardCharsets.US_ASCII));
        } catch (Exception e) {
            throw new RuntimeException("Data transfer error.", e);
        }
    }
}
