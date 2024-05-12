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
import ppl.common.utils.reflect.type.InternalParameterizedType;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

        ParameterizedType cType = (ParameterizedType) LL.class.getDeclaredField("c").getGenericType();
        ParameterizedType owner = (ParameterizedType) cType.getOwnerType();
        System.out.println(owner);
        Class<?> bType = (Class<?>) ((Class<?>) cType.getRawType()).getGenericSuperclass();
        System.out.println(bType.getGenericSuperclass());
//        System.out.println(bType.getRawType());
//        if (bType.getActualTypeArguments()[0].equals(((Class<?>) cType.getRawType()).getTypeParameters()[0])) {
//            System.out.println(cType.getActualTypeArguments()[0]);
//        }


//        ParameterizedType pt = (ParameterizedType) LL.A.III.class.getGenericSuperclass();
//        System.out.println(LL.A.III.class.getGenericSuperclass());
//        System.out.println(LL.A.II.class.getEnclosingClass().getTypeParameters()[0]);
//        System.out.println(LL.A.II.class.getEnclosingClass().getTypeParameters()[0].equals(((ParameterizedType) pt.getOwnerType()).getActualTypeArguments()[0]));
//        System.out.println(((ParameterizedType) LL.A.III.class.getGenericSuperclass()).getActualTypeArguments()[0].equals(LL.A.II.class.getTypeParameters()[0]));
    }

    public static class LL<I> {
        public class A<Y> {
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

        }

        public static LL<String>.A<String>.C<String> c;
    }

}