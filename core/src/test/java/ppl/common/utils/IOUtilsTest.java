package ppl.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import ppl.common.utils.exception.UnreachableCodeException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@ExtendWith(MockitoExtension.class)
public class IOUtilsTest {

    private static final byte[] DATA = ("# 公共信息\n" +
            "## Header鉴权方式:\n" +
            "### 方式一：AK/SK签名加密方式 请求示例：\n" +
            "X-Auth-Key：{APP Key}\n" +
            "X-Auth-ActionId：{API Id}\n" +
            "X-Auth-Signature：{生成的签名}\n" +
            "X-Auth-Timestamp：{时间戳}\n" +
            "\n" +
            "### 方式二：TOKEN加密方式 请求示例：\n" +
            "API-TOKEN： {API-TOKEN}\n" +
            "\n" +
            "# 市场管理\n" +
            "## API信息\n" +
            "#### pgApi\n" +
            "##### 基础信息\n" +
            "API 名称：pgApi\n" +
            "API 描述：\n" +
            "支持格式：JSON\n" +
            "请求协议：HTTP/HTTPS\n" +
            "请求方式：POST\n" +
            "超时时间：3\n" +
            "API ID：65\n" +
            "传输加密：不加密\n" +
            "支持调用次数：无限制\n" +
            "支持调用周期：无限制\n" +
            "调用 URL：http://172.16.84.114:8086/api/gateway/pgApi\n" +
            "API-TOKEN：3060C5CFD167805A616C72671F347C4F\n" +
            "\n" +
            "##### 参数信息\n" +
            "**输入参数**\n" +
            "\n" +
            "| 参数名 | 数据类型 | 必填 | 行级权限 | 说明 |\n" +
            "| -- | -- | -- | -- | -- |\n" +
            "| id | int4 | 是 | 否 |  |\n" +
            "| name1 | varchar | 是 | 否 |  |\n" +
            "\n" +
            "\n" +
            "##### 请求返回示例\n" +
            "##### Header鉴权方式：\n" +
            "方式一：AK/SK签名加密方式\n" +
            "**Request URL**\n" +
            "http(s)：//调用URL\n" +
            "**Headers**\n" +
            "X-Auth-Key：{APP Key}\n" +
            "X-Auth-ActionId：{API Id}\n" +
            "X-Auth-Signature：{生成的签名}\n" +
            "X-Auth-Timestamp：{时间戳}\n" +
            "**Body**\n" +
            "```json\n" +
            "{\n" +
            "\t\"inFields\":{\n" +
            "\t\t\"id\":\"\",\n" +
            "\t\t\"name1\":\"\"\n" +
            "\t}\n" +
            "}\n" +
            "```\n" +
            "方式二：TOKEN加密方式\n" +
            "**Request URL**\n" +
            "http(s)：//调用URL\n" +
            "**Headers**\n" +
            "API-TOKEN： {API-TOKEN}\n" +
            "**Body**\n" +
            "```json\n" +
            "{\n" +
            "\t\"inFields\":{\n" +
            "\t\t\"id\":\"\",\n" +
            "\t\t\"name1\":\"\"\n" +
            "\t}\n" +
            "}\n" +
            "```\n" +
            "**返回结果：**\n" +
            "返回JSON样例\n" +
            "```json\n" +
            "暂无样例\n" +
            "```\n" +
            "— — — — — — — — —\n" +
            "## API信息\n" +
            "#### pgQuery\n" +
            "##### 基础信息\n" +
            "API 名称：pgQuery\n" +
            "API 描述：\n" +
            "支持格式：JSON\n" +
            "请求协议：HTTP/HTTPS\n" +
            "请求方式：POST\n" +
            "超时时间：3\n" +
            "API ID：67\n" +
            "传输加密：不加密\n" +
            "支持调用次数：无限制\n" +
            "支持调用周期：无限制\n" +
            "调用 URL：http://172.16.84.114:8086/api/gateway/pgQuery\n" +
            "API-TOKEN：D208CEF7D4534D65DE654311D9CE02EC\n" +
            "\n" +
            "##### 参数信息\n" +
            "**分页参数**\n" +
            "\n" +
            "| 参数名 | 数据类型 | 必填 | 说明 |\n" +
            "| -- | -- | -- | -- |\n" +
            "| pageNo | VARCHAR | 否 | 页码 |\n" +
            "| pageSize | VARCHAR | 否 | 每页记录数 |\n" +
            "\n" +
            "\n" +
            "##### 请求返回示例\n" +
            "##### Header鉴权方式：\n" +
            "方式一：AK/SK签名加密方式\n" +
            "**Request URL**\n" +
            "http(s)：//调用URL\n" +
            "**Headers**\n" +
            "X-Auth-Key：{APP Key}\n" +
            "X-Auth-ActionId：{API Id}\n" +
            "X-Auth-Signature：{生成的签名}\n" +
            "X-Auth-Timestamp：{时间戳}\n" +
            "**Body**\n" +
            "```json\n" +
            "{\n" +
            "\t\"pageNo\":1,\n" +
            "\t\"pageSize\":null,\n" +
            "\t\"inFields\":{}\n" +
            "}\n" +
            "```\n" +
            "方式二：TOKEN加密方式\n" +
            "**Request URL**\n" +
            "http(s)：//调用URL\n" +
            "**Headers**\n" +
            "API-TOKEN： {API-TOKEN}\n" +
            "**Body**\n" +
            "```json\n" +
            "{\n" +
            "\t\"pageNo\":1,\n" +
            "\t\"pageSize\":null,\n" +
            "\t\"inFields\":{}\n" +
            "}\n" +
            "```\n" +
            "**返回结果：**\n" +
            "返回JSON样例\n" +
            "```json\n" +
            "暂无样例\n" +
            "```\n" +
            "— — — — — — — — —\n" +
            "## API信息\n" +
            "#### pgUpdate\n" +
            "##### 基础信息\n" +
            "API 名称：pgUpdate\n" +
            "API 描述：\n" +
            "支持格式：JSON\n" +
            "请求协议：HTTP/HTTPS\n" +
            "请求方式：POST\n" +
            "超时时间：3\n" +
            "API ID：69\n" +
            "传输加密：不加密\n" +
            "支持调用次数：无限制\n" +
            "支持调用周期：无限制\n" +
            "调用 URL：http://172.16.84.114:8086/api/gateway/pgUpdate\n" +
            "API-TOKEN：D4A0C6A34D29EB9E3D5D26589D954330\n" +
            "\n" +
            "##### 参数信息\n" +
            "**输入参数**\n" +
            "\n" +
            "| 参数名 | 数据类型 | 必填 | 行级权限 | 说明 |\n" +
            "| -- | -- | -- | -- | -- |\n" +
            "| name1 | varchar | 是 | 否 |  |\n" +
            "| id | int4 | 是 | 否 |  |\n" +
            "\n" +
            "\n" +
            "##### 请求返回示例\n" +
            "##### Header鉴权方式：\n" +
            "方式一：AK/SK签名加密方式\n" +
            "**Request URL**\n" +
            "http(s)：//调用URL\n" +
            "**Headers**\n" +
            "X-Auth-Key：{APP Key}\n" +
            "X-Auth-ActionId：{API Id}\n" +
            "X-Auth-Signature：{生成的签名}\n" +
            "X-Auth-Timestamp：{时间戳}\n" +
            "**Body**\n" +
            "```json\n" +
            "{\n" +
            "\t\"inFields\":{\n" +
            "\t\t\"id\":\"\",\n" +
            "\t\t\"name1\":\"\"\n" +
            "\t}\n" +
            "}\n" +
            "```\n" +
            "方式二：TOKEN加密方式\n" +
            "**Request URL**\n" +
            "http(s)：//调用URL\n" +
            "**Headers**\n" +
            "API-TOKEN： {API-TOKEN}\n" +
            "**Body**\n" +
            "```json\n" +
            "{\n" +
            "\t\"inFields\":{\n" +
            "\t\t\"id\":\"\",\n" +
            "\t\t\"name1\":\"\"\n" +
            "\t}\n" +
            "}\n" +
            "```\n" +
            "**返回结果：**\n" +
            "返回JSON样例\n" +
            "```json\n" +
            "暂无样例\n" +
            "```\n" +
            "— — — — — — — — —\n" +
            "## API信息\n" +
            "#### test\n" +
            "##### 基础信息\n" +
            "API 名称：test\n" +
            "API 描述：\n" +
            "支持格式：JSON\n" +
            "请求协议：HTTP/HTTPS\n" +
            "请求方式：GET\n" +
            "超时时间：3\n" +
            "API ID：97\n" +
            "传输加密：不加密\n" +
            "支持调用次数：无限制\n" +
            "支持调用周期：无限制\n" +
            "调用 URL：http://172.16.84.114:8086/api/gateway/test\n" +
            "API-TOKEN：ECE2513B8B64FCBDDED2960F7FB1EBD1\n" +
            "\n" +
            "##### 参数信息\n" +
            "\n" +
            "##### 请求返回示例\n" +
            "##### Header鉴权方式：\n" +
            "方式一：AK/SK签名加密方式\n" +
            "**Request URL**\n" +
            "http(s)：//调用URL\n" +
            "**Headers**\n" +
            "X-Auth-Key：{APP Key}\n" +
            "X-Auth-ActionId：{API Id}\n" +
            "X-Auth-Signature：{生成的签名}\n" +
            "X-Auth-Timestamp：{时间戳}\n" +
            "**Body**\n" +
            "```json\n" +
            "{}\n" +
            "```\n" +
            "方式二：TOKEN加密方式\n" +
            "**Request URL**\n" +
            "http(s)：//调用URL\n" +
            "**Headers**\n" +
            "API-TOKEN： {API-TOKEN}\n" +
            "**Body**\n" +
            "```json\n" +
            "{}\n" +
            "```\n" +
            "**返回结果：**\n" +
            "正常返回示例\n" +
            "```json\n" +
            "暂无样例\n" +
            "```\n" +
            "错误返回示例\n" +
            "```json\n" +
            "暂无样例\n" +
            "```\n" +
            "**错误码**\n" +
            "暂无数据\n" +
            "— — — — — — — — —\n" +
            "## API信息\n" +
            "#### test1\n" +
            "##### 基础信息\n" +
            "API 名称：test1\n" +
            "API 描述：\n" +
            "支持格式：JSON\n" +
            "请求协议：HTTP/HTTPS\n" +
            "请求方式：GET\n" +
            "超时时间：30\n" +
            "API ID：99\n" +
            "传输加密：不加密\n" +
            "支持调用次数：无限制\n" +
            "支持调用周期：无限制\n" +
            "调用 URL：http://172.16.84.114:8086/api/gateway/test1?l=\n" +
            "API-TOKEN：BFCBCB7954B6F0FF568D09C380D4B057\n" +
            "\n" +
            "##### 参数信息\n" +
            "**输入参数**\n" +
            "\n" +
            "| 参数名 | 参数位置 | 数据类型 | 必填 | 说明 |\n" +
            "| -- | -- | -- | -- | -- |\n" +
            "| l | query | long | 是 |  |\n" +
            "\n" +
            "\n" +
            "##### 请求返回示例\n" +
            "##### Header鉴权方式：\n" +
            "方式一：AK/SK签名加密方式\n" +
            "**Request URL**\n" +
            "http(s)：//调用URL\n" +
            "**Headers**\n" +
            "X-Auth-Key：{APP Key}\n" +
            "X-Auth-ActionId：{API Id}\n" +
            "X-Auth-Signature：{生成的签名}\n" +
            "X-Auth-Timestamp：{时间戳}\n" +
            "**Body**\n" +
            "```json\n" +
            "{}\n" +
            "```\n" +
            "方式二：TOKEN加密方式\n" +
            "**Request URL**\n" +
            "http(s)：//调用URL\n" +
            "**Headers**\n" +
            "API-TOKEN： {API-TOKEN}\n" +
            "**Body**\n" +
            "```json\n" +
            "{}\n" +
            "```\n" +
            "**返回结果：**\n" +
            "正常返回示例\n" +
            "```json\n" +
            "暂无样例\n" +
            "```\n" +
            "错误返回示例\n" +
            "```json\n" +
            "暂无样例\n" +
            "```\n" +
            "**错误码**\n" +
            "暂无数据\n" +
            "— — — — — — — — —\n" +
            "## API信息\n" +
            "#### testexternal\n" +
            "##### 基础信息\n" +
            "API 名称：testexternal\n" +
            "API 描述：\n" +
            "支持格式：JSON\n" +
            "请求协议：HTTP/HTTPS\n" +
            "请求方式：GET\n" +
            "超时时间：3\n" +
            "API ID：127\n" +
            "传输加密：不加密\n" +
            "支持调用次数：无限制\n" +
            "支持调用周期：无限制\n" +
            "调用 URL：http://172.16.84.114:8086/api/gateway/testexternal\n" +
            "API-TOKEN：60A2F72FA0405E1DA69B58FE315D173A2940F09FF0F4F8F0E8EDF6C7AEAD0C49\n" +
            "\n" +
            "##### 参数信息\n" +
            "\n" +
            "##### 请求返回示例\n" +
            "##### Header鉴权方式：\n" +
            "方式一：AK/SK签名加密方式\n" +
            "**Request URL**\n" +
            "http(s)：//调用URL\n" +
            "**Headers**\n" +
            "X-Auth-Key：{APP Key}\n" +
            "X-Auth-ActionId：{API Id}\n" +
            "X-Auth-Signature：{生成的签名}\n" +
            "X-Auth-Timestamp：{时间戳}\n" +
            "**Body**\n" +
            "```json\n" +
            "{}\n" +
            "```\n" +
            "方式二：TOKEN加密方式\n" +
            "**Request URL**\n" +
            "http(s)：//调用URL\n" +
            "**Headers**\n" +
            "API-TOKEN： {API-TOKEN}\n" +
            "**Body**\n" +
            "```json\n" +
            "{}\n" +
            "```\n" +
            "**返回结果：**\n" +
            "正常返回示例\n" +
            "```json\n" +
            "暂无样例\n" +
            "```\n" +
            "错误返回示例\n" +
            "```json\n" +
            "暂无样例\n" +
            "```\n" +
            "**错误码**\n" +
            "暂无数据\n" +
            "— — — — — — — — —\n" +
            "# API 返回码\n" +
            "| 返回码状态 | 释义说明 |\n" +
            "| -- | -- |\n" +
            "| 1 | 成功 |\n" +
            "|  2 | server runtime error |\n" +
            "|  3 | 未定义异常 |\n" +
            "|  4 | 不支持本次请求，请查看操作文档 |\n" +
            "|  5 | token不合法 |\n" +
            "|  6 | 签名错误 |\n" +
            "|  7 | 未找到授权信息 |\n" +
            "|  8 | 请求头错误 |\n" +
            "|  100 | 无权限访问此API |\n" +
            "|  101 | 访问频繁，请稍后重试 |\n" +
            "|  102 | API 不可用 |\n" +
            "|  103 | 参数解析异常 |\n" +
            "|  104 | API查询超时 |\n" +
            "|  105 | API申请不可用 |\n" +
            "|  106 | API调用未在申请时间内或已超过申请次数 |\n" +
            "|  107 | 安全组限制访问 |\n" +
            "|  108 | 客户端ip获取失败 |\n" +
            "|  109 | API path错误 |\n" +
            "|  140 | 访问远程服务器出错 |\n" +
            "|  141 | 仅支持GET、POST、DELETE和PUT请求 |\n" +
            "|  143 | 请求方式错误 |\n" +
            "|  142 | 常量参数仅支持header和query |\n" +
            "|  144 | 存在必填参数缺失 |\n" +
            "|  145 | 该数据源不支持 |\n" +
            "|  146 | API配置为空 |\n" +
            "|  147 | SFTP配置为空 |\n" +
            "|  148 | 手动注册服务失败 |\n" +
            "|  149 | 删除服务失败 |\n" +
            "|  150 | RSA加密解密失败 |\n" +
            "|  151 | 请求参数解密失败，请检查加密信息 |\n" +
            "|  152 | 当前开启传输加密，缺少data参数 |\n" +
            "|  153 | 当前开启传输加密，缺少aesKey参数 |\n" +
            "|  154 | data参数不唯一 |\n" +
            "|  155 | aesKey参数不唯一 |\n" +
            "|  156 | contentType={%s}，不存在 |\n" +
            "|  157 | 没有可用的服务: {%s} |\n" +
            "|  158 | spring.redis.sentinel.nodes未配置 |\n" +
            "|  159 | {%s} 不匹配任何服务器! |\n" +
            "|  160 | redis哨兵项配置错误 |\n" +
            "|  161 | spring.redis.sentinel.nodes配置错误 |\n" +
            "|  162 | 密码解密错误，password=%s，%s |\n" +
            "|  163 | 不支持该类型 |\n" +
            "|  164 | 响应结果加密失败 |\n").getBytes(StandardCharsets.UTF_8);
    private static final int BUFFER_SIZE = 1024;

    @Mock(stubOnly = true)
    private InputStream is;

    @BeforeEach
    void setUp() {
        try {
            Mockito.lenient().when(is.read(ArgumentMatchers.any(byte[].class))).thenAnswer(new Answer<Object>() {
                private int current = 0;
                private int addUp = 0;
                private final int[] ints = new int[] {
                        199, 157, 674, 156, 573,
                        1016, 649, 310, 0, 98,
                        428, 83, 995, 1024, 444,
                        455, 932, 283
                };

                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    if (addUp >= DATA.length) {
                        return -1;
                    }

                    byte[] arg = invocation.getArgument(0, byte[].class);
                    int cnt = ints[current++];
                    System.arraycopy(DATA, addUp, arg, 0, cnt);
                    addUp += cnt;
                    return cnt;
                }
            });
        } catch (IOException e) {
            throw new UnreachableCodeException();
        }
    }

    @Test
    public void testCopy() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        IOUtils.copy(is, os,BUFFER_SIZE);
        Assertions.assertArrayEquals(DATA, os.toByteArray());
    }

}