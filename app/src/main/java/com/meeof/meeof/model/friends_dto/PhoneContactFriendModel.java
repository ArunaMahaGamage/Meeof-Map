package com.meeof.meeof.model.friends_dto;

/**
 * Created by ransikadesilva on 10/10/17.
 */

public class PhoneContactFriendModel {
    String uri;
    String name;
    String email;

    public PhoneContactFriendModel(String name, String phone,String uri) {
        this.uri = uri;
        this.name = name;
        this.email = phone;
    }

    @Override
    public String toString() {
        return "PhoneContactFriendModel{" +
                "uri='" + uri + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
