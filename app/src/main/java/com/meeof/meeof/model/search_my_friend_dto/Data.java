package com.meeof.meeof.model.search_my_friend_dto;

import java.io.Serializable;

public class Data implements Serializable {
    private String first_name;
    private int user_id;

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    @Override
    public String toString() {
        return "Data{" +
                "user_id=" + user_id +
                ", first_name='" + first_name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {


        if (obj == null) {
            return false;
        }
        if (!com.meeof.meeof.model.search_my_friend_dto.Data.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final com.meeof.meeof.model.search_my_friend_dto.Data other = (com.meeof.meeof.model.search_my_friend_dto.Data) obj;
        if (this.user_id != other.getUser_id()) {
            return false;
        }
        return true;


        //return super.equals(obj);
    }

}
