package ppl.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.argument.argument.value.collector.ExCollectors;
import ppl.common.utils.command.*;
import ppl.common.utils.filesystem.core.CFile;
import ppl.common.utils.filesystem.core.Connection;
import ppl.common.utils.filesystem.core.FileSystem;
import ppl.common.utils.filesystem.core.Protocol;
import ppl.common.utils.filesystem.sftp.SftpProperties;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        CommandArguments arguments = CommandArguments.newBuilder()
                .addArgument(ValueOptionArgument.newBuilder("test", 't').split(s -> {
                    return Arrays.stream(s.split(","));
                }).map(Integer::parseInt).collect(ExCollectors.set()).build(s -> {
                    StringBuilder builder = new StringBuilder();
                    for (Integer i : s) {
                        builder.append(i).append(",");
                    }
                    builder.setLength(builder.length() - 1);
                    return builder.toString();
                }))
                .addArgument(ValueOptionArgument.requiredIdentity("host", 'h'))
                .addArgument(ToggleOptionArgument.toggle("enabled", 'e'))
                .addArgument(PositionArgument.newBuilder("config").build(Function.identity()))
                .build();
        CommandParser parser = new CommandParser(arguments);
//        parser.parse(args).forEach(System.out::println);
//        System.out.println(arguments.get("-h"));
        Command command = new Command(arguments);
        command.init(args);
        System.out.println(command);
        System.out.println(command.get("host"));
        System.out.println(command.get("config"));
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
//        String json = "{\n" +
//                "\"meta\": {\n" +
//                "    \"code\": 1,\n" +
//                "    \"message\": \"message\"\n" +
//                "},\n" +
//                "\"data\": {\n" +
//                "  \"a\": 1,\n" +
//                "  \"b\": 2\n" +
//                "}\n" +
//                "}";
//        ResBody<JsonData> body = JsonUtil.parseObject(json, new TypeReference<ResBody<JsonData>>() {
//        });
//        System.out.println(JsonUtil.toString(body.getData()));
//
//        URL url = URL.create("http://localhost/aa?你好jk=mk");
//        url = url.appendDynamicQuery(" jk", "mk ");
//        url = url.removeDynamicQuery("jk");
//        System.out.println(url.toString());
//
//        List<Query> q = Queries.parseQueries("%20a=jng=ko&&finge=你好");
//        System.out.println(q.toString());
//
//        java.net.URL url1 = new java.net.URL("http://localhost:18080/myapp/rest1/test2");
//        HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
//        conn.connect();
//        conn.getInputStream();
//        conn.disconnect();
//
//        url1.openConnection();
    }
}