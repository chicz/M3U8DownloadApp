package com.example.mym3u8down.livedata;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NetWorkSpeedModel extends ViewModel {

    private MutableLiveData<String> speed;

    private final static NetWorkSpeedModel INSTANCE = new NetWorkSpeedModel();

    private NetWorkSpeedModel(){
        speed = new MutableLiveData<>();
    }

    public static NetWorkSpeedModel getInstance(){
        return INSTANCE;
    }

    public MutableLiveData<String> getSpeed(){
        return speed;
    }

}
