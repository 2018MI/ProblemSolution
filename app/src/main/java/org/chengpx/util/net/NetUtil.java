package org.chengpx.util.net;

import org.chengpx.util.JsonUtil;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class NetUtil {
    private final static NetUtil NET_UTIL = new NetUtil();
    private OkHttpClient client;
    private boolean looperFlag;
    private LinkedList<RequestBean> requestPool;
    private RequestFactory requestFactory;
    private JsonUtil jsonUtil;

    public static NetUtil getNetUtil() {
        return NET_UTIL;
    }

    private NetUtil() {
        this.client = new OkHttpClient();
        this.looperFlag = true;
        this.requestPool = new LinkedList<>();
        this.requestFactory = RequestFactory.getRequestFactory();
        this.jsonUtil = new JsonUtil();
        initLooper();
    }

    public <Arg, Result> void addRequest(String actionName, Map<String, Arg> argMap, Class<Result> resultClass) {
        requestPool.add(new RequestBean(requestFactory.buildRequest(actionName, argMap), actionName, resultClass));
    }

    private void initLooper() {
        looperFlag = true;
        new Thread() {
            @Override
            public void run() {
                while (looperFlag) {
                    if (requestPool.size() == 0) {
                        try {
                            sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        looperFlag = false;
                        sendRequest();
                    }
                }
            }
        }.start();
    }

    private void sendRequest() {
        final RequestBean requestBean = requestPool.removeFirst();
        client.newCall(requestBean.getRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("请求失败"+e.getMessage());
                initLooper();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("请求成功"+call.request().toString());
                if (requestBean.getResultClass() != null) {
                    sendMessage(response, requestBean);
                } else {
                    System.out.println("丢弃服务器返回的数据");
                }
                initLooper();
            }
        });
    }

    private void sendMessage(Response response, RequestBean requestBean) throws IOException {
        switch (requestBean.getActionName()) {
            default:
                EventBus.getDefault().post(jsonUtil.getResult(response, requestBean.getResultClass()));
                break;
            case "GetParkFree.do":
                EventBus.getDefault().post(jsonUtil.getResultAs(response, requestBean.getResultClass()));
                break;
        }
    }
}
