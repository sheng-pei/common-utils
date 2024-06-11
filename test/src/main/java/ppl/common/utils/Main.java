package ppl.common.utils;

import ppl.common.utils.argument.argument.value.collector.ExCollectors;
import ppl.common.utils.command.*;
import ppl.common.utils.net.URLDecoder;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) {
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
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.flip();
        System.out.println(URLDecoder.decode("%2B", Charset.defaultCharset()));
    }

}