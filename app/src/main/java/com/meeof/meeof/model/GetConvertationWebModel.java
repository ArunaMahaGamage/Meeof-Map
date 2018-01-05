package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.meeof.meeof.model.friends_all_dto.Pending_requests;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Dharmesh on 11/23/2017.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class GetConvertationWebModel implements Serializable {



    private String status;
    private ConvertationDataModel data;


    public GetConvertationWebModel() {
    }

    public GetConvertationWebModel(String status, ConvertationDataModel convertationDataModels, List<Pending_requests> pending_requests) {
        this.status = status;
        this.data =convertationDataModels;

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ConvertationDataModel getData() {
        return data;
    }

    public void setData(ConvertationDataModel data) {
        this.data = data;
    }
}

