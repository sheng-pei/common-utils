package ppl.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.exception.IOStreamException;

import java.io.*;

public final class IOs {

    private IOs() { }

    private static final Logger logger = LoggerFactory.getLogger(IOs.class);

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
            byte[] bytes = new byte[bufferSize];
            int cnt = is.read(bytes);
            while (cnt >= 0) {
                os.write(bytes, 0, cnt);
                cnt = is.read(bytes);
            }
            os.flush();
        } catch (IOException e) {
            throw new IOStreamException("Error to copy", e);
        }

    }

}
