package org.chengpx.util.http;

import com.google.gson.reflect.TypeToken;

import org.chengpx.util.common.SignalInstancePool;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * create at 2018/4/23 14:50 by chengpx
 */
public class HttpUtils {

    private static HttpUtils sHttpUtils = new HttpUtils();

    private final OkHttpClient mOkHttpClient;
    private String tag = "org.chengpx.util.http.HttpUtils";

    private HttpUtils() {
        mOkHttpClient = new OkHttpClient();
    }

    /**
     * 发送一个 post 请求
     *
     * @param url       协议接口
     * @param arg       请求参数
     * @param typeToken 返回类型令牌
     * @param <ARG>     请求参数封装体类型
     */
    public <ARG> void sendPost(String url, ARG arg, final TypeToken typeToken) {
        String json;
        if (arg != null) {
            json = SignalInstancePool.newGson().toJson(arg);
        } else {
            json = "{}";
        }
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .build();
        mOkHttpClient.newCall(request).enqueue(new okhttp3.Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (typeToken == null) {
                    return;
                }
                String json = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String serverInfo = jsonObject.getString("serverInfo");
                    EventBus.getDefault().post(SignalInstancePool.newGson().fromJson(serverInfo, typeToken.getType()));
                } catch (JSONException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(SignalInstancePool.newGson().fromJson(json, typeToken.getType()));
                } finally {
                    RequestPool.getInstance().next();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

        });

    }

    /**
     * 返回实例对象
     *
     * @return 单例对象
     */
    public static HttpUtils getInstance() {
        return sHttpUtils;
    }

}
