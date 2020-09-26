package com.example.mym3u8down;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mym3u8down.utils.AnimUtils;
import com.example.mym3u8down.utils.MyDateTimeUtils;
import com.example.mym3u8down.utils.ScreenOrientation;
import com.example.mym3u8down.video.IMediaController;
import com.example.mym3u8down.video.VideoPlayer;
import com.example.mym3u8down.view.MarqueeTextView;

import androidx.appcompat.widget.AppCompatSeekBar;

public class VideoPlayActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "VideoPlayActivity";

    private final static String VIDEO_PATH = "VIDEO_PATH";

    private final static int MSG_VIDEO_PROGRESS = 0;
    private final static int MSG_CLOSE_MEDIA_CONTROL = 1;
    private String path;


    private VideoPlayer videoPlayer;
    private RelativeLayout playback_controller_view;
    private LinearLayout playback_controller_top;
    private ImageButton playback_back;
    private MarqueeTextView playback_controls_title;
    private LinearLayout playback_controller_bottom;
    private CheckBox playback_play_pause;
    private AppCompatSeekBar playback_seekbar;
    private TextView playback_position;
    private TextView playback_duration;

    private IMediaController iMediaController = new IMediaController() {
        @Override
        public void hide() {
            if(isShowing()){
                Log.d("VideoPlay","will hide mediacontrol!");
                AnimUtils.setOutAnim(playback_controller_top, false, 1*1000, 0.1f).start();
                AnimUtils.setOutAnim(playback_controller_bottom, true, 1*1000, 0.1f).start();
                playback_controller_view.setVisibility(View.GONE);
                uiHandler.removeMessages(MSG_CLOSE_MEDIA_CONTROL);
            }
        }

        @Override
        public boolean isShowing() {
            if(playback_controller_view!=null){
                return playback_controller_view.getVisibility() == View.VISIBLE;
            }
            return false;
        }

        @Override
        public void setAnchorView(View view) {

        }

        @Override
        public void setEnabled(boolean enabled) {

        }

        @Override
        public void setMediaPlayer(MediaController.MediaPlayerControl player) {

        }

        @Override
        public void show(int timeout) {
            if(!isShowing()){
                Log.d("VideoPlay","will hide mediacontrol!");
                uiHandler.removeMessages(MSG_CLOSE_MEDIA_CONTROL);
                playback_controller_view.setVisibility(View.VISIBLE);
                AnimUtils.setInAnim(playback_controller_top, 1*1000, 1f).start();
                AnimUtils.setInAnim(playback_controller_bottom, 1*1000, 1f).start();
                uiHandler.sendEmptyMessageDelayed(MSG_CLOSE_MEDIA_CONTROL, timeout);
            }
        }

        @Override
        public void show() {
            show(10*1000);
        }

        @Override
        public void showOnce(View view) {

        }
    };

    public static void actionStart(Context context, String path){
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra(VIDEO_PATH, path);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseActivity.NavigationBarStatusBar(this, true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.video_play);
        initData();
        initView();

        videoPlayer.setVideoPath(path);
        videoPlayer.start();
        videoPlayer.setMediaController(iMediaController);
        uiHandler.sendEmptyMessageDelayed(MSG_CLOSE_MEDIA_CONTROL, 10*1000);
        ViewGroup.LayoutParams layoutParams = videoPlayer.getLayoutParams();
        if(layoutParams != null){
            Log.d(TAG,"before+_+videoPlay.width:"+layoutParams.width+",videoPlay.height:"+layoutParams.height);
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            Log.d(TAG,"after+_+videoPlay.width:"+layoutParams.width+",videoPlay.height:"+layoutParams.height);
            videoPlayer.setLayoutParams(layoutParams);
        }
        uiHandler.sendEmptyMessageDelayed(MSG_VIDEO_PROGRESS, 200);
        playback_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int seekPos = seekBar.getProgress();
                videoPlayer.seekTo(seekPos);
            }
        });
        playback_play_pause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    videoPlayer.pause();
                }else {
                    videoPlayer.start();
                }
            }
        });
        playback_back.setOnClickListener(this);
    }

    @SuppressLint("HandlerLeak")
    Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CLOSE_MEDIA_CONTROL:
                    if(iMediaController!=null && iMediaController.isShowing()){
                        iMediaController.hide();
                    }
                    break;
                case MSG_VIDEO_PROGRESS:
                    if(videoPlayer.getDuration() > 0){
                        playback_seekbar.setMax(videoPlayer.getDuration());
                        playback_seekbar.setProgress(videoPlayer.getCurrentPosition());
                        Log.d(TAG,"Position():"+videoPlayer.getCurrentPosition());
                        Log.d(TAG,"duration():"+videoPlayer.getDuration());
                        Log.d(TAG,"progress():"+videoPlayer.getBufferPercentage()* videoPlayer.getDuration() / 100);
                        Log.d(TAG,"BufferPercentage():"+videoPlayer.getBufferPercentage());
                        playback_seekbar.setSecondaryProgress(videoPlayer.getBufferPercentage()* videoPlayer.getDuration() / 100);
                    }
                    playback_position.setText(MyDateTimeUtils.videoSedFormat(videoPlayer.getCurrentPosition()/1000));
                    playback_duration.setText(MyDateTimeUtils.videoSedFormat(videoPlayer.getDuration()/1000));
                    uiHandler.sendEmptyMessageDelayed(MSG_VIDEO_PROGRESS, 200);
                    break;
                 default:
                     break;
            }
        }
    };

    private void initData(){
        Intent intent = getIntent();
        path = intent.getStringExtra(VIDEO_PATH);
        Log.d(TAG+"path:",path);
    }

    private void initView(){
        videoPlayer = (VideoPlayer) findViewById(R.id.video_player);
        //videoPlayer.setVideoListener(this);
        LayoutInflater.from(getApplicationContext()).inflate(R.layout.playback_control_view, videoPlayer);
        playback_controller_view = findViewById(R.id.playback_controller_view);
        playback_controller_top = findViewById(R.id.playback_controller_top);
        playback_back = findViewById(R.id.playback_back);
        playback_controls_title = findViewById(R.id.playback_controls_title);
        playback_controls_title.setText(path.substring(path.lastIndexOf("/")>0?path.lastIndexOf("/"):0));

        playback_controller_bottom = findViewById(R.id.playback_controller_bottom);
        playback_play_pause = findViewById(R.id.playback_play_pause);
        playback_seekbar = findViewById(R.id.playback_seekbar);
        playback_position = findViewById(R.id.playback_position);
        playback_duration = findViewById(R.id.playback_duration);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Log.d(TAG, "onWindowFocusChanged:" + "true");
        } else {
            Log.d(TAG, "onWindowFocusChanged:" + "false");
        }
        BaseActivity.NavigationBarStatusBar(this, hasFocus);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.playback_back:
                videoPlayer.stop();
                videoPlayer.release(true);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {

            case KeyEvent.KEYCODE_ENTER://确定键enter

            case KeyEvent.KEYCODE_DPAD_CENTER:
                Log.d(TAG,"enter--->");
                break;

            case KeyEvent.KEYCODE_BACK:    //返回键
                Log.d(TAG,"back--->");
                //videoPlayer.stop();
                this.finish();
                return true;   //这里由于break会退出，所以我们自己要处理掉 不返回上一层

            case KeyEvent.KEYCODE_SETTINGS: //设置键
                Log.d(TAG,"setting--->");
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:   //向下键

                /*    实际开发中有时候会触发两次，所以要判断一下按下时触发 ，松开按键时不触发
                 *    exp:KeyEvent.ACTION_UP
                 */
                if (event.getAction() == KeyEvent.ACTION_DOWN){
                    Log.d(TAG,"down--->");
                }
                break;

            case KeyEvent.KEYCODE_DPAD_UP:   //向上键
                Log.d(TAG,"up--->");
                break;

            case KeyEvent.KEYCODE_0:   //数字键0
                Log.d(TAG,"0--->");
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT: //向左键
                Log.d(TAG,"left--->");
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:  //向右键
                Log.d(TAG,"right--->");
                break;

            case KeyEvent.KEYCODE_INFO:    //info键
                Log.d(TAG,"info--->");
                break;

            case KeyEvent.KEYCODE_PAGE_DOWN:     //向上翻页键

            case KeyEvent.KEYCODE_MEDIA_NEXT:
                Log.d(TAG,"page down--->");
                break;

            case KeyEvent.KEYCODE_PAGE_UP:     //向下翻页键

            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                Log.d(TAG,"page up--->");
                break;

            case KeyEvent.KEYCODE_VOLUME_UP:   //调大声音键
                Log.d(TAG,"voice up--->");
                break;

            case KeyEvent.KEYCODE_VOLUME_DOWN: //降低声音键
                Log.d(TAG,"voice down--->");
                break;

            case KeyEvent.KEYCODE_VOLUME_MUTE: //禁用声音
                Log.d(TAG,"voice mute--->");
                break;

            default:
                break;

        }
        return super.onKeyDown(keyCode, event);

    }

    //activity生命周期**
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");
        if(videoPlayer.getCurrentPosition() > 0){
            videoPlayer.start();
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "onPause");
        videoPlayer.pause();
    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG, "onStop");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG, "onRestart");
    }
    //activity生命周期end
}
