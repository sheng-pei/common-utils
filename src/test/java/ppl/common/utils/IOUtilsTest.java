package ppl.common.utils;

import org.apache.commons.lang3.EnumUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.*;
import java.util.Date;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IOUtilsTest {

    @Mock
    private Closeable closeable;

    @Test
    public void testCloseInvoked() throws Exception {
        IOUtils.closeQuietly(closeable);
        verify(closeable).close();
    }

    @Test
    public void testCloseThrowException() throws Exception {
        doThrow(new IOException()).when(closeable).close();
        IOUtils.closeQuietly(closeable);
    }

    @Test
    public void testCopy() throws Exception {
        File inFile = new File("/Users/shengpei/Desktop/osg-uc0009.tgz");
        File outFile = new File("/Users/shengpei/Desktop/oo00oo.tgz");
        InputStream is = new FileInputStream(inFile);
        OutputStream os = new FileOutputStream(outFile);
        Date start = new Date();
        IOUtils.copy(is, os, 10240);
        System.out.println("Elapsed time: " + (System.currentTimeMillis() - start.getTime()));
    }

}