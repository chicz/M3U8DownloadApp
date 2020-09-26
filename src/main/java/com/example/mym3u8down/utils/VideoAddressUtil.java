package com.example.mym3u8down.utils;

import com.example.mym3u8down.entity.FileDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VideoAddressUtil {

    public static List<FileDetail> getVideoAddrByNet(String url){
        List<FileDetail> list = new ArrayList<>();
        if(url==null || "".equals(url)){
            return  list;
        }
        Request request = OkHttpUtils.getRequest(url);
        OkHttpClient httpClient = OkHttpUtils.getDefaultClient();
        try {
            Response response = httpClient.newCall(request).execute();
            String responseData = response.body().string();
            System.out.println("----video address url resp----: "+responseData);
            JSONArray jsonArray = new JSONArray(responseData);
            for(int i=0;i < jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String fileName = jsonObject.getString("fileName");
                String fileEpisode = jsonObject.getString("fileEpisode");
                String filePath = jsonObject.getString("filePath");
                FileDetail fileDetail = new FileDetail();
                if(fileName!=null){
                    if (fileEpisode!=null && !"null".equals(fileEpisode)){
                        fileDetail.setFile_name(fileName+" "+fileEpisode);
                    }else {
                        fileDetail.setFile_name(fileName);
                    }
                }
                if(filePath!=null){
                    fileDetail.setFile_path(filePath);
                }
                list.add(fileDetail);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
