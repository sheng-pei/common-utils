package ppl.common.utils.compress.impl;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.IOs;
import ppl.common.utils.compress.ArchiveException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.NoSuchElementException;

public final class Tar extends AbstractCCmprss {

    private static final Logger logger = LoggerFactory.getLogger(Tar.class);

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static Tar create(File tarFile) {
        try {
            return new Tar(tarFile, DEFAULT_CHARSET);
        } catch (IOException e) {
            throw new ArchiveException("Failed to open file: " + tarFile, e);
        }
    }

    public static Tar create(InputStream is) {
        return new Tar(is, DEFAULT_CHARSET);
    }

    private final TarArchiveInputStream is;
    private boolean nexted;
    private TarArchiveEntry e;

    private Tar(File file, Charset charset) throws IOException {
        this(Files.newInputStream(file.toPath()), charset);
    }

    private Tar(InputStream is, Charset charset) {
        super(logger);
        this.is = new TarArchiveInputStream(is, charset.name());
    }

    @Override
    protected boolean next() throws IOException {
        e = is.getNextTarEntry();
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
        return e.isSymbolicLink();
    }

    @Override
    protected String linkName() {
        checkNext();
        checkExists();
        return e.getLinkName();
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
//    private void putIntoTarStream(TarArchiveOutputStream tarStream, File file, String name) {
//        TarArchiveEntry tarEntry = new TarArchiveEntry(name);
//        tarEntry.setSize(file.length());
//        openArchiveEntry(tarStream, tarEntry);
//        copyToTar(file, tarStream);
//        closeArchiveEntry(tarStream);
//    }

//    private void openArchiveEntry(TarArchiveOutputStream tarOutputStream, ArchiveEntry entry) {
//        try {
//            tarOutputStream.putArchiveEntry(entry);
//        } catch (IOException e) {
//            throw new RuntimeException("File operation exception", e);
//        }
//    }

//    private void closeArchiveEntry(TarArchiveOutputStream tarOutputStream) {
//        try {
//            tarOutputStream.closeArchiveEntry();
//        } catch (IOException e) {
//            throw new RuntimeException("File operation exception", e);
//        }
//    }

//    private void copyToTar(File file, TarArchiveOutputStream tarOutputStream) {
//        InputStream is = null;
//        try {
//            is = new BufferedInputStream(Files.newInputStream(file.toPath()));
//            IOUtils.copy(is, tarOutputStream);
//        } catch (IOException e) {
//            throw new RuntimeException("file operation exception", e);
//        } finally {
//            IOUtils.closeQuietly(is);
//        }
//    }

}
