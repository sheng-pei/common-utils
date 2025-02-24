package ppl.common.utils;

import ppl.common.utils.ext.Exts;
import ppl.common.utils.json.jackson.JsonUtils;
import ppl.common.utils.string.trie.Trie;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println(Exts.DEFAULT_EXTS.parseExt("a.tar.gz"));
        System.out.println(Exts.DEFAULT_EXTS.parseExt("a.Rar"));
        System.out.println(Exts.DEFAULT_EXTS.parseExt("prepin.12"));
        System.out.println(Exts.DEFAULT_EXTS.parseExt("a.doCx"));
        System.out.println(Exts.DEFAULT_EXTS.parseExt("a.s"));
        System.out.println(Exts.DEFAULT_EXTS.parseExt("a.S"));

        System.out.println(JsonUtils.pretty("{\"components\":[{\"componentCode\":\"Hadoop.hdfs_namenode;Hadoop,yarn_nodemanager\",\"componentName\":\"计算节点\",\"widgets\":[{\"defaultValue\":true,\"field\":\"是否授权\",\"fieldType\":\"radio\",\"value\":true,\"widgetType\":3},{\"defaultValue\":\"计算节点\",\"field\":\"显示名称\",\"fieldType\":\"input\",\"value\":\"计算节点\",\"widgetType\":3},{\"defaultValue\":30,\"field\":\"容量限制\",\"fieldType\":\"input\",\"value\":30,\"widgetType\":2},{\"defaultValue\":34,\"field\":\"容量/版本\",\"fieldType\":\"input\",\"value\":34,\"widgetType\":2}]},{\"componentCode\":\"DTApp.Batch\",\"componentId\":\"rdos\",\"componentName\":\"离线计算开发套件\",\"widgets\":[{\"defaultValue\":true,\"field\":\"是否授权\",\"fieldType\":\"radio\",\"value\":true,\"widgetType\":3},{\"defaultValue\":\"离线计算\",\"field\":\"显示名称\",\"fieldType\":\"input\",\"value\":\"离线计算\",\"widgetType\":3},{\"defaultValue\":34,\"field\":\"容量/版本\",\"fieldType\":\"input\",\"value\":34,\"widgetType\":2},{\"defaultValue\":2,\"field\":\"容量限制\",\"fieldType\":\"input\",\"value\":2,\"widgetType\":2},{\"children\":[{\"defaultValue\":true,\"field\":\"数据源\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"数据开发\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"运维中心\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"数据地图\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"项目管理\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"数据模型\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3}],\"defaultValue\":true,\"field\":\"功能模块\",\"fieldType\":\"checkboxGroup\",\"value\":true,\"widgetType\":3},{\"defaultValue\":10000,\"effectApi\":\"batchHiveTableInfo/ddlCreateTable;batchHiveTableInfo/createTable\",\"field\":\"表数量\",\"fieldType\":\"input\",\"proofImpl\":\"batchHiveTableInfo/capableOfCreate\",\"value\":10000,\"widgetType\":1},{\"defaultValue\":10000,\"effectApi\":\"batchTask/addOrUpdateTask\",\"field\":\"任务数\",\"fieldType\":\"input\",\"proofImpl\":\"batchTask/capableOfCreate\",\"value\":10000,\"widgetType\":1}]},{\"componentCode\":\"DTApp.Stream\",\"componentId\":\"stream\",\"componentName\":\"实时计算开发套件\",\"widgets\":[{\"defaultValue\":true,\"field\":\"是否授权\",\"fieldType\":\"radio\",\"value\":true,\"widgetType\":3},{\"defaultValue\":\"StreamIDE\",\"field\":\"显示名称\",\"fieldType\":\"input\",\"value\":\"StreamIDE\",\"widgetType\":3},{\"defaultValue\":2,\"field\":\"容量限制\",\"fieldType\":\"input\",\"value\":2,\"widgetType\":2},{\"defaultValue\":34,\"field\":\"容量/版本\",\"fieldType\":\"input\",\"value\":34,\"widgetType\":2},{\"children\":[{\"defaultValue\":true,\"field\":\"数据源\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"数据开发\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"任务运维\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"项目管理\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3}],\"defaultValue\":true,\"field\":\"功能模块\",\"fieldType\":\"checkboxGroup\",\"value\":true,\"widgetType\":3}]},{\"componentCode\":\"DTApp.Analytics\",\"componentId\":\"analyticsEngine\",\"componentName\":\"分析引擎\",\"widgets\":[{\"defaultValue\":true,\"field\":\"是否授权\",\"fieldType\":\"radio\",\"value\":true,\"widgetType\":3},{\"defaultValue\":\"Analytics\",\"field\":\"显示名称\",\"fieldType\":\"input\",\"value\":\"Analytics\",\"widgetType\":3},{\"defaultValue\":2,\"field\":\"容量限制\",\"fieldType\":\"input\",\"value\":2,\"widgetType\":2},{\"defaultValue\":34,\"field\":\"容量/版本\",\"fieldType\":\"input\",\"value\":34,\"widgetType\":2},{\"defaultValue\":20,\"effectApi\":\"dataBaseManagement/createTable;dataBaseManagement/ddlCreateTable\",\"field\":\"表数量限制\",\"fieldType\":\"input\",\"proofImpl\":\"dataBaseManagement/capableOfCreate\",\"value\":20,\"widgetType\":1},{\"defaultValue\":10,\"effectApi\":\"\",\"field\":\"并发查询限制\",\"fieldType\":\"input\",\"proofImpl\":\"\",\"value\":10,\"widgetType\":1},{\"children\":[{\"defaultValue\":true,\"field\":\"数据库管理\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"表管理\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3}],\"defaultValue\":true,\"field\":\"功能模块\",\"fieldType\":\"checkboxGroup\",\"value\":true,\"widgetType\":3}]},{\"componentCode\":\"DTApp.Valid\",\"componentId\":\"dataQuality\",\"componentName\":\"数据质量\",\"widgets\":[{\"defaultValue\":true,\"field\":\"是否授权\",\"fieldType\":\"radio\",\"value\":true,\"widgetType\":3},{\"defaultValue\":\"Valid\",\"field\":\"显示名称\",\"fieldType\":\"input\",\"value\":\"Valid\",\"widgetType\":3},{\"defaultValue\":2,\"field\":\"容量限制\",\"fieldType\":\"input\",\"value\":2,\"widgetType\":2},{\"defaultValue\":34,\"field\":\"容量/版本\",\"fieldType\":\"input\",\"value\":34,\"widgetType\":2},{\"defaultValue\":2000,\"effectApi\":\"monitor/add\",\"field\":\"规则数\",\"fieldType\":\"input\",\"proofImpl\":\"monitor/capableOfCreate\",\"value\":2000,\"widgetType\":1},{\"children\":[{\"defaultValue\":true,\"field\":\"概览\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"任务查询\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"规则配置\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":false,\"field\":\"实时校验\",\"fieldType\":\"checkbox\",\"value\":false,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"数据源管理\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"项目管理\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3}],\"defaultValue\":true,\"field\":\"功能模块\",\"fieldType\":\"checkboxGroup\",\"value\":true,\"widgetType\":3}]},{\"componentCode\":\"DTApp.API\",\"componentId\":\"dataApi\",\"componentName\":\"数据API\",\"widgets\":[{\"defaultValue\":true,\"field\":\"是否授权\",\"fieldType\":\"radio\",\"value\":true,\"widgetType\":3},{\"defaultValue\":\"API\",\"field\":\"显示名称\",\"fieldType\":\"input\",\"value\":\"API\",\"widgetType\":3},{\"defaultValue\":2,\"field\":\"容量限制\",\"fieldType\":\"input\",\"value\":2,\"widgetType\":2},{\"defaultValue\":34,\"field\":\"容量/版本\",\"fieldType\":\"input\",\"value\":34,\"widgetType\":2},{\"defaultValue\":500,\"effectApi\":\"apis/saveOrUpdateApiInfo\",\"field\":\"API总数\",\"fieldType\":\"input\",\"proofImpl\":\"apis/capableOfCreate\",\"value\":500,\"widgetType\":1},{\"children\":[{\"defaultValue\":true,\"field\":\"概览\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"API市场\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"我的API\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"API管理\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"授权与安全\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"数据源管理\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"项目管理\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3}],\"defaultValue\":true,\"field\":\"功能模块\",\"fieldType\":\"checkboxGroup\",\"widgetType\":3},{\"componentCode\":\"DTApp.tagEngine\",\"componentId\":\"tagEngine\",\"componentName\":\"标签引擎\",\"widgets\":[{\"defaultValue\":true,\"field\":\"是否授权\",\"fieldType\":\"radio\",\"value\":true,\"widgetType\":3},{\"defaultValue\":\"API\",\"field\":\"显示名称\",\"fieldType\":\"input\",\"value\":\"tagEngine\",\"widgetType\":3},{\"defaultValue\":2,\"field\":\"容量限制\",\"fieldType\":\"input\",\"value\":2,\"widgetType\":2},{\"defaultValue\":34,\"field\":\"容量/版本\",\"fieldType\":\"input\",\"value\":34,\"widgetType\":2},{\"children\":[{\"defaultValue\":true,\"field\":\"数据源管理\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"项目管理\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"实体管理\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"标签中心\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"群组分析\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"数据服务\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3}],\"defaultValue\":true,\"field\":\"功能模块\",\"fieldType\":\"checkboxGroup\",\"widgetType\":3}]}]},{\"componentCode\":\"DTApp.Science\",\"componentId\":\"science\",\"componentName\":\"算法平台开发套件\",\"widgets\":[{\"defaultValue\":true,\"field\":\"是否授权\",\"fieldType\":\"radio\",\"value\":true,\"widgetType\":3},{\"defaultValue\":\"science\",\"field\":\"显示名称\",\"fieldType\":\"input\",\"value\":\"DataScienceIDE\",\"widgetType\":3},{\"defaultValue\":2,\"field\":\"容量限制\",\"fieldType\":\"input\",\"value\":2,\"widgetType\":2},{\"defaultValue\":34,\"field\":\"容量/版本\",\"fieldType\":\"input\",\"value\":34,\"widgetType\":2},{\"children\":[{\"defaultValue\":true,\"field\":\"算法实验\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"运维中心\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"数据管理\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"项目管理\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3}],\"defaultValue\":true,\"field\":\"功能模块\",\"fieldType\":\"checkboxGroup\",\"value\":true,\"widgetType\":3}]},{\"componentCode\":\"DTApp.Assets\",\"componentId\":\"dataAssets\",\"componentName\":\"数据资产平台\",\"widgets\":[{\"defaultValue\":true,\"field\":\"是否授权\",\"fieldType\":\"radio\",\"value\":true,\"widgetType\":3},{\"defaultValue\":\"dataAsset\",\"field\":\"显示名称\",\"fieldType\":\"input\",\"value\":\"dataAsset\",\"widgetType\":3},{\"defaultValue\":2,\"field\":\"容量限制\",\"fieldType\":\"input\",\"value\":2,\"widgetType\":2},{\"defaultValue\":\"1.0\",\"field\":\"容量/版本\",\"fieldType\":\"input\",\"value\":\"1.0\",\"widgetType\":2},{\"children\":[{\"defaultValue\":true,\"field\":\"数据地图\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"元数据管理\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"我的数据\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3},{\"defaultValue\":true,\"field\":\"安全管理\",\"fieldType\":\"checkbox\",\"value\":true,\"widgetType\":3}],\"defaultValue\":true,\"field\":\"功能模块\",\"fieldType\":\"checkboxGroup\",\"value\":true,\"widgetType\":3}]}],\"product\":\"DTinsight（基础版）\",\"productCode\":\"DTinsight\"}"));

