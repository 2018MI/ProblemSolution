package org.chengpx.util.net;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class NetUtil {
    private final static NetUtil NET_UTIL = new NetUtil();
    private Gson mGson;
    private LinkedList<RequestBean> mRequestBeans;
    private RequestBean mCurrentRequestBean;
    private ArrayBlockingQueue<RequestBean> mRequestPool;
    private Semaphore mSemaphore;
    private RequestFactory mRequestFactory;
    private OkHttpClient mClient;
    private boolean mLooperFlag;

    public static NetUtil getNetUtil() {
        return NET_UTIL;
    }

    public <ARG, RESULT> void addRequest(String actionName, Map<String, ARG> argMap, Class<RESULT> resultClass) {
        mRequestBeans.add(new RequestBean(mRequestFactory.buildRequest(actionName, argMap), resultClass, actionName));
    }

    private NetUtil() {
        this.mGson = new Gson();
        this.mRequestBeans = new LinkedList<>();
        this.mRequestPool = new ArrayBlockingQueue<>(1);
        this.mSemaphore = new Semaphore(1);
        this.mRequestFactory = RequestFactory.getRequestFactory();
        this.mClient = new OkHttpClient();
        initLooper();
    }

    private void initLooper() {
        mLooperFlag = true;
        new Thread(new CheckRunnable()).start();
        new Thread(new SendRunnable()).start();
    }

    private void check() {
        while (mLooperFlag) {
            RequestBean requestBean = null;
            if (mRequestBeans.size() == 0) {
                try {
                    Thread.sleep(300);
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            requestBean = mRequestBeans.removeFirst();
            try {
                mRequestPool.put(requestBean);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void send() {
        while (mLooperFlag) {
            try {
                mSemaphore.acquire();
                mCurrentRequestBean = mRequestPool.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mClient.newCall(mCurrentRequestBean.getRequest()).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Wong-Util", "网络请求失败" + e.getMessage());
                    EventBus.getDefault().post(false);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    } finally {
                        mSemaphore.release();
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    try {
                        result = new JsonParser().parse(result).getAsJsonObject().get("serverInfo").toString();
                        postResult(result, mCurrentRequestBean.getResultClass());
                    } catch (Exception e) {
                        Log.e("Wong-Util", "Json数据解析失败" + e.getMessage() + result);
                        EventBus.getDefault().post(false);
                    } finally {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mSemaphore.release();
                    }
                }
            });
        }
    }

    private <RESULT> void postResult(String result, Class<RESULT> resultClass) {
        switch (mCurrentRequestBean.getActionName()) {
            default:
                EventBus.getDefault().post(mGson.fromJson(result, resultClass));
                break;
            case "GetBusstationInfo.do":
                JsonArray jsonArray = new JsonParser().parse(result).getAsJsonArray();
                List<RESULT> resultList = new ArrayList<>();
                for (JsonElement jsonElement : jsonArray) {
                    resultList.add(mGson.fromJson(jsonElement, resultClass));
                }
                EventBus.getDefault().post(resultList);
                break;
        }
    }

    class CheckRunnable implements Runnable {
        @Override
        public void run() {
            check();
        }
    }

    class SendRunnable implements Runnable {
        @Override
        public void run() {
            send();
        }
    }
}
