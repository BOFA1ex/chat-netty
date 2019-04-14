package com.bofa.server.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bofa.exception.ChatErrorCode;
import com.bofa.exception.ChatException;
import com.bofa.protocol.command.Command;
import com.bofa.protocol.request.AbstractRequestPacket;
import com.bofa.protocol.request.LoginRequestPacket;
import com.bofa.protocol.response.AbstractResponsePacket;
import com.bofa.protocol.response.LoginResponsePacket;
import io.netty.util.CharsetUtil;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.util
 * 狗皮膏药式定义常量
 * @date 2019/4/4
 */
public class HttpUtil {

    static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static CloseableHttpClient createDefault() {
        return HttpClientBuilder.create().build();
    }

    private static final String DEFAULT_BASE_URL = "http://localhost:8088";

    private static final String CODE_SUCCESS = "200";

    private static final String CODE = "code";

    private static final String MESSAGE = "message";

    private static final String DATA = "data";

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String JSON_CONTENT_TYPE = "application/json;charset=utf-8";

    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded;charset=utf-8";

    /**
     * httpUtil invoke by RequestMethod.GET
     *
     * @param url    invoke http url
     * @param clazz  T class
     * @param params String[] args
     * @param <T>    T
     * @return
     */
    public static <T> T getData(String url, Class<T> clazz, String... params) {
        if (params == null || params.length == 0) {
            ChatException.throwChatException(ChatErrorCode.Parameter_com_null, null);
        }
        CloseableHttpClient httpClient = createDefault();
        HttpGet request = new HttpGet(DEFAULT_BASE_URL + url);
        StringBuilder sb = new StringBuilder(url + "?");
        int l = params.length - 1;
        while (--l > 0) {
            sb.append("&").append(params[l]);
        }
        try {
            request.setURI(new URI(sb.toString()));
            CloseableHttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity());
                return JSONObject.parseObject(result, clazz);
            }
        } catch (URISyntaxException | IOException e) {
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }


    /**
     * httpUtil invoke by RequestMethod.POST
     * which requestBody contentType default is application/json
     *
     * @param url
     * @param u
     * @param clazz
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T extends AbstractResponsePacket, U extends AbstractRequestPacket> T postJsonData(String url, U u, Class<T> clazz) {
        CloseableHttpClient httpClient = createDefault();
        HttpPost post = new HttpPost(DEFAULT_BASE_URL + url);

        post.setHeader(CONTENT_TYPE, JSON_CONTENT_TYPE);

        StringEntity stringEntity = new StringEntity(JSON.toJSONString(u), CharsetUtil.UTF_8);
        post.setEntity(stringEntity);

        logger.debug("HttpPost: " + post);
        T t = null;
        try {
            t = clazz.newInstance();
            CloseableHttpResponse response = httpClient.execute(post);
            logger.debug("ResponseStatusCode: " + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
                logger.debug("ResponseCode: " + jsonObject.get(CODE));
                if (CODE_SUCCESS.equals(jsonObject.get(CODE))) {
                    JSONObject data = (JSONObject) jsonObject.get(DATA);
                    logger.debug("ResponseData: " + data);
                    if (data != null) {
                        t = JSONObject.toJavaObject(data, clazz);
                    }
                } else {
                    t.setCode((String) jsonObject.get(CODE));
                    t.setMessage((String) jsonObject.get(MESSAGE));
                    t.setSuccess(false);
                }
            } else {
                t.setMessage(response.getStatusLine().getReasonPhrase());
                t.setCode(String.valueOf(response.getStatusLine().getStatusCode()));
                t.setSuccess(false);
            }
            logger.debug("Response: ", t);
        } catch (IOException | IllegalAccessException | InstantiationException e) {
            logger.error("HttpUtilError", e);
        }
        return t;
    }

    public static void main(String[] args) {
        LoginRequestPacket request = new LoginRequestPacket();
        request.setUserName("ch");
        request.setPassword("ch4sb");
        LoginResponsePacket response = postJsonData(Command.LOGIN_REQUEST.url, request, LoginResponsePacket.class);
        System.out.println(response);
    }
}
