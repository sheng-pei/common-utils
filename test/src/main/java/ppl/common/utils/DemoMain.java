package ppl.common.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import ppl.common.utils.ext.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class DemoMain {

    public static class A {
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
        private Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }

    public static void main(String[] args) throws IOException {
//        A a = new A();
//        a.setDate(new Date());
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        System.out.println(objectMapper.writeValueAsString(a));
//        System.out.println(JsonUtils.read("{\"date\":\"2024-10-14\"}", A.class).getDate());
//        System.out.println(JsonUtils.write(a));

//        A b = objectMapper.readValue("{\"date\":\"2024-十月-13 11:04:23\"}", A.class);
//        A b1 = JsonUtils.read("{\"date\":\"2024-十月-13 11:04:23\"}", A.class);
//        System.out.println(b.getDate());
//        System.out.println(b1.getDate());

//        ExtPatternParser.compile("rcp/ext/.*");
        Exts exts = Exts.builder().add(ExtPatternParser.compile("exe", 0))
                .add(ExtPatternParser.compile("tar", 1))
                .add(ExtPatternParser.compile("tar.gz", 2))
                .add(ExtPatternParser.compile("gz", 3))
                .add(ExtPatternParser.compile("0c/Ext", 4))
                .add(ExtPatternParser.compile("0c/JPG/", 5))
                .add(ExtPatternParser.compile("0ie/mp3", 6))
                .add(ExtPatternParser.compile("rie/brt/\\.brt(?:\\.[0-9]+)?$", 7))
                .add(ExtPatternParser.compile("rie/prepin/^prepin\\.", 8))
                .build();
        System.out.println(exts.getExt("a.brt.109"));
        System.out.println(exts.getExt("a.bRt.109"));
        System.out.println(exts.getExt("prepi8n.jnhgi"));
        System.out.println(exts.getExt("prEpiN.jnhgi"));

    }
}
