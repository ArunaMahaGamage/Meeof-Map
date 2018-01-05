package com.meeof.meeof.model.update_image_dto;

public class Data {
    private int media_id;
    private int update_id;
    private int user_id;
    private String file_name;
    private String created_at;
    private String updated_at;
    private String exif_data;
    private int temp_file;

    public int getMedia_id() {
        return media_id;
    }

    public void setMedia_id(int media_id) {
        this.media_id = media_id;
    }

    public int getUpdate_id() {
        return update_id;
    }

    public void setUpdate_id(int update_id) {
        this.update_id = update_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getExif_data() {
        return exif_data;
    }

    public void setExif_data(String exif_data) {
        this.exif_data = exif_data;
    }

    public int getTemp_file() {
        return temp_file;
    }

    public void setTemp_file(int temp_file) {
        this.temp_file = temp_file;
    }
}
