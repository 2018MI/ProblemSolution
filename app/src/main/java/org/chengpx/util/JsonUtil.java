package org.chengpx.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

public class JsonUtil {
    private Gson gson;

    public JsonUtil() {
        this.gson = new Gson();
    }

    public <Arg> String mapToJson(Map<String, Arg> argMap) {
        JsonObject jsonObject = new JsonObject();
        if (argMap == null) {
            return "{}";
        }
        for (String key : argMap.keySet()) {
            jsonObject.addProperty(key, String.valueOf(argMap.get(key)));
        }
        return jsonObject.toString();
    }

    public <Result> Result getResult(Response response, Class<Result> resultClass) throws IOException {
        return gson.fromJson(responseToString(response), resultClass);
    }

    public <Result> List<Result> getResultAs(Response response, Class<Result> resultClass) throws IOException {
        JsonArray jsonArray = new JsonParser().parse(responseToString(response)).getAsJsonArray();
        List<Result> list = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {
            list.add(gson.fromJson(jsonElement, resultClass));
        }
        return list;
    }

    private String responseToString(Response response) throws IOException {
        String result = response.body().string();
        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
        try {
            return jsonObject.get("serverInfo").toString();
        } catch (Exception e) {
            return result;
        }
    }
}
