package com.example.mym3u8down.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FileDownloadUtil {

    public final static long gb = 1024*1024*1024;
    public final static long mb = 1024*1024;
    public final static long kb = 1024;

    public static String getRootMenu(Context context){
        return context.getExternalFilesDir(null).getAbsolutePath();
    }

    public static synchronized String createFileName(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
        return sdf.format(new Date());
    }


    public static String getUnit(long size){
        if(size/gb > 0){
            return "GB";
        }else if(size/mb > 0){
            return "MB";
        }else if(size/kb > 0){
            return "KB";
        }else {
            return "B";
        }
    }
    //余数保留两位
    public static String getFileSize(long size){

        if(size==0){
            return "";
        }
        String unit = getUnit(size);
        double d = 1*100/1024;
        int quotient;//商
        double decimal;//小数
        int decimal_desc;
        switch (unit){
            case "GB":
                quotient = (int) (size/gb);
                decimal = (double) size%gb*100/gb;break;
            case "MB":
                quotient = (int) (size/mb);
                decimal = (double) size%mb*100/mb;break;
            case "KB":
                quotient = (int) (size/kb);
                decimal = (double) size%kb*100/kb;break;
            case "B":
                default:
                    quotient = (int) size;
                    decimal = 0;break;
        }
        decimal_desc = (int) Math.ceil(decimal);
        if(decimal_desc == 0){
            return quotient+" "+unit;
        }else if (decimal_desc < 10){
            return quotient+".0"+decimal_desc+" "+unit;
        }else if (decimal_desc%10 == 0){
            return quotient+"."+decimal_desc/10+" "+unit;
        }else {
            return quotient+"."+decimal_desc+" "+unit;
        }
    }

    public static String getFileDate(long timestamp){
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return sdf.format(date);
    }

    public static List<String> getM3U8Content(Request request,OkHttpClient httpClient){
        List<String> list = new ArrayList<>();
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        String line;
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            inputStream = response.body().byteStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
            while ( (line=bufferedReader.readLine()) != null){
                if(line.indexOf("#") < 0){
                    list.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(bufferedReader!=null){
                    bufferedReader.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return list;
    }

    public static boolean fileDownload(Request request, OkHttpClient httpClient,String filePath,String fileName){
        return fileDownload(request,httpClient,filePath,fileName,false);
    }
    public static boolean fileDownload(Request request, OkHttpClient httpClient,String filePath,String fileName,boolean isCancel){
        if(request==null || httpClient==null || checkEmpty(filePath) || checkEmpty(fileName) || isCancel){
            return false;
        }
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        File menu = new File(filePath);
        if(!menu.exists()){
            if(!menu.mkdirs()){
                System.out.println("fileDownload()创建文件夹失败");
            }
        }
        File file = new File(filePath+"/"+fileName);
        int len;
        byte[] bytes = new byte[1024];
        Response response = null;
        try {
            if(file.exists()){
                file.delete();
            }else {
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            /*
            * 如果想返回response的话，可以把Callback当参数传进来
            * response = httpClient.newCall(request).enqueue(callback);
            * 调用方：FileDownloadUtil.fileDown( .......,new okhttp3.Callback(){
            *             @Override
            *             public void onResponse(Call call,Response response) throws IOException{//do something}
            *             @Override
            *             public void onFailure(Call call,IOException e){//do something}
            *         });
            * */
            response = httpClient.newCall(request).execute();
            inputStream = response.body().byteStream();
            while ( (len=inputStream.read(bytes)) != -1){
                if(isCancel){
                    return false;
                }
                fileOutputStream.write(bytes,0, len);
                fileOutputStream.flush();
            }
            response.body().close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            try{
                if(inputStream!=null){
                    inputStream.close();
                }
                if(fileOutputStream!=null){
                    fileOutputStream.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static boolean checkEmpty(String str){
        if(null==str || "".equals(str)){
            return true;
        }
        return false;
    }

}
