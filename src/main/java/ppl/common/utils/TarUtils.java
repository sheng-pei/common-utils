package ppl.common.utils;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.*;

public final class TarUtils {

    private TarUtils() { }

    private void putIntoTarStream(TarArchiveOutputStream tarStream, File file, String name) {
        TarArchiveEntry tarEntry = new TarArchiveEntry(name);
        tarEntry.setSize(file.length());
        openArchiveEntry(tarStream, tarEntry);
        copyToTar(file, tarStream);
        closeArchiveEntry(tarStream);
    }

    private void openArchiveEntry(TarArchiveOutputStream tarOutputStream, ArchiveEntry entry) {
        try {
            tarOutputStream.putArchiveEntry(entry);
        } catch (IOException e) {
            throw new RuntimeException("File operation exception", e);
        }
    }

    private void closeArchiveEntry(TarArchiveOutputStream tarOutputStream) {
        try {
            tarOutputStream.closeArchiveEntry();
        } catch (IOException e) {
            throw new RuntimeException("File operation exception", e);
        }
    }

    private void copyToTar(File file, TarArchiveOutputStream tarOutputStream) {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            IOUtils.copy(is, tarOutputStream);
        } catch (IOException e) {
            throw new RuntimeException("file operation exception", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}
