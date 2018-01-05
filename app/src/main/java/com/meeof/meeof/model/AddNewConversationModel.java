package com.meeof.meeof.model;

/**
 * Created by Dharmesh on 11/23/2017.
 */

public class AddNewConversationModel
{
    private String status;
    private int groupID;

    public AddNewConversationModel(String status)
    {
        this.status=status;
    }
    public AddNewConversationModel(String status, int groupID)
    {
        this.groupID=groupID;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }
}
