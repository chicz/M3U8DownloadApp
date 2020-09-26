package com.example.mym3u8down.livedata;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddrValueModel extends ViewModel {
    private MutableLiveData<String> editvalue;

    private final static AddrValueModel INSTANCE = new AddrValueModel();

    private AddrValueModel(){
        editvalue = new MutableLiveData<>();
    }

    public static AddrValueModel getInstance(){
        return INSTANCE;
    }

    public MutableLiveData<String> getEditvalue(){
        return editvalue;
    }

}
