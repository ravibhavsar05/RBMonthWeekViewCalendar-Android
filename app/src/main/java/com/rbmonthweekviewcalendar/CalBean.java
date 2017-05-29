package com.rbmonthweekviewcalendar;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CalBean {


    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("date")
    @Expose
    private String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public CalBean(String id, String title, String date, String type) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.type = type;
    }

}

