package com.example.mym3u8down.task;

import android.os.AsyncTask;

import com.example.mym3u8down.MainActivity;
import com.example.mym3u8down.listener.DownloadListener;
import com.example.mym3u8down.utils.FileDownloadUtil;
import com.example.mym3u8down.utils.OkHttpUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class DownloadTask extends AsyncTask<String, Integer, Void> {

    private DownloadListener listener;
    private static int completeCount = 0;
    private final static int maxRetryCount = 5;

    public DownloadTask(DownloadListener listener){
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(String... params) {
        //0:url;1:lastFileName;
        completeCount=0;
        btn_3_down(params[0],params[1]);
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values){
        int progress = values[0];
        //因为未准备暂停服务，所以lastProgress的作用不大
        /*if(progress > lastProgress){
            listener.onProgress(progress);
            lastProgress = progress;
        }*/
        listener.onProgress(progress);
    }

    public void cancelDownload(){
        completeCount=0;
        listener.onCanceled();
    }

    private void btn_3_down(final String url,final String lastFileName_prefix){
        List<String> list_m3u8_addr = FileDownloadUtil.getM3U8Content(OkHttpUtils.getRequest(url),OkHttpUtils.getDefaultClient());
        if(url!=null && !"".equals(url) && list_m3u8_addr.size()>0){
            String suffix = list_m3u8_addr.get(0);
            //suffix也有前缀，如：/first/second/third/index.m3u8
            String m3u8_path = url.substring(0,url.lastIndexOf("index."))+suffix;
            final List<String> list = FileDownloadUtil.getM3U8Content(OkHttpUtils.getRequest(m3u8_path),OkHttpUtils.getDefaultClient());

            //开始下载
            String video_path = m3u8_path.substring(0,m3u8_path.lastIndexOf("index."));

            final OkHttpClient httpClient = OkHttpUtils.getDefaultClient();
            //final String lastFileName_prefix = FileDownloadUtil.createFileName();
            final String filePath = MainActivity.root_menu + "/" + MainActivity.file_video_temp_menu + "/" + lastFileName_prefix;
            for (final String fileName : list){
                final Request request = OkHttpUtils.getRequest(video_path+"/"+fileName);
                //开启线程的话，怎么cancel?
                /*if(!isCanceled){
                    downVideo(request,httpClient,filePath,fileName,
                            1,list.size(),lastFileName_prefix+".mp4");
                }*/
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //String fileName_md5 = MyMd5Util.digest(fileName);
                            //int i = (int) (Long.valueOf(fileName_md5.substring(0,10),16) % 5);
                            downVideo(request,httpClient,filePath,fileName,
                                    1,list.size(),lastFileName_prefix+".mp4");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }

    private void downVideo(Request request, OkHttpClient httpClient, String filePath, String fileName,
                           int currentCount, int listSize, String lastFileName){
        if(currentCount<=maxRetryCount){
            boolean isSuccess = FileDownloadUtil.fileDownload(request,httpClient,filePath,fileName);
            if(isSuccess){
                addCompleteCount(listSize,currentCount,filePath,fileName,lastFileName);
            }else {
                System.out.println("--------第"+currentCount+"次下载失败--------"+fileName+": "+fileName);
                downVideo(request,httpClient,filePath,fileName,currentCount+1,listSize,lastFileName);
            }
        }else {
            System.out.println("--------"+fileName+"----超过最大重试次数");
            addCompleteCount(listSize,currentCount,filePath,fileName,lastFileName);
        }
    }

    private synchronized void addCompleteCount(int listSize,int completeTime,String filePath,String fileName,String lastFileName){
        completeCount++;
        int progress = (int) completeCount*100/listSize;
        publishProgress(progress);
        System.out.println("--------"+fileName+"----第"+completeTime+"次下载完成,总共完成个数："+ completeCount +"/"+listSize);
        if(completeCount==listSize){
            System.out.println("--------开始合并--------");
            if(!mergeTs(filePath,lastFileName)){
                System.out.println("合并文件失败");
            }
            System.out.println("--------合并完成--------");
            System.out.println("--------开始删除--------");
            deleteFiles(filePath);
            System.out.println("--------删除完成--------");
            listener.onSuccess();
        }
    }

    public static boolean mergeTs(String filePath,String lastFileName){
        String merge_path = MainActivity.root_menu+"/"+MainActivity.file_video_download_menu;
        File menu = new File(merge_path);
        if(!menu.exists()){
            if(!menu.mkdirs()){
                return false;
            }
        }
        File file = new File(merge_path, lastFileName);
        if(file.exists()){
            file.delete();
        }else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        FileOutputStream fileOutputStream = null;
        FileInputStream fileInputStream = null;
        try {
            File[] files = new File(filePath).listFiles();
            List fileList = Arrays.asList(files);
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.isDirectory() && o2.isFile())
                        return -1;
                    if (o1.isFile() && o2.isDirectory())
                        return 1;
                    return o1.getName().compareTo(o2.getName());
                }
            });

            fileOutputStream = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            for(File f : files){
                System.out.println("----合并fileName: "+f.getName());
                fileInputStream = new FileInputStream(f);
                int len;
                while ((len=fileInputStream.read(bytes)) != -1){
                    fileOutputStream.write(bytes, 0, len);
                }
                fileInputStream.close();
            }
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }finally {
            try {
                if(fileInputStream!=null){
                    fileInputStream.close();
                }
                if(fileOutputStream!=null){
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return true;
    }

    public static void deleteFiles(String filePath){
        if(filePath!=null && !"".equals(filePath)){
            File[] files = new File(filePath).listFiles();
            for(File f : files){
                f.delete();
            }
        }
    }

}
