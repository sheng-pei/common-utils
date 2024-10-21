package ppl.common.utils;

import ppl.common.utils.ext.Exts;

import java.io.IOException;
import java.util.Objects;

public class DemoMain {
    public static void main(String[] args) throws IOException {
        System.out.println(Exts.DEFAULT_EXTS.parse("a.bsD98").getBase());
        System.out.println(Exts.DEFAULT_EXTS.parse("prepin.vafew").getExt());
//        System.out.println(exts.getExt("a.bsD98").isKnown());
//        System.out.println(exts.getExt("a.utc98").getExt());
//        System.out.println(exts.getExt("a.uTc98").getExt());
//        System.out.println(exts.getExt("a.bsD98").isKnown());
    }
}
