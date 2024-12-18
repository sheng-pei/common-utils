package ppl.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import ppl.common.utils.ext.Exts;
import ppl.common.utils.filesystem.core.FileSystem;
import ppl.common.utils.filesystem.core.Protocol;
import ppl.common.utils.filesystem.ftp.FtpProperties;
import ppl.common.utils.json.jackson.JsonUtils;

import java.io.File;
import java.io.IOException;

public class DemoMain {
    public static class A {
        private Integer i;

        public Integer getI() {
            return i;
        }

        public void setI(Integer i) {
            this.i = i;
        }
    }

    public static void main(String[] args) throws IOException {
        FileName fileName = FileName.create(Exts.DEFAULT_EXTS, "y.a(important)(公开).doc");
        System.out.println(fileName.toString());
    }
}