//        String random = "LT-wZ1dnMhT2DPCmkqtVCI+";
//        ECPublicKey publicKey = BCECUtils.publicKey("04bd2df35b56122e520452083a9c8e21861a9325ebe32851be97317e6bbe15e88005c3bc077d07a90107150b66a250b697dfbbe2600026eb2abc5d10b24357b108");
//        ECPrivateKey privateKey = BCECUtils.privateKey("848830bddbf9c87627a356bb3007ffb65e4af39e60df475340220a6f187c22bf");
//
//        String salt = "$2y$10$" + Base64.getEncoder().encodeToString(padding("baizhi@dtstack.com".getBytes(), ' ', 16));
//        String digest = BCrypt.hashpw("DT#passw0rd2022".getBytes(), salt);
//        System.out.println(digest);
//
////        MessageDigestPasswordEncoder encoder = new MessageDigestPasswordEncoder("SHA-256");
//        String body = random + "#" + digest + "#" + "baizhi@dtstack.com";
//        File file = new File("body");
//        try (OutputStream os = new FileOutputStream(file)) {
//            os.write(Bytes.hex(ECUtils.encrypt(publicKey, body.getBytes())).getBytes());
//        }
//
//        System.out.println(URLEncoder.encode("http://localhost:8080"));

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
//        Mask.asciiMask(":/?#[]@!$&'()*+,;=");
    }

    private static byte[] padding(byte[] s, char pad, int size) {
        if (s.length < size) {
            byte[] s1 = new byte[size];
            Arrays.fill(s1, (byte) pad);
            System.arraycopy(s, 0, s1, 0, s.length);
            return s1;
        }
        return s;
    }

}