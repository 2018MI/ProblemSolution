package org.chengpx.util.net;

import okhttp3.Request;

public class RequestBean {
    private Request request;
    private String actionName;
    private Class resultClass;

    public RequestBean(Request request, String actionName, Class resultClass) {
        this.request = request;
        this.actionName = actionName;
        this.resultClass = resultClass;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public Class getResultClass() {
        return resultClass;
    }

    public void setResultClass(Class resultClass) {
        this.resultClass = resultClass;
    }
}
