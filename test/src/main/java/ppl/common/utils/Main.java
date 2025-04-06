package ppl.common.utils;

import ppl.common.utils.attire.proxy.Proxies;
import ppl.common.utils.attire.proxy.server.ServerInvocationHandler;
import ppl.common.utils.attire.proxy.server.initializer.BaseHttpServer;
import ppl.common.utils.attire.proxy.server.initializer.ServerCallInitializer;
import ppl.common.utils.attire.proxy.server.param.ServerJsonBodyParameterInterceptor;
import ppl.common.utils.attire.proxy.server.param.ServerQueryStatefulParameterInterceptor;
import ppl.common.utils.http.Clients;
import ppl.common.utils.json.jackson.JsonUtils;
import ppl.common.utils.string.Strings;

import java.io.Reader;
import java.io.StringReader;

public class Main {
    public static void main(String[] args) {
        System.out.println(JsonUtils.write("hello world."));
        System.out.println(JsonUtils.write("hello world."));
        System.out.println(JsonUtils.write("hello world."));
        System.out.println(JsonUtils.write("hello world."));
        System.out.println(JsonUtils.write("hello world."));
        System.out.println(JsonUtils.write("hello world."));
        Reader reader = new StringReader("aaaaaaaaaaaaaaaaaaaaaaajjjjjjjjjjjjjjjjjjjjbbbbbbbb");
        System.out.println(Strings.truncate(reader, 15));
//        ServerCallInitializer initializer = new ServerCallInitializer();
//        initializer.addServer(new BaseHttpServer("test", "1.0", "localhost:18080"));
//        ServerInvocationHandler handler = ServerInvocationHandler.builder()
//                .putConnector("default", Clients.create())
//                .setDefault("default")
//                .setCallInitializer(initializer)
//                .addRequestParameterInterceptor(new ServerQueryStatefulParameterInterceptor())
//                .addRequestBodyInterceptor(new ServerJsonBodyParameterInterceptor())
//                .build();
//        Test test = (Test) Proxies.create(new Class[] {Test.class}, handler);
//        test.toString();
//        Vo vo = new Vo();
//        vo.setId(1L);
//        test.json1("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaallllllllllllllllllllllllllllkkkkkkkkkkkkkkkkkkkkkkkkkkkkk", vo);
    }
}
