package com.meeof.meeof.model.profile;

public class Country {
    private String mAlpha2_code;
    private String mEnglish_name;

    public String getAlpha2_code() {
        return mAlpha2_code;
    }

    public void setAlpha2_code(String mAlpha2_code) {
        this.mAlpha2_code = mAlpha2_code;
    }

    public String getEnglish_name() {
        return mEnglish_name;
    }

    public void setEnglish_name(String mEnglish_name) {
        this.mEnglish_name = mEnglish_name;
    }

    @Override
    public String toString() {
        return "Country{" +
                "mAlpha2_code='" + mAlpha2_code + '\'' +
                ", mEnglish_name='" + mEnglish_name + '\'' +
                '}';
    }
}
