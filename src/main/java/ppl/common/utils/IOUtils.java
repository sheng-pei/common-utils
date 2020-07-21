package ppl.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.exception.StreamException;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

    private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);

    private static final int COPY_BUF_SIZE = 8024;

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                logger.error("Cannot close resource", e);
            }
        }
    }

    public static void copy(InputStream is, OutputStream os) {
        copy(is, os, COPY_BUF_SIZE);
    }

    public static void copy(InputStream is, OutputStream os, int bufferSize) {
        if (bufferSize < 1) {
            throw new IllegalArgumentException("Required positive size, but: " + bufferSize);
        }

        try {
            org.apache.commons.io.IOUtils.copy(is, os, bufferSize);
        } catch (IOException e) {
            throw new StreamException("Error to copy", e);
        }

    }

}
