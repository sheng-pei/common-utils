package ppl.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import ppl.common.utils.bytes.Bytes;
import ppl.common.utils.ext.Exts;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DemoMain {
    private static final Pattern P_NUMBER_LABEL = Pattern.compile("\\s*[（(][0-9]+[)）]\\s*$");
    private static final Pattern P_EXACT_SECRET_FLAG = Pattern.compile("[(（](?:公开|内部|秘密|机密)[）)]$");

    public static void main(String[] args) throws IOException {
        Pattern p = Pattern.compile("^rar(\\.|$)");
        Matcher matcher = p.matcher("rar");
        matcher.find();
        System.out.println(Exts.DEFAULT_EXTS.parseKnownExt(".rar").getBase());
    }
}
