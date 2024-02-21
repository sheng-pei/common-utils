package ppl.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.filesystem.core.CFile;
import ppl.common.utils.filesystem.core.Connection;
import ppl.common.utils.filesystem.core.FileSystem;
import ppl.common.utils.filesystem.core.Protocol;
import ppl.common.utils.filesystem.ftp.FtpProperties;
import ppl.common.utils.filesystem.path.Path;
import ppl.common.utils.filesystem.path.Paths;
import ppl.common.utils.filesystem.sftp.SftpProperties;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
//        CommandArguments arguments = CommandArguments.newBuilder()
//                .addArgument(ValueOptionArgument.requiredIdentity("host", 'h'))
//                .addArgument(ToggleOptionArgument.toggle("enabled", 'e'))
//                .addArgument(PositionArgument.newBuilder("config").build(PositionArgument.defToCanonical()))
//                .build();
//        CommandParser parser = new CommandParser(arguments);
//        parser.parse(args).forEach(System.out::println);
//        System.out.println(arguments.get("-h"));
//        Command command = new Command(arguments);
//        command.init(args);
//        System.out.println(command.get("host"));
//        System.out.println(command.get("config"));
//        SftpProperties properties = new SftpProperties();
//        properties.setServer("172.16.101.180");
//        properties.setPort(22);
//        properties.setUsername("root");
//        properties.setPassword("Abc!@#135");
//        properties.setWorking("/opt/dtstack/a/b/c");
//        properties.setCharset(Charset.forName("GBK"));
//        try (FileSystem sftp = Protocol.SFTP.open(properties)) {
//            try (Connection conn = sftp.getConnection()) {
//                List<CFile> files = conn.listFiles(LocalDateTime.now().minusDays(1), true);
//                System.out.println(files.size());
//            }
//        }

        Exts exts = new Exts(Arrays.asList("zip"));
        System.out.println(exts.getExt("a.zip"));
    }
}