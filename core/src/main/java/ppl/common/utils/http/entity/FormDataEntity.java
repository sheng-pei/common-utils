package ppl.common.utils.http.entity;

import ppl.common.utils.http.Entity;
import ppl.common.utils.http.header.known.ContentDisposition;
import ppl.common.utils.http.header.known.ContentType;
import ppl.common.utils.http.header.value.disposition.Disposition;
import ppl.common.utils.http.header.value.disposition.DispositionType;
import ppl.common.utils.http.header.value.mediatype.Mime;
import ppl.common.utils.pair.Pair;
import ppl.common.utils.string.Strings;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

//RFC 2388, 7578
public class FormDataEntity implements Entity {

    private static final ContentType CONTENT_TYPE = new ContentType(Mime.MULTIPART_FORM_DATA.toString());

    private final ContentType contentType;
    private final String delimiter;
    private final String lastDelimiter;
    private final List<Pair<String, Entity>> fields = new ArrayList<>();

    public FormDataEntity() {
        String boundary = new Boundaries().getBoundary();
        this.delimiter = "\r\n--" + boundary + "\r\n";
        this.lastDelimiter = "\r\n--" + boundary + "--\r\n";
        this.contentType = new ContentType(CONTENT_TYPE.knownValue().setParameter("boundary", boundary));
    }

    public void addField(String name, Entity value) {
        if (value instanceof FormDataEntity) {
            throw new IllegalArgumentException("Nest form data is not allowed.");
        }
        fields.add(Pair.create(name, value));
    }

    @Override
    public ContentType contentType() {
        return contentType;
    }

    @Override
    public void write(OutputStream os) {
        // "text/plain" could be ignored
        // Multiple Files for One Form Field sent by supplying each file in a separate part but all with the same"name" parameter
        try {
            for (Pair<String, Entity> field : fields) {
                os.write(this.delimiter.getBytes(StandardCharsets.ISO_8859_1));
                String name = field.getFirst();
                Entity entity = field.getSecond();
                DispositionType dispositionType = DispositionType.ensureKnown(Disposition.FORM_DATA.toString());
                dispositionType.setParameter("name", new String(name.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
                if (entity instanceof FileEntity) {
                    FileEntity fileEntity = (FileEntity) entity;
                    if (Strings.isNotEmpty(fileEntity.filename())) {
                        dispositionType.setParameter("filename", new String(fileEntity.filename().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
                    }
                }
                ContentDisposition disposition = new ContentDisposition(dispositionType);
                os.write(disposition.toCanonicalString().getBytes(StandardCharsets.ISO_8859_1));
                os.write("\r\n".getBytes());
                ContentType contentType = entity.contentType();
                String stringContentType = contentType.toCanonicalString();
                if (!stringContentType.equals(Mime.PLAIN.toString())) {
                    os.write(stringContentType.getBytes(StandardCharsets.ISO_8859_1));
                    os.write("\r\n".getBytes());
                }
                entity.write(os);
            }
            os.write(this.lastDelimiter.getBytes(StandardCharsets.ISO_8859_1));
        } catch (IOException e) {
            throw new RuntimeException("Form data write error.", e);
        }
    }
}
