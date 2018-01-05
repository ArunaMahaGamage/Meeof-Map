package com.meeof.meeof.model.countries_dto;

public class Data {
    private String alpha2_code;
    private String english_name;

    public String getalpha2_code() {
        return alpha2_code;
    }

    public void setalpha2_code(String alpha2Code) {
        this.alpha2_code = alpha2Code;
    }

    public String getenglish_name() {
        return english_name;
    }

    public void setenglish_name(String englishName) {
        this.english_name = englishName;
    }
}
