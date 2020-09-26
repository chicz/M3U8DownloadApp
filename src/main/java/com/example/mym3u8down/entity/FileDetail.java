package com.example.mym3u8down.entity;

public class FileDetail {

    private String file_path;
    private int file_pic;
    private String file_name;
    private long file_size;
    private long file_date;

    public FileDetail(){

    }

    public FileDetail(String file_path,int file_pic,String file_name,long file_size,long file_date){
        this.file_path=file_path;
        this.file_pic=file_pic;
        this.file_name=file_name;
        this.file_size=file_size;
        this.file_date=file_date;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public int getFile_pic() {
        return file_pic;
    }

    public void setFile_pic(int file_pic) {
        this.file_pic = file_pic;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public long getFile_date() {
        return file_date;
    }

    public void setFile_date(long file_date) {
        this.file_date = file_date;
    }

    @Override
    public String toString() {
        return "FileDetail{" +
                "file_path='" + file_path + '\'' +
                ", file_pic=" + file_pic +
                ", file_name='" + file_name + '\'' +
                ", file_size=" + file_size +
                ", file_date=" + file_date +
                '}';
    }
}
