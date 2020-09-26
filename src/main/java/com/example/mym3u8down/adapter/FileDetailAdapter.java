package com.example.mym3u8down.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mym3u8down.R;
import com.example.mym3u8down.VideoPlayActivity;
import com.example.mym3u8down.entity.FileDetail;
import com.example.mym3u8down.utils.FileDownloadUtil;
import com.example.mym3u8down.view.MarqueeTextView;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class FileDetailAdapter extends RecyclerView.Adapter<FileDetailAdapter.ViewHolder> {

    private Context mContext;
    private List<FileDetail> fileList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;

        TextView serial_num;
        ImageView file_pic;
        MarqueeTextView file_name;
        TextView file_size;
        TextView file_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            serial_num = (TextView) itemView.findViewById(R.id.serial_num);
            file_pic = (ImageView) itemView.findViewById(R.id.file_pic);
            file_name = (MarqueeTextView) itemView.findViewById(R.id.file_name);
            file_size = (TextView) itemView.findViewById(R.id.file_size);
            file_date = (TextView) itemView.findViewById(R.id.file_date);
        }
    }

    public FileDetailAdapter(List<FileDetail> list){
        fileList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.video_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Toast.makeText(mContext,"You Click cardView",Toast.LENGTH_SHORT).show();
                int position = holder.getAdapterPosition();
                File file = new File(fileList.get(position).getFile_path());
                if(null==file || !file.exists() || !file.isFile()){
                    return true;
                }
                VideoPlayActivity.actionStart(mContext, file.getPath());
                //Toast.makeText(mContext,"path:"+file.getPath()+"----,----"+file.getAbsolutePath(),Toast.LENGTH_LONG).show();
                return false;
            }
        });
        holder.cardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //Toast.makeText(mContext,"hasFocus:"+hasFocus, Toast.LENGTH_SHORT).show();
                if (hasFocus){
                    v.setBackgroundColor(Color.parseColor("#80000000"));
                }else {
                    v.setBackgroundColor(Color.parseColor("#ffffff"));
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FileDetail fileDetail = fileList.get(position);
        holder.serial_num.setText(String.valueOf(position+1));
        File file = new File(fileDetail.getFile_path());
        if(!file.exists() || !file.isFile()){
            Glide.with(mContext).load(fileDetail.getFile_pic()).into(holder.file_pic);
        }else {
            Glide.with(mContext).load(Uri.fromFile(new File(fileDetail.getFile_path()))).into(holder.file_pic);
        }
        Glide.with(mContext).load(Uri.fromFile(new File(fileDetail.getFile_path()))).into(holder.file_pic);
        holder.file_name.setText(fileDetail.getFile_name());
        holder.file_size.setText(FileDownloadUtil.getFileSize(fileDetail.getFile_size()));
        holder.file_date.setText(FileDownloadUtil.getFileDate(fileDetail.getFile_date()));
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
}
