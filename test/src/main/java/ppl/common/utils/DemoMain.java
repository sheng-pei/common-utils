package ppl.common.utils;

import ppl.common.utils.ext.Exts;

import java.io.IOException;
import java.util.Objects;

public class DemoMain {
    public static void main(String[] args) throws IOException {
        Exts exts = Exts.builder()
                .add("rip/bsd/\\.bsd[0-9]*$")
                .add("rcp/uTc/\\.uTc[0-9]*$")
                .build();
        System.out.println(exts.getExt("a.bsD98").getExt());
//        System.out.println(exts.getExt("a.bsD98").isKnown());
        System.out.println(exts.getExt("a.utc98").getExt());
        System.out.println(exts.getExt("a.uTc98").getExt());
//        System.out.println(exts.getExt("a.bsD98").isKnown());
    }
}
