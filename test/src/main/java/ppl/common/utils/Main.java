package ppl.common.utils;

import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {

//        String random = "LT-wZ1dnMhT2DPCmkqtVCI+";
//        ECPublicKey publicKey = BCECUtils.publicKey("04bd2df35b56122e520452083a9c8e21861a9325ebe32851be97317e6bbe15e88005c3bc077d07a90107150b66a250b697dfbbe2600026eb2abc5d10b24357b108");
//        ECPrivateKey privateKey = BCECUtils.privateKey("848830bddbf9c87627a356bb3007ffb65e4af39e60df475340220a6f187c22bf");
//
//        String salt = "$2y$10$" + Base64.getEncoder().encodeToString(padding("baizhi@dtstack.com".getBytes(), ' ', 16));
//        String digest = BCrypt.hashpw("DT#passw0rd2022".getBytes(), salt);
//        System.out.println(digest);
//
////        MessageDigestPasswordEncoder encoder = new MessageDigestPasswordEncoder("SHA-256");
//        String body = random + "#" + digest + "#" + "baizhi@dtstack.com";
//        File file = new File("body");
//        try (OutputStream os = new FileOutputStream(file)) {
//            os.write(Bytes.hex(ECUtils.encrypt(publicKey, body.getBytes())).getBytes());
//        }
//
//        System.out.println(URLEncoder.encode("http://localhost:8080"));

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
//        Mask.asciiMask(":/?#[]@!$&'()*+,;=");
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