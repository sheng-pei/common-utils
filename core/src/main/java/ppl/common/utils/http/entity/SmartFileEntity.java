package ppl.common.utils.http.entity;

import ppl.common.utils.IOs;
import ppl.common.utils.ext.Ext;
import ppl.common.utils.ext.Exts;
import ppl.common.utils.http.header.known.ContentType;
import ppl.common.utils.http.header.value.mediatype.MediaType;
import ppl.common.utils.http.header.value.mediatype.Mime;
import ppl.common.utils.string.Strings;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;

public class SmartFileEntity implements FileEntity {

    private static final Mime DEFAULT_MIME = Mime.OCTET;

    private final File file;
    private final ContentType contentType;

    public SmartFileEntity(File file) {
        this(file, Exts.DEFAULT_EXTS);
    }

    public SmartFileEntity(File file, Exts exts) {
        Objects.requireNonNull(file);
        Objects.requireNonNull(exts);
        Mime mime = null;
        Ext ext = exts.parseExt(file.getName());
        if (ext != null) {
            mime = Mime.primary(ext.getExt());
        }
        mime = mime == null ? DEFAULT_MIME : mime;
        this.contentType = new ContentType(MediaType.ensureKnown(mime.toString()));
        this.file = file;
    }

    @Override
    public String filename() {
        return file.getName();
    }

    @Override
    public ContentType contentType() {
        return contentType;
    }

    @Override
    public void write(OutputStream os) {
        try (InputStream is = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
            IOs.copy(is, os);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(Strings.format(
                    "File not found: '{}'.", file.getAbsoluteFile()), e);
        } catch (Exception e) {
            throw new RuntimeException(Strings.format(
                    "File write error: '{}'.", file.getAbsoluteFile()), e);
        }
    }
}
