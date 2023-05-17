package ppl.common.utils.filesystem;

import com.sun.crypto.provider.HmacMD5;
import ppl.common.utils.net.MaskPredicate;
import ppl.common.utils.net.URLDecoder;
import ppl.common.utils.net.URLEncoder;

import java.net.URI;

public class Main {
    public static void main(String[] args) throws Exception {
        URI uri = URI.create("http://www.baidu.com/agbz中h/sign?me*th?od=iii/&pathname=mfjje#你好");
        System.out.println(URLEncoder.encode(" 中%20国nfeiij%%"));
        System.out.println(URLDecoder.decode("%20%E4%B8%AD%25"));
//        System.out.println(URLEncoder.encode("房间"));
//        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
//        connection.connect();
//        System.out.printf("" + connection.getResponseCode());
    }
}
