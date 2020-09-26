package com.example.mym3u8down;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mym3u8down.adapter.FileAddrAdapter;
import com.example.mym3u8down.adapter.FileDetailAdapter;
import com.example.mym3u8down.entity.FileDetail;
import com.example.mym3u8down.livedata.AddrValueModel;
import com.example.mym3u8down.livedata.MyLivedataModel;
import com.example.mym3u8down.livedata.NetWorkSpeedModel;
import com.example.mym3u8down.service.DownloadService;
import com.example.mym3u8down.utils.FileDownloadUtil;
import com.example.mym3u8down.utils.MyMd5Util;
import com.example.mym3u8down.utils.NetWorkSpeedUtil;
import com.example.mym3u8down.utils.OkHttpUtils;
import com.example.mym3u8down.utils.SystemTool;
import com.example.mym3u8down.utils.VideoAddressUtil;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    public final static String PREPARE = "prepare";
    public final static String SUCCESS = "success";
    public final static String ERROR = "error";
    private final static int maxRetryConut = 5;
    private static int completeCount = 0;

    private final static String LOCAL_MODEL = "local_model";
    private final static String NET_MODEL = "net_model";
    private final static String OTHER_MODEL = "other_model";

    public static String root_menu;
    public static String file_first_menu;
    public static String file_second_menu;
    public static String file_video_temp_menu;
    public static String file_video_download_menu;
    public static String video_addr_url;

    private DrawerLayout mDrawerLayout;
    private ConstraintLayout constraintLayout;
    private List<View> viewList = new ArrayList<>();
    private EditText editText1;
    private Button btn1;
    private EditText editText2;
    private Button btn2;
    private EditText editText3;
    private Button btn3;
    private Button btn3_1;
    private static int btn3_clickCount;

    private LinearLayout localLyt;
    private ImageView localImg;
    private TextView localText;
    private LinearLayout netLyt;
    private ImageView netImg;
    private TextView netText;
    private LinearLayout otherLyt;
    private ImageView otherImg;
    private TextView otherText;

    private RecyclerView addrRecyclerView;
    private FileAddrAdapter fileAddrAdapter;
    private List<FileDetail> fileAddrList = new ArrayList<>();
    private static int currentModel=0;

    private RecyclerView recyclerView;
    private FileDetailAdapter fileDetailAdapter;
    private List<FileDetail> fileDetails = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView netSpeedView;

    private DownloadService.DownloadBinder downloadBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        initData();
        initView();


        Intent intent = new Intent(this, DownloadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android8.0以上通过startForegroundService启动service
            System.out.println("----test a----");
            startForegroundService(intent);
            System.out.println("----test a after----");
        } else {
            System.out.println("----test b----");
            startService(intent);
            System.out.println("----test b after----");
        }
        //startService(intent);//启动服务
        System.out.println("----test d----");
        bindService(intent, connection, BIND_AUTO_CREATE);//绑定服务
        System.out.println("----test e----");

        //myLivedataModel = new MyLivedataModel();
        //myLivedataModel = ViewModelProviders.of(this).get(MyLivedataModel.class);
        //myLivedataModel = new ViewModelProvider(this).get(MyLivedataModel.class);
        MyLivedataModel.getInstance().getStatus().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                switch (s){
                    case PREPARE:
                        btn3.setEnabled(false);
                        btn3.setBackgroundColor(Color.parseColor("#c9302c"));
                        break;
                    case SUCCESS:
                    case ERROR:
                        btn3.setEnabled(true);
                        btn3.setBackgroundColor(Color.parseColor("#46b8da"));
                        btn3.setText("下        载");
                        swipeRefreshLayout.setRefreshing(true);
                        refreshFileList();
                        break;
                        default: break;
                }
            }
        });
        MyLivedataModel.getInstance().getProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer==100){
                    btn3.setText("99.9%");
                }else {
                    btn3.setText(integer+"%");
                }
            }
        });
        AddrValueModel.getInstance().getEditvalue().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                editText3.setText(s);
            }
        });
        NetWorkSpeedModel.getInstance().getSpeed().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                netSpeedView.setText(s);
            }
        });
        NetWorkSpeedUtil netWorkSpeedUtil = new NetWorkSpeedUtil(this);
        netWorkSpeedUtil.startShowNetSpeed();
    }

    private void initView(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawerLyt);
        ActionBarDrawerToggle act = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.activity_main_nav_menu_open, R.string.activity_main_nav_menu_close);
        mDrawerLayout.addDrawerListener(act);
        act.syncState();
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        collapsingToolbarLayout.setTitle("  ");

        addrRecyclerView = findViewById(R.id.addr_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        addrRecyclerView.setLayoutManager(linearLayoutManager);
        fileAddrAdapter = new FileAddrAdapter(fileAddrList);
        addrRecyclerView.setAdapter(fileAddrAdapter);

        constraintLayout = findViewById(R.id.constrainLyt);

        editText1 = findViewById(R.id.edit_1);
        viewList.add(editText1);
        btn1 = findViewById(R.id.btn_1);
        btn1.setOnClickListener(this);

        editText2 = findViewById(R.id.edit_2);
        viewList.add(editText2);
        btn2 = findViewById(R.id.btn_2);
        btn2.setOnClickListener(this);

        editText3 = findViewById(R.id.edit_3);
        viewList.add(editText3);
        btn3 = findViewById(R.id.btn_3);
        btn3.setOnClickListener(this);
        btn3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    v.setBackgroundColor(Color.parseColor("#449d44"));
                }else {
                    v.setBackgroundColor(Color.parseColor("#5bc0de"));
                }
            }
        });
        btn3_1 = findViewById(R.id.btn_3_1);
        btn3_1.setOnClickListener(this);
        btn3_1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    v.setBackgroundColor(Color.parseColor("#449d44"));
                }else {
                    v.setBackgroundColor(Color.parseColor("#5bc0de"));
                }
            }
        });

        localLyt = findViewById(R.id.addr_model_1);
        localImg = findViewById(R.id.addr_img1);
        localText = findViewById(R.id.addr_content1);
        netLyt = findViewById(R.id.addr_model_2);
        netImg = findViewById(R.id.addr_img2);
        netText = findViewById(R.id.addr_content2);
        otherLyt = findViewById(R.id.addr_model_3);
        otherImg = findViewById(R.id.addr_img3);
        otherText = findViewById(R.id.addr_content3);
        localLyt.setOnClickListener(this);
        netLyt.setOnClickListener(this);
        otherLyt.setOnClickListener(this);

        netSpeedView = findViewById(R.id.net_speed);

        recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
        fileDetailAdapter = new FileDetailAdapter(fileDetails);
        recyclerView.setAdapter(fileDetailAdapter);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorAccent,
                R.color.design_default_color_primary,
                R.color.design_default_color_primary_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFileList();
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(true);
                }
            }
        });
        refreshFileList();
        refreshAddrModels(LOCAL_MODEL);
    }

    private void initData(){
        root_menu = FileDownloadUtil.getRootMenu(getApplicationContext());
        file_first_menu = getString(R.string.m3u8_addr_file);
        file_second_menu = getString(R.string.m3u8_file);
        file_video_temp_menu = getString(R.string.video_temp);
        file_video_download_menu = getString(R.string.video_download);
        video_addr_url = getString(R.string.video_addr_url);
        currentModel=0;
    }

    private void refreshAddrModels(String str){
        fileAddrList.clear();
        fileAddrAdapter.notifyDataSetChanged();
        switch (str){
            case LOCAL_MODEL:
                initAddrList();
                break;
            case NET_MODEL:
            case OTHER_MODEL:
                System.out.println("--video_addr_url--: "+video_addr_url);
                initAddrByNet(video_addr_url);
                break;
        }
    }

    private void initAddrList(){
        fileAddrList.clear();
        FileDetail f1 = new FileDetail();
        f1.setFile_name("海贼王");
        f1.setFile_path("http://iqiyi.cdn9-okzy.com/20200913/15396_0c748d9a/index.m3u8?sign=81576ac44e605640a3d22dbc6327f9c4");
        fileAddrList.add(f1);
        FileDetail f2 = new FileDetail();
        f2.setFile_name("喜洋洋与灰太狼 第1集");
        f2.setFile_path("https://youku.cdn-8-okzy.com/20180714/19834_63dd109c/index.m3u8?sign=d53a756a28088d99392c8cf7282de445");
        fileAddrList.add(f2);
        FileDetail f3 = new FileDetail();
        f3.setFile_name("我家大师兄脑子有坑 第1集");
        f3.setFile_path("http://youku.cdn-2-okzy.com/20180411/9407_8f8e7872/index.m3u8");
        fileAddrList.add(f3);
        FileDetail f4 = new FileDetail();
        f4.setFile_name("萤火之森");
        f4.setFile_path("https://youku.com-youku.net/20180630/15495_00e2d09e/index.m3u8");
        fileAddrList.add(f4);
        fileAddrAdapter.notifyDataSetChanged();
    }

    private void initAddrByNet(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //用这种方式的话,fileAddrList的地址都变了,adapter监听的还是原来那个地址，所以列表不刷新
                //fileAddrList = VideoAddressUtil.getVideoAddrByNet(url);
                fileAddrList.addAll(VideoAddressUtil.getVideoAddrByNet(url));
                /*for (FileDetail fileDetail:fileAddrList){
                    System.out.println(fileDetail.toString());
                }*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileAddrAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.settings:

                break;
            case android.R.id.home:
                return true;
            default:
        }
        return true;
    }

    private void refreshFileList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                fileDetails.clear();
                String filePath = MainActivity.root_menu+"/"+MainActivity.file_video_download_menu;
                File file_menu = new File(filePath);
                if(!file_menu.exists()){
                    file_menu.mkdirs();
                }
                File[] files = new File(filePath).listFiles();
                for(File f : files){
                    if(f.isFile()){
                        FileDetail fileDetail = new FileDetail();
                        fileDetail.setFile_path(f.getPath());
                        fileDetail.setFile_pic(R.mipmap.tubiao);
                        fileDetail.setFile_name(f.getName());
                        fileDetail.setFile_size(f.length());
                        fileDetail.setFile_date(f.lastModified());
                        fileDetails.add(fileDetail);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileDetailAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    public void onClick(View v) {
        if (downloadBinder == null){
            return ;
        }
        switch (v.getId()){
            case R.id.btn_1:
                Toast.makeText(this,"You click btn1,ohhhhhh!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_2:
                Toast.makeText(this,"You click btn2,yehhhhh!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_3:
                String url = editText3.getText().toString();
                String filePath = FileDownloadUtil.createFileName();
                Toast.makeText(this,"btn3:"+url,Toast.LENGTH_SHORT).show();
                if(url!=null && !"".equals(url)){
                    downloadBinder.startDownload(url,filePath);
                    MyLivedataModel.getInstance().getStatus().setValue(PREPARE);
                    MyLivedataModel.getInstance().getProgress().setValue(0);
                }else {
                    Toast.makeText(this,"链接不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_3_1:
                Toast.makeText(this,"Cancel:"+MyLivedataModel.getInstance().getStatus().getValue(),Toast.LENGTH_SHORT).show();
                if(++btn3_clickCount%maxRetryConut==0){
                    btn3.setEnabled(true);
                    btn3.setText("下        载");
                    btn3_1.setText("重        置");
                    btn3_clickCount = 0;
                }else{
                    btn3_1.setText(maxRetryConut-btn3_clickCount+"");
                }
                break;
            case R.id.addr_model_1:
                selectModel(LOCAL_MODEL);
                refreshAddrModels(LOCAL_MODEL);
                break;
            case R.id.addr_model_2:
                selectModel(NET_MODEL);
                refreshAddrModels(NET_MODEL);
                break;
            case R.id.addr_model_3:
                selectModel(OTHER_MODEL);
                refreshAddrModels(OTHER_MODEL);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        if (me.getAction() == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
            //View v = getCurrentFocus();      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (SystemTool.isShouldHideKeyboard(viewList, me)) { //判断用户点击的是否是输入框以外的区域
                //EditText失去焦点
                constraintLayout.setFocusable(true);
                constraintLayout.setFocusableInTouchMode(true);
                constraintLayout.requestFocus();
                //收起键盘
                SystemTool.hideInputKeyboard(this, viewList);
            }
        }
        return super.dispatchTouchEvent(me);
    }

    class LocalReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    private void btn_3_down(final String url){
        List<String> list_m3u8_addr = FileDownloadUtil.getM3U8Content(OkHttpUtils.getRequest(url),OkHttpUtils.getDefaultClient());
        if(url!=null && !"".equals(url) && list_m3u8_addr.size()>0){
            String suffix = list_m3u8_addr.get(0);
            //suffix也有前缀，如：/first/second/third/index.m3u8
            String m3u8_path = url.substring(0,url.lastIndexOf("index."))+suffix;
            final List<String> list = FileDownloadUtil.getM3U8Content(OkHttpUtils.getRequest(m3u8_path),OkHttpUtils.getDefaultClient());

            //开始下载
            String video_path = m3u8_path.substring(0,m3u8_path.lastIndexOf("index."));

            final List<OkHttpClient> httpClients = new ArrayList<>();
            for(int i=0;i<5;i++){
                httpClients.add(OkHttpUtils.getDefaultClient());
            }
            final String lastFileName_prefix = FileDownloadUtil.createFileName();
            final String filePath = root_menu + "/" + file_video_temp_menu + "/" + lastFileName_prefix;
            for (final String fileName : list){
                final Request request = OkHttpUtils.getRequest(video_path+"/"+fileName);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String fileName_md5 = MyMd5Util.digest(fileName);
                            int i = (int) (Long.valueOf(fileName_md5.substring(0,10),16) % 5);
                            downVideo(MainActivity.this,request,httpClients.get(i),filePath,fileName,
                                    1,list.size(),lastFileName_prefix+".mp4");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }

    private void downVideo(Context context,Request request,OkHttpClient httpClient,String filePath,String fileName,
                           int currentCount,int listSize,String lastFileName){
        if(currentCount<=maxRetryConut){
            boolean isSuccess = FileDownloadUtil.fileDownload(request,httpClient,filePath,fileName);
            if(isSuccess){
                addCompletCount(context,listSize,currentCount,filePath,fileName,lastFileName);
            }else {
                System.out.println("--------第"+currentCount+"次下载失败--------"+fileName+": "+fileName);
                downVideo(context,request,httpClient,filePath,fileName,currentCount+1,listSize,lastFileName);
            }
        }else {
            System.out.println("--------"+fileName+"----超过最大重试次数");
            addCompletCount(context,listSize,currentCount,filePath,fileName,lastFileName);
        }
    }

    private synchronized void addCompletCount(Context context,int listSize,int completeTime,String filePath,String fileName,String lastFileName){
        completeCount++;
        System.out.println("--------"+fileName+"----第"+completeTime+"次下载完成,总共完成个数："+ completeCount +"/"+listSize);
        if(completeCount==listSize){
            System.out.println("--------开始合并--------");
            if(!mergeTs(filePath,lastFileName)){
                Toast.makeText(context,"合并文件失败",Toast.LENGTH_SHORT).show();
            }
            System.out.println("--------合并完成--------");
            System.out.println("--------开始删除--------");
            //deleteFiles();
            System.out.println("--------删除完成--------");
            //localComplete=true;
        }
    }

    private boolean mergeTs(String filePath,String lastFileName){
        String merge_path = root_menu+"/"+file_video_download_menu;
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

    private void deleteFiles(String filePath){
        if(filePath!=null && !"".equals(filePath)){
            File[] files = new File(filePath).listFiles();
            for(File f : files){
                f.delete();
            }
        }
    }

    private void selectModel(String str){
        localImg.setImageResource(R.mipmap.bendi);
        localText.setTextColor(Color.parseColor("#ffffff"));
        netImg.setImageResource(R.mipmap.wangluo);
        netText.setTextColor(Color.parseColor("#ffffff"));
        otherImg.setImageResource(R.mipmap.other);
        otherText.setTextColor(Color.parseColor("#ffffff"));
        switch (str){
            case NET_MODEL:
                netImg.setImageResource(R.mipmap.wangluo_select);
                netText.setTextColor(Color.parseColor("#1afa29"));
                break;
            case OTHER_MODEL:
                otherImg.setImageResource(R.mipmap.other_select);
                otherText.setTextColor(Color.parseColor("#1afa29"));
                break;
            case LOCAL_MODEL:
                default:
                    localImg.setImageResource(R.mipmap.bendi_select);
                    localText.setTextColor(Color.parseColor("#1afa29"));
                    break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                //Toast.makeText(this,"you click up", Toast.LENGTH_SHORT).show();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                //Toast.makeText(this,"you click down", Toast.LENGTH_SHORT).show();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if(drawerIsOpen()){
                    currentModel = (currentModel-1+3)%3;
                    changeAddrModel(currentModel);
                }
                //Toast.makeText(this,"you click left", Toast.LENGTH_SHORT).show();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(drawerIsOpen()){
                    currentModel = (currentModel+1+3)%3;
                    changeAddrModel(currentModel);
                }
                //Toast.makeText(this,"you click right", Toast.LENGTH_SHORT).show();
                break;
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:

                break;
            case KeyEvent.KEYCODE_BACK:
                //Toast.makeText(this,"you click back", Toast.LENGTH_SHORT).show();
                if(drawerIsOpen()){
                    //Toast.makeText(this,"drawer is back", Toast.LENGTH_SHORT).show();
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean drawerIsOpen(){
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            return true;
        }
        return false;
    }
    private void changeAddrModel(int currentModel){
        switch (currentModel){
            case 0:
                localLyt.performClick();break;
            case 1:
                netLyt.performClick();break;
            case 2:
                otherLyt.performClick();break;
        }
    }

}
