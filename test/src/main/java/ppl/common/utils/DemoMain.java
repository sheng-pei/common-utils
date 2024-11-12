package ppl.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import ppl.common.utils.filesystem.core.FileSystem;
import ppl.common.utils.filesystem.core.Protocol;
import ppl.common.utils.filesystem.ftp.FtpProperties;

import java.io.File;

public class DemoMain {
    public static void main(String[] args) {
        FtpProperties properties = FtpProperties.builder()
                .setHost("localhost")
                .setPort(21)
                .setUsername("admin")
                .setPassword("111111")
                .setWorking("/").build();
        try (FileSystem system = Protocol.FTP.open(properties)) {
            system.getConnection().store("2", new File("README.md"));
        }
    }
}
