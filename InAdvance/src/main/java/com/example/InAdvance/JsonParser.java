package com.example.InAdvance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {

    private List<HashMap<String, String>> parseJsonArray (JSONArray jsonArray){
        List<HashMap<String, String>> dataList = new ArrayList<>();
        for(int i = 0; i< jsonArray.length(); i++){
            try {
                HashMap<String, String> data = parseJsonObject((JSONObject) jsonArray.get(i));
                dataList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    public List<HashMap<String, String>> parseResult(JSONObject object){
        JSONArray jsonArray = null;
        try {
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseJsonArray(jsonArray);
    }


    private HashMap<String, String> parseJsonObject(JSONObject object){
        HashMap<String, String> dataList = new HashMap<>();
        String name =  "";
        String latitude = "";
        String longitude ="";
        String opening_hours = "";
        String business_status ="";
        String icon ="";
        Integer rating = 0;
        JSONArray types = new JSONArray();
        Integer user_ratings_total = 0;
        String vicinity = "";
        try {

            business_status = object.getString("business_status");
            name = object.getString("name") !=null ? object.getString("name") : "";

            JSONObject geo = object.getJSONObject("geometry");
            if(geo!=null){
                latitude = geo.getJSONObject("location").getString("lat");
                longitude = geo.getJSONObject("location").getString("lng");
            }

            icon = object.getString("icon");

            if(object.getJSONObject("opening_hours")!=null){
                opening_hours=object.getJSONObject("opening_hours").getString("open_now");
            };


            rating = object.getInt("rating");
            types = object.getJSONArray("types");
            user_ratings_total = object.getInt("user_ratings_total");
            vicinity = object.getString("vicinity");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        dataList.put("business_status", business_status);
        dataList.put("name", name);
        dataList.put("lat", latitude);
        dataList.put("lng", longitude);
        dataList.put("icon", icon);
        dataList.put("open_hours", opening_hours);
        dataList.put("rating", String.valueOf(rating));
        dataList.put("types", String.valueOf(types));
        dataList.put("user_ratings_total", String.valueOf(user_ratings_total));
        dataList.put("vicinity", vicinity);
        return dataList;

    }

}
