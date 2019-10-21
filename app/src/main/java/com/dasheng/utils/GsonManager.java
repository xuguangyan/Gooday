package com.dasheng.utils;

import com.google.gson.Gson;

import java.util.Map;

public class GsonManager<T> {
    public static <T> T getGson(String json, Class<T> tClass) {
        Gson gson = new Gson();
        return gson.fromJson(json, tClass);
    }

    public static <T> String mapToJson(Map<String, T> map) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        return jsonStr;
    }
}