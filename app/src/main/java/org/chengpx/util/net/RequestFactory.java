package org.chengpx.util.net;

import com.google.gson.Gson;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestFactory {
    private Gson mGson;
    private String mIp;
    private String mPort;
    private final static RequestFactory REQUEST_FACTORY = new RequestFactory();

    private RequestFactory() {
        this.mGson = new Gson();
        this.mIp = "192.168.2.19";
        this.mPort = "9090";
    }

    public static RequestFactory getRequestFactory() {
        return REQUEST_FACTORY;
    }

    public <ARG> Request buildRequest(String actionName, Map<String, ARG> argMap) {
        String requestArg = "{}";
        if (argMap != null) {
            requestArg = mGson.toJson(argMap);
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestArg);
        return new Request.Builder().url("http://" + mIp + ":" + mPort + "/transportservice/action/" + actionName).post(requestBody).build();
    }
}
