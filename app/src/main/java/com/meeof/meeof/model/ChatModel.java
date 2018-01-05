package com.meeof.meeof.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dharmesh on 11/23/2017.
 */

public class ChatModel
{
    private int SenderId;
    private String SenderName;
    private String Message;
    private String Timestamp;
    private String SenderImage;
    private int GroupId;
    private String PreviousDate;
    private boolean IsSame;
    private boolean IsFromIncoming;
    public ChatModel(int SenderId, String SenderName, String Message, String Timestamp, String SenderImage, int GroupId, String PreviousDate, boolean IsSame, boolean IsFromIncoming)
    {
        this.SenderId=SenderId;
        this.SenderName=SenderName;
        this.Message=Message;
        this.Timestamp=Timestamp;
        this.SenderImage=SenderImage;
        this.GroupId=GroupId;
        this.PreviousDate=PreviousDate;
        this.IsSame=IsSame;
        this.IsFromIncoming=IsFromIncoming;
    }


    public int getSenderId() {
        return SenderId;
    }

    public void setSenderId(int senderId) {
        SenderId = senderId;
    }

    public String getSenderName() {
        return SenderName;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getSenderImage() {
        return SenderImage;
    }

    public void setSenderImage(String senderImage) {
        SenderImage = senderImage;
    }

    public String toJson() {

        String data="";
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("sender_id",SenderId);
            jsonObject.put("Sender_name",SenderName);
            jsonObject.put("Message",Message);
            jsonObject.put("Sender_image",SenderImage);
            jsonObject.put("timestamp",Timestamp);
            jsonObject.put("group_id",getGroupId());
            data=jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public int getGroupId() {
        return GroupId;
    }

    public void setGroupId(int groupId) {
        GroupId = groupId;
    }

    public String getPreviousDate() {
        return PreviousDate;
    }

    public void setPreviousDate(String previousDate) {
        PreviousDate = previousDate;
    }

    public boolean isSame() {
        return IsSame;
    }

    public void setSame(boolean same) {
        IsSame = same;
    }

    public boolean isFromIncoming() {
        return IsFromIncoming;
    }

    public void setFromIncoming(boolean fromIncoming) {
        IsFromIncoming = fromIncoming;
    }
}
