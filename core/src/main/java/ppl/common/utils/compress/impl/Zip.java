package ppl.common.utils.compress.impl;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.IOUtils;
import ppl.common.utils.compress.ArchiveException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class Zip extends AbstractCCmprss {

    private static final Logger logger = LoggerFactory.getLogger(Zip.class);
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static Zip create(File zipFile) {
        try {
            return new Zip(zipFile, DEFAULT_CHARSET);
        } catch (IOException e) {
            throw new ArchiveException("Failed to open file: " + zipFile, e);
        }
    }

    private final ZipFile file;
    private final Enumeration<ZipArchiveEntry> entries;
    private boolean nexted;
    private ZipArchiveEntry e;

    private Zip(File file, Charset charset) throws IOException {
        super(logger);
        this.file = new ZipFile(file, charset.name());
        this.entries = this.file.getEntries();
    }

    @Override
    protected boolean next() throws IOException {
        nexted = true;
        boolean hasNext = entries.hasMoreElements();
        if (hasNext) {
            e = entries.nextElement();
        } else {
            e = null;
        }
        return hasNext;
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
    protected void copy(OutputStream os) throws IOException {
        checkNext();
        checkExists();
        IOUtils.copy(file.getInputStream(e), os);
    }

    @Override
    protected boolean isSymbolicLink() {
        checkNext();
        checkExists();
        return e.isUnixSymlink();
    }

    @Override
    protected String linkName() throws IOException {
        checkNext();
        checkExists();
        return file.getUnixSymlink(e);
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
            file.close();
        } catch (IOException e) {
            throw new ArchiveException("Failed to close.", e);
        }
    }
}
