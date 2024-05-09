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
import org.springframework.core.ResolvableType;
import ppl.common.utils.reflect.resolvable.ClassResolvable;
import ppl.common.utils.reflect.resolvable.Resolvable;
import ppl.common.utils.reflect.resolvable.Resolvables;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
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

//        Resolvable resolvable = Resolvables.getClassResolvable(D.class);
//        A<String>.III<Object> iii = A.c;
        System.out.println(((Class<?>) A.class.getDeclaredField("c").getGenericType()).getEnclosingClass());
    }

    public static class A<Y> {
        public class II<I> {
        }

        public class III<I> extends II<I> {
            public void a(I y) {

            }
        }

        public class B<X> extends III<Y> {
            public void a(Y y) {

            }
        }

        public class C<P> extends B {

        }

        public class D<T, V extends C<T>> extends C<B<C<T>>> {

        }

        public static A<String> a = new A<>();
        public static A.C c = a.new C<>();
    }

}