package com.example.mym3u8down.livedata;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyLivedataModel extends ViewModel {
    private MutableLiveData<String> status;
    private MutableLiveData<Integer> progress;

    private final static MyLivedataModel INSTANCE = new MyLivedataModel();

    private MyLivedataModel(){
        status = new MutableLiveData<>();
        progress = new MutableLiveData<>();
    }

    public static MyLivedataModel getInstance(){
        return INSTANCE;
    }

    public MutableLiveData<String> getStatus(){
        return status;
    }

    public MutableLiveData<Integer> getProgress(){
        return progress;
    }
}
