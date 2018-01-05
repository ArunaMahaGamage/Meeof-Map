package com.meeof.meeof.model;

/**
 * Created by ransikadesilva on 12/13/17.
 */

public class Country {
    private String englishName;
    private String alpha2Code;

    public Country(String englishName, String alpha2Code) {
        this.englishName = englishName;
        this.alpha2Code = alpha2Code;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getAlpha2Code() {
        return alpha2Code;
    }

    public void setAlpha2Code(String alpha2Code) {
        this.alpha2Code = alpha2Code;
    }
}
