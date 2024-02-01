package ppl.common.utils;

import ppl.common.utils.command.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        CommandArguments arguments = CommandArguments.newBuilder()
                .addArgument(ValueOptionArgument.requiredIdentity("host", 'h'))
                .addArgument(ToggleOptionArgument.toggle("enabled", 'e'))
                .addArgument(PositionArgument.newBuilder("config").build(PositionArgument.defToCanonical()))
                .build();
        CommandParser parser = new CommandParser(arguments);
        parser.parse(args).forEach(System.out::println);
        System.out.println(arguments.get("-h"));
        Command command = new Command(arguments);
        command.init(args);
        System.out.println(command.get("host"));
        System.out.println(command.get("config"));
    }
}
