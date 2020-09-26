package com.example.mym3u8down.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mym3u8down.R;
import com.example.mym3u8down.entity.FileDetail;
import com.example.mym3u8down.livedata.AddrValueModel;
import com.example.mym3u8down.view.MarqueeTextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FileAddrAdapter extends RecyclerView.Adapter<FileAddrAdapter.ViewHolder> {

    private Context mContext;
    private List<FileDetail> mAddrList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        MarqueeTextView addr_name;
        EditText addr_value;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            addr_name = itemView.findViewById(R.id.addr_name);
            addr_value = itemView.findViewById(R.id.addr_value);
        }
    }

    public FileAddrAdapter(List<FileDetail> list){
        mAddrList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.video_addr, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.addr_value.setInputType(EditorInfo.TYPE_NULL);
        holder.addr_value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int positoin = holder.getAdapterPosition();
                if(hasFocus){
                    holder.addr_value.setTextColor(Color.parseColor("#1afa29"));
                    AddrValueModel.getInstance().getEditvalue().setValue(holder.addr_value.getText().toString());
                }else {
                    holder.addr_value.setTextColor(Color.parseColor("#ffffff"));
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FileDetail fileDetail = mAddrList.get(position);
        holder.addr_name.setText(fileDetail.getFile_name());
        holder.addr_value.setText(fileDetail.getFile_path());
    }

    @Override
    public int getItemCount() {
        return mAddrList.size();
    }

}
