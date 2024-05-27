package ppl.common.utils;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import ppl.common.utils.argument.argument.value.collector.ExCollectors;
//import ppl.common.utils.command.*;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.function.Function;

//import ppl.common.utils.reflect.resolvable.ParameterizedResolvable;
//import ppl.common.utils.reflect.resolvable.ParameterizedResolvable;

import ppl.common.utils.reflect.resolvable.GenericResolvable;
import ppl.common.utils.reflect.resolvable.ParameterizedTypeResolvable;
import ppl.common.utils.reflect.resolvable.Resolvables;
import ppl.common.utils.reflect.resolvable.TypeVariableResolvable;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Main {
//    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException, IOException, NoSuchFieldException, InstantiationException, IllegalAccessException, ClassNotFoundException, ExecutionException {
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

//        Field field = LL.class.getDeclaredField("c");
//        GenericResolvable gr = Resolvables.getParameterizedTypeResolvable((ParameterizedType) field.getGenericType());
//        gr = (GenericResolvable) gr.getParent();
//        System.out.println(gr.getType());
//        System.out.println(((GenericResolvable) ((TypeVariableResolvable) gr.getGeneric(0)).getBound(0)).getType());
    }

}