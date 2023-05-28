package ppl.common.utils.filesystem;

//import ppl.common.utils.net.URLDecoder;
//import ppl.common.utils.net.URLEncoder;

public class Main {
    public static void main(String[] args) throws Exception {
//        URI uri = URI.create("http://www.baidu.com/agbz中h/sign?me*th?od=iii/&pathname=mfjje#你好");
//        System.out.println(URLEncoder.encode(" 中%20国nfeiij%%"));
//        System.out.println(URLDecoder.decode("%20%E4%B8%AD%25"));

//        System.out.println(URLEncoder.encode("房间"));
//        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
//        connection.connect();
//        System.out.printf("" + connection.getResponseCode());
    }

    private static long highMask(char first, char last) {
        long m = 0;
        int f = Math.max(Math.min(first, 127), 64) - 64;
        int l = Math.max(Math.min(last, 127), 64) - 64;
        for (int i = f; i <= l; i++)
            m |= 1L << i;
        return m;
    }
}
