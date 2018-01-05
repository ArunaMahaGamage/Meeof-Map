package com.meeof.meeof.model.countries_dto;

import java.util.List;

/**
 * Created by ransikadesilva on 10/5/17.
 */

public class CountriesResponse {




    private String status;
    private List<Data> data;

    public CountriesResponse(String status, List<Data> data) {
        this.status = status;
        this.data = data;
    }

    public CountriesResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CountriesResponse{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
