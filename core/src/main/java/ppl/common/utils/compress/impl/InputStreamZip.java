package ppl.common.utils.compress.impl;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.IOs;
import ppl.common.utils.compress.ArchiveException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

public class InputStreamZip extends AbstractCCmprss {

    private static final Logger logger = LoggerFactory.getLogger(InputStreamZip.class);
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

//    public static InputStreamZip create(File zipFile) {
//        try {
//            return new InputStreamZip(zipFile, DEFAULT_CHARSET);
//        } catch (IOException e) {
//            throw new ArchiveException("Failed to open file: " + zipFile, e);
//        }
//    }

    public static InputStreamZip create(InputStream is) {
        return new InputStreamZip(is, DEFAULT_CHARSET);
    }

//    private InputStreamZip(File zipFile, Charset charset) throws IOException {
//        this(Files.newInputStream(zipFile.toPath()), charset);
//    }

    private final ZipArchiveInputStream is;
    private boolean nexted;
    private ZipArchiveEntry e;

    private InputStreamZip(InputStream is, Charset charset) {
        super(logger);
        this.is = new ZipArchiveInputStream(is, charset.name());
    }

    @Override
    protected boolean next() throws IOException {
        e = is.getNextZipEntry();
        nexted = true;
        return e != null;
    }

    @Override
    protected String entryName() {
        checkNext();
        checkExists();
        return e.getName();
    }

    @Override
    protected boolean isDirectory() {
        checkNext();
        checkExists();
        return e.isDirectory();
    }

    @Override
    protected void copy(OutputStream os) {
        checkNext();
        checkExists();
        IOs.copy(is, os);
    }

    @Override
    protected boolean isSymbolicLink() {
        checkNext();
        checkExists();
        return false;
    }

    @Override
    protected String linkName() {
        checkNext();
        checkExists();
        throw new UnsupportedOperationException("Symbolic link is not supported by zip archive input stream.");
    }

    private void checkNext() {
        if (!nexted) {
            throw new IllegalStateException("Please call next first.");
        }
    }

    private void checkExists() {
        if (e == null) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void close() throws ArchiveException {
        try {
            is.close();
        } catch (IOException e) {
            throw new ArchiveException("Failed to close.", e);
        }
    }
}
