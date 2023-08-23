package ppl.common.utils;

import ppl.common.utils.command.*;

public class Main {
    public static void main(String[] args) {
        CommandArguments arguments = CommandArguments.newBuilder()
                .addArgument(ValueOptionArgument.requiredIdentity("host", 'h'))
                .addArgument(ToggleOptionArgument.toggle("enabled", 'e'))
                .addArgument(PositionArgument.newBuilder("config").build())
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
