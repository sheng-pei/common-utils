package ppl.common.utils;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class ResourceUtilsTest {

    @Test
    public void test() throws MalformedURLException {
        URL url = new URL("Jar:uua:a!/aa");

        System.out.println(url.toString());
    }

}
