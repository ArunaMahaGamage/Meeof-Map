package com.meeof.meeof.model.event_invites.raw_emails_dto;

import java.util.List;

/**
 * Created by Anuja Ranwalage on 10/23/2017.
 */

public class RawEmailsResponse {


    private String status;
    private List<Data> data;

    public RawEmailsResponse(String status, List<Data> data) {
        this.status = status;
        this.data = data;
    }

    public RawEmailsResponse() {
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
}
