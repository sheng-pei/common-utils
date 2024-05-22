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

        Field field = LL.class.getDeclaredField("c");
        ParameterizedType pt = (ParameterizedType) field.getGenericType();
        ParameterizedTypeResolvable ptr = Resolvables.getParameterizedTypeResolvable(pt);
        System.out.println(((GenericResolvable) ptr.getGeneric(0)).getType());

        ParameterizedTypeResolvable pptr = (ParameterizedTypeResolvable) ptr.getParent();
        GenericResolvable g = (GenericResolvable) pptr.getGeneric(0);
        System.out.println(g.getType());
        System.out.println(((GenericResolvable) g.getParent()).getType());
        System.out.println(((GenericResolvable) ((GenericResolvable) g.getParent()).getGeneric(0)).getType());

        GenericResolvable a = (GenericResolvable) g.getOwner();
        System.out.println(a.getType());
        System.out.println(((GenericResolvable) a.getGeneric(0)).getType());

        GenericResolvable u = (GenericResolvable) g.getInterfaces()[0];
        System.out.println(u.getType());
        System.out.println(((GenericResolvable) u.getOwner()).getType());
        System.out.println(((GenericResolvable) u.getOwner()).getOwner());

        System.out.println(LL.class.getTypeParameters()[0].getBounds()[0]);
    }

    public interface U {

    }

    public interface V extends U {

    }

    public static class LL<I> {
        public void ll(I i) {

        }

        public class A<Y> {
            public Y y;
            public void a(Y y) {

            }
            public class II<K> {
                public void ii(K k) {
                }
            }

            public class III<X> extends II<I> implements V {
                private I i;
                public I b() {
                    return i;
                }
            }

            public class B<X> extends III<X> {

                public B() {
                }

                public X a(X y) {
                    return null;
                }
            }

            public class C<X> extends B<III> {
            }

            public class D<T, V extends C<T>> extends C<B<C<T>>> {

            }

        }

        public static LL<Number> ll = new LL<>();
        public static LL<Number>.A<String> s = ll.new A<>();
        public static LL<Number>.A<Integer> a = ll.new A<>();
        public static LL<Number>.A<String>.III<String> iii = s.new III<>();
        public static LL<Number>.A<Integer>.C<String> c = a.new C<String>();
    }

}