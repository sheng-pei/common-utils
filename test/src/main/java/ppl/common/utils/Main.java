package ppl.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import ppl.common.utils.character.ascii.Mask;
import ppl.common.utils.http.url.URL;
import ppl.common.utils.json.jackson.CommonModule;
import ppl.common.utils.json.jackson.JavaTimeModule;
import ppl.common.utils.net.URLEncoder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;

public class Main {

    private static final URLEncoder DYNAMIC_QUERY_NAME_ENCODER = URLEncoder.builder()
            .setPercentEncodingReserved(true)
            .or(Mask.asciiMask("$'()*+,;:@/?-").predicate())
            .or(Mask.NON_OCTET.predicate())
            .build();

    public static void main(String[] args) throws IOException {
//        java.net.URL u = new java.net.URL("http://a.b.c/a/?");
//        System.out.println(u.toString());
        URL url = URL.create("http://wWw.%3fDu.com:80/中/[]?===[]#");
        System.out.println(url.toString());
        System.out.println(url.normalizeString());

//        String random = "LT-vCMEvlxZBPcwW1uZ9Wn7";
//        ECPublicKey publicKey = BCECUtils.publicKey("04bd2df35b56122e520452083a9c8e21861a9325ebe32851be97317e6bbe15e88005c3bc077d07a90107150b66a250b697dfbbe2600026eb2abc5d10b24357b108");
//        ECPublicKey publicKey = BCECUtils.publicKey("0456cefd60d7c87c000d58ef57fa73ba4d9c0dfa08c08a7331495c2e1da3f2bd5231b7e7e6cc8189f668535ce0f8eaf1bd6de84c182f6c8e716f780d3a970a23c3");
//        ECPrivateKey privateKey = BCECUtils.privateKey("848830bddbf9c87627a356bb3007ffb65e4af39e60df475340220a6f187c22bf");
//        System.out.println(new String(ECUtils.decrypt(privateKey, Bytes.fromHex("04236cfe5ccae28be7f82cb1a942487e6137e2c3c938269c37b0a6b4ca46e970e954987b9e4228337f68230c96f80834c1a59545af6523d815a281577c9f1d457405cc52f444f9d2566892107e4c9a86844296783578605c687984419a707966cc624d5080f248992cafeaa2b19a3e182dff54d2b92913ad55b38dd61a91ca98c6d2960ea18e461cb85350b5d0715fcb99c161034871c3fec83af277edf2f4588edb383b82f7cc2a4612f5120c2736c0d799c8885081f2f93f4e5cce519b2eb06e800f369441"))));

        CommonModule module = new CommonModule();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(module);
        ZonedDateTime zonedDateTime = mapper.readValue("\"2024-09-03T15:41:57.187000+08:00\"", ZonedDateTime.class);
        System.out.println(mapper.writeValueAsString(zonedDateTime.toLocalDateTime()));

//        String salt = "$2y$10$" + Base64.getEncoder().encodeToString(padding("baizhi@dtstack.comfawegwegawefawefaewfwe".getBytes(), ' ', 16));
//        String digest = BCrypt.hashpw("DT#passw0rd2022".getBytes(), salt);
//        System.out.println(digest);
//
//        String filename = "zip文件时间（公开） (1).zip";
//        filename = filename.trim();
//        int lastIdx = filename.lastIndexOf('.');
//        if (lastIdx >= 0) {
//            filename = filename.substring(0, lastIdx);
//        }
//        System.out.println(filename);
//
//
////
//        MessageDigestPasswordEncoder encoder = new MessageDigestPasswordEncoder("SHA-256");
//        System.out.println(encoder.encode(digest));
//        System.out.println(encoder.encode(digest).length());
//        String body = random + digest + "baizhi@dtstack.com";
//        System.out.println(Bytes.hex(body.getBytes()));
//        System.out.println(Bytes.hex(ECUtils.encrypt(publicKey, "aa".getBytes())));
//        File file = new File("body");
//        OutputStream os = new FileOutputStream(file);
//        os.write(ECUtils.encrypt(publicKey, (random + "$user$user@dtstack.com").getBytes()));
//        System.out.println(new String(ECUtils.encrypt(publicKey, (random + "$user$user@dtstack.com").getBytes())));
//        CommandArguments arguments = CommandArguments.newBuilder()
//                .addArgument(ValueOptionArgument.newBuilder("test", 't')
//                        .split(s -> Arrays.stream(s.split(",")))
//                        .map(Integer::parseInt)
//                        .collect(ExCollectors.set())
//                        .build(s -> {
//                            StringBuilder builder = new StringBuilder();
//                            for (Integer i : s) {
//                                builder.append(i).append(",");
//                            }
//                            builder.setLength(builder.length() - 1);
//                            return builder.toString();
//                        }))
//                .addArgument(ValueOptionArgument.requiredIdentity("host", 'h'))
//                .addArgument(ToggleOptionArgument.toggle("enabled", 'e'))
//                .addArgument(PositionArgument.newBuilder("config").build(Function.identity()))
//                .build();
//        Command command = new Command(arguments);
//        command.init(args);
//        System.out.println(command);
//        System.out.println(command.get("host"));
//        System.out.println(command.get("config"));
        Mask.asciiMask(":/?#[]@!$&'()*+,;=");
    }

    private static byte[] padding(byte[] s, char pad, int size) {
        if (s.length < size) {
            byte[] s1 = new byte[size];
            Arrays.fill(s1, (byte) pad);
            System.arraycopy(s, 0, s1, 0, s.length);
            return s1;
        }
        return s;
    }

}