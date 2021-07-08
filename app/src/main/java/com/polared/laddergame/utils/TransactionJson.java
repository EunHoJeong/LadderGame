package com.polared.laddergame.utils;

import com.google.gson.Gson;

public class TransactionJson {
    public static String stringArrayFromJson(String[] arrayData){
        Gson gson = new Gson();
        return gson.toJson(arrayData);

    }

    public static String[] jsonFromStringArray(String jsonData){
        Gson gson = new Gson();
        return gson.fromJson(jsonData, String[].class);
    }
}
