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
import ppl.common.utils.reflect.resolvable.ParameterizedTypeResolvable;
import ppl.common.utils.reflect.resolvable.Resolvable;
import ppl.common.utils.reflect.resolvable.Resolvables;
import ppl.common.utils.reflect.type.InternalParameterizedType;

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
//        ParameterizedType pt = (ParameterizedType) field.getGenericType();
//
        ParameterizedType pt = (ParameterizedType) LL.A.C.class.getGenericSuperclass();
        pt = (ParameterizedType) pt.getActualTypeArguments()[0];
        pt = (ParameterizedType) pt.getOwnerType();
        pt = (ParameterizedType) pt.getOwnerType();
        System.out.println(((TypeVariable) pt.getActualTypeArguments()[0]).getGenericDeclaration());

//        ResolvableType rt = ResolvableType.forField(field);
//        System.out.println(rt.getSuperType().getGeneric(0).getSuperType().resolve());
//        System.out.println(rt.getSuperType().getGeneric(0).getSuperType().getGeneric(0).resolve());

//        ParameterizedTypeResolvable ptr = Resolvables.getParameterizedTypeResolvable(pt);
//        ParameterizedTypeResolvable pptr = (ParameterizedTypeResolvable) ptr.getParent();
//        ParameterizedTypeResolvable ppptr = (ParameterizedTypeResolvable) pptr.getParent();
//        System.out.println(((ClassResolvable) ((ParameterizedTypeResolvable) (((ParameterizedTypeResolvable) pptr.getGeneric(0)).getOwner())).getGeneric(0)).getType());
//        System.out.println(((ClassResolvable) ppptr.getGeneric(0)).getType());

//        LL<Object>.A<String>.II<String> ii = LL.c.a(LL.iii);
    }

    public static class LL<I> {
        public class A<Y> {

            public class II<I> {
            }

            public class III<X> extends II<I> {
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

            public class C<X> extends B<III<Y>> {
            }

            public class D<T, V extends C<T>> extends C<B<C<T>>> {

            }

        }

        public static LL<Object> ll = new LL<>();
        public static LL<Object>.A<String> s = ll.new A<>();
        public static LL<Object>.A<Integer> a = ll.new A<>();
        public static LL<Object>.A<String>.III<String> iii = s.new III<>();
        public static LL<Object>.A<Integer>.C<String> c = a.new C<String>();
    }

}