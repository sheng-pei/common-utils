package ppl.common.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class IOUtilsTest {

    @Mock
    private Closeable closeable;

    @Test
    public void testCloseInvoked() throws Exception {
        IOUtils.closeQuietly(closeable);
        Mockito.verify(closeable).close();
    }

    @Test
    public void testCloseThrowException() throws Exception {
        Mockito.doThrow(new IOException()).when(closeable).close();
        IOUtils.closeQuietly(closeable);
    }

    @Test
    public void testCopy() throws Exception {
        try {
            File inFile = new File("/Users/shengpei/Desktop/osg-uc0009.tgz");
            File outFile = new File("/Users/shengpei/Desktop/oo00oo.tgz");
            InputStream is = new FileInputStream(inFile);
            OutputStream os = new FileOutputStream(outFile);
            Date start = new Date();
            IOUtils.copy(is, os, 10240);
            System.out.println("Elapsed time: " + (System.currentTimeMillis() - start.getTime()));
        } catch (FileNotFoundException e) {
            System.out.println("no source file ignored");
        }
    }

}