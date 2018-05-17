package org.chengpx.util.net;

import okhttp3.Request;

public class RequestBean {
    private Request mRequest;
    private Class mResultClass;
    private String mActionName;

    RequestBean(Request request, Class aClass, String actionName) {
        mRequest = request;
        mResultClass = aClass;
        mActionName = actionName;
    }

    public Request getRequest() {
        return mRequest;
    }

    public Class getResultClass() {
        return mResultClass;
    }

    public String getActionName() {
        return mActionName;
    }
}
