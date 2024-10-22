package ppl.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import ppl.common.utils.ext.Exts;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class DemoMain {
    public static void main(String[] args) throws IOException {
        System.out.println(Exts.DEFAULT_EXTS.parse("a.bsD98").getExt());
        System.out.println(Exts.DEFAULT_EXTS.parse("prepin.vafew").getExt());
        System.out.println(Exts.DEFAULT_EXTS.parse("uuu.tar.gz").getExt());
//        System.out.println(exts.getExt("a.bsD98").isKnown());
//        System.out.println(exts.getExt("a.utc98").getExt());
//        System.out.println(exts.getExt("a.uTc98").getExt());
//        System.out.println(exts.getExt("a.bsD98").isKnown());
    }
}
