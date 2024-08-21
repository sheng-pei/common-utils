package ppl.common.utils;

import ppl.common.utils.argument.argument.value.collector.ExCollectors;
import ppl.common.utils.character.ascii.Mask;
import ppl.common.utils.command.*;
import ppl.common.utils.security.BCECUtils;
import ppl.common.utils.security.ECUtils;

import java.io.*;
import java.security.interfaces.ECPublicKey;
import java.util.Arrays;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) throws IOException {
        String random = "LT-+IRSJSmM3JzeGKiH5g1v";
        ECPublicKey publicKey = BCECUtils.publicKey("04bd2df35b56122e520452083a9c8e21861a9325ebe32851be97317e6bbe15e88005c3bc077d07a90107150b66a250b697dfbbe2600026eb2abc5d10b24357b108");
        File file = new File("body");
        OutputStream os = new FileOutputStream(file);
        os.write(ECUtils.encrypt(publicKey, (random + "$user$user@dtstack.com").getBytes()));
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

}