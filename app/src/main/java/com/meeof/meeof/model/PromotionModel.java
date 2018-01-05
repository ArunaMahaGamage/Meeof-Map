package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Dharmesh on 12/11/2017.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class PromotionModel
{
    private boolean success;
    private boolean more;
    private PromotionsInsideModel data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    public PromotionsInsideModel getData() {
        return data;
    }

    public void setData(PromotionsInsideModel data) {
        this.data = data;
    }
}
