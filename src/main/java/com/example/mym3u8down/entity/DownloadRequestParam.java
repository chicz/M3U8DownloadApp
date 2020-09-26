package com.example.mym3u8down.entity;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class DownloadRequestParam {
    //context最好还是不要传进来，但未了确认是哪步出错Toast，这里还是暂用
    private Context context;
    private Request request;
    private OkHttpClient httpClient;
    private String filePath;
    private String fileName;
    private int listSize;
    private String lastFileName;

    public DownloadRequestParam(Context context,Request request, OkHttpClient httpClient, String filePath, String fileName, int listSize, String lastFileName){
        this.context=context;
        this.request=request;
        this.httpClient=httpClient;
        this.filePath=filePath;
        this.fileName=fileName;
        this.listSize=listSize;
        this.lastFileName=lastFileName;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getListSize() {
        return listSize;
    }

    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    public String getLastFileName() {
        return lastFileName;
    }

    public void setLastFileName(String lastFileName) {
        this.lastFileName = lastFileName;
    }
}
