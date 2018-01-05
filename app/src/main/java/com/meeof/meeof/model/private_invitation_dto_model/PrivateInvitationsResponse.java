package com.meeof.meeof.model.private_invitation_dto_model;

/**
 * Created by ransikadesilva on 12/21/17.
 */

public class PrivateInvitationsResponse {


    private String status;
    private ReturnData returnData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ReturnData getReturnData() {
        return returnData;
    }

    public void setReturnData(ReturnData returnData) {
        this.returnData = returnData;
    }

    @Override
    public String toString() {
        return "PrivateInvitationsResponse{" +
                "status='" + status + '\'' +
                ", returnData=" + returnData +
                '}';
    }
}
