package com.meeof.meeof.model.addUpdate;

import java.io.Serializable;

/**
 * Created by ransikadesilva on 12/21/17.
 */

public class AddUpdateResponse implements Serializable {
    private String status;
    private String data;

    public AddUpdateResponse(){}
    public AddUpdateResponse(String status, String data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
