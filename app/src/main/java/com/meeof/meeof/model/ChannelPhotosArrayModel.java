package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Dharmesh on 12/19/2017.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ChannelPhotosArrayModel
{
    private String file_name;
    private String type;

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
