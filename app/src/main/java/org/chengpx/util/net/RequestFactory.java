package org.chengpx.util.net;


import org.chengpx.util.JsonUtil;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestFactory {
    private JsonUtil jsonUtil;
    private String ip;
    private String port;

    private final static RequestFactory REQUEST_FACTORY = new RequestFactory();

    private RequestFactory() {
        this.ip = "192.168.2.19";
        this.port = "9090";
        this.jsonUtil = new JsonUtil();
    }

    public static RequestFactory getRequestFactory() {
        return REQUEST_FACTORY;
    }

    public <Arg> Request buildRequest(String actionName, Map<String, Arg> argMap) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonUtil.<Arg>mapToJson(argMap));
        return new Request.Builder().
                url("http://" + ip + ":" + port + "/transportservice/action/" + actionName).
                post(requestBody).
                build();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
