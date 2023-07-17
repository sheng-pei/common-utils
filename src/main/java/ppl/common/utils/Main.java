package ppl.common.utils;

import ppl.common.utils.argument.collector.Collectors;
import ppl.common.utils.command.Command;
import ppl.common.utils.command.CommandArguments;
import ppl.common.utils.command.Option;
import ppl.common.utils.command.Position;
import ppl.common.utils.string.Strings;

import java.util.Arrays;


public class Main {
    public static void main(String[] args) {
        CommandArguments arguments = CommandArguments.newBuilder()
                .addArgument(Option.newBuilder("hello", 'h').map(s -> s, Option.ref()).build())
                .addArgument(Option.toggle('t'))
                .addArgument(Option.toggle('w'))
                .addArgument(Option.toggle('z'))
                .addArgument(Option.toggle("toggle", 'm'))
                .addArgument(Option.newBuilder("world",
                                Arrays.asList("world1", "world2"),
                                Arrays.asList('o', 'r'))
                        .split(s -> Arrays.stream(Strings.split(s, ",")))
                        .map(Integer::parseInt, Option.ref())
                        .collect(Collectors.list(), Option.ref())
                        .build())
                .addArgument(Position.newBuilder("position").collect().build())
                .build();
        Command command = new Command(arguments);
        command.init(args);
        System.out.println(command.get("world"));
    }
}
