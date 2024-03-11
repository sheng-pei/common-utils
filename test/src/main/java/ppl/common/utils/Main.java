package ppl.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.character.ascii.Mask;
import ppl.common.utils.config.Node;
import ppl.common.utils.config.Nodes;
import ppl.common.utils.config.Value;
import ppl.common.utils.filesystem.core.CFile;
import ppl.common.utils.filesystem.core.Connection;
import ppl.common.utils.filesystem.core.FileSystem;
import ppl.common.utils.filesystem.core.Protocol;
import ppl.common.utils.filesystem.ftp.FtpProperties;
import ppl.common.utils.filesystem.path.Path;
import ppl.common.utils.filesystem.path.Paths;
import ppl.common.utils.filesystem.sftp.SftpProperties;
import ppl.common.utils.http.url.Query;
import ppl.common.utils.http.url.URL;
import ppl.common.utils.net.URLEncoder;
import ppl.common.utils.test.JsonData;
import ppl.common.utils.test.ResBody;
import ppl.common.utils.test.json.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException, IOException {
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
        String json = "{\n" +
                "\"meta\": {\n" +
                "    \"code\": 1,\n" +
                "    \"message\": \"message\"\n" +
                "},\n" +
                "\"data\": {\n" +
                "  \"a\": 1,\n" +
                "  \"b\": 2\n" +
                "}\n" +
                "}";
        ResBody<JsonData> body = JsonUtil.parseObject(json, new TypeReference<ResBody<JsonData>>() {
        });
        System.out.println(JsonUtil.toString(body.getData()));

        URL url = URL.create("http://localhost/aa?你好jk=mk");
        url = url.appendDynamicQuery(" jk", "mk ");
        url = url.removeDynamicQuery("jk");
        System.out.println(url.toString());

        URLEncoder encoder = URLEncoder.builder()
                .or(Mask.mask("$'()*+,;:@/?-").predicate())
                .or(Mask.NON_OCTET.predicate())
                .build();
        System.out.println(encoder.parse("&=jfie nzj.?d你好"));
        System.out.println("你好");
    }
}