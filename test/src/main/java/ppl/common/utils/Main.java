package ppl.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.compress.Archive;
import ppl.common.utils.compress.Archives;

import java.nio.file.Paths;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
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
        try (Archive archive = Archives.open(Paths.get(args[0]))) {
            archive.decompressTo(Paths.get(args[1]).toFile());
        }
    }
}