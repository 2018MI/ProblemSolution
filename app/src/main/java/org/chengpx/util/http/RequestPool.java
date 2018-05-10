package org.chengpx.util.http;

import com.google.gson.reflect.TypeToken;

import org.chengpx.util.common.ThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * 请求池<br/>
 * 向池中添加构建 post 请求需要的 url, 请求参数 json, 以及请求成功后的回调接口对象，
 * 请求池完成请求的发送<br/>
 * 请求池每次只发送一个请求, 下一次的请求发送之前必须调用 next()
 * <p>
 * create at 2018/4/26 15:33 by chengpx
 */
public class RequestPool {

    private static RequestPool sRequestPool = new RequestPool();

    private final ArrayBlockingQueue<MyRequest> mRunnableArrayBlockingQueue;
    private final Semaphore mSemaphore;
    private final List<MyRequest> mMyRequestList;
    private boolean mIsShouldPut;
    private boolean mIsShouldTake;

    private RequestPool() {
        mRunnableArrayBlockingQueue = new ArrayBlockingQueue<MyRequest>(1);
        mSemaphore = new Semaphore(1);
        mMyRequestList = new ArrayList<>();
        execute();
    }

    public static RequestPool getInstance() {
        return sRequestPool;
    }

    /**
     * 添加一个请求
     *
     * @param url       协议地址
     * @param req       请求参数
     * @param typeToken 返回数据令牌
     * @param <Req>     请求参数封装体类型
     */
    public <Req> void add(String url, Req req, TypeToken typeToken) {
        mMyRequestList.add(new MyRequest(url, req, typeToken));
    }

    public void remove() {
    }

    private void execute() {
        for (int index = 0; index < 1; index++) {
            ThreadPool.getInstance().execute(new RequestPutTask());
            ThreadPool.getInstance().execute(new RequestTakeTask());
        }
    }

    private class RequestPutTask implements Runnable {

        @Override
        public void run() {
            mIsShouldPut = true;
            while (mIsShouldPut) {
                if (mMyRequestList.size() == 0) {
                    continue;
                }
                MyRequest myRequest = mMyRequestList.remove(0);
                if (myRequest == null) {
                    continue;
                }
                try {
                    mRunnableArrayBlockingQueue.put(myRequest);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private class RequestTakeTask implements Runnable {
        @Override
        public void run() {
            mIsShouldTake = true;
            while (mIsShouldTake) {
                try {
                    MyRequest myRequest = mRunnableArrayBlockingQueue.take();
                    HttpUtils.getInstance().sendPost(myRequest.getUrl(), myRequest.getARG(), myRequest.getTypeToken());
                    mSemaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MyRequest<ARG> {

        private final String mUrl;
        private final ARG mARG;
        private final TypeToken mTypeToken;

        MyRequest(String url, ARG ARG, TypeToken typeToken) {
            mUrl = url;
            mARG = ARG;
            mTypeToken = typeToken;
        }

        public String getUrl() {
            return mUrl;
        }

        public ARG getARG() {
            return mARG;
        }

        public TypeToken getTypeToken() {
            return mTypeToken;
        }

    }

    /**
     * 发送下一个请求
     */
    public void next() {
        mSemaphore.release();
    }

}
