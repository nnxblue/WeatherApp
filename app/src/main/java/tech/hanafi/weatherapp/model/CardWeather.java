package tech.hanafi.weatherapp.model;

/**
 * Created by han.afi on 10/3/17.
 */

public class CardWeather {
    private int iconDrawableId;
    private String time;
    private String summary;
    private String temp;

    public CardWeather() {
    }

    public CardWeather(int iconDrawableId, String time, String summary, String temp) {
        this.iconDrawableId = iconDrawableId;
        this.time = time;
        this.summary = summary;
        this.temp = temp;
    }

    public int getIconDrawableId() {
        return iconDrawableId;
    }

    public void setIconDrawableId(int iconDrawableId) {
        this.iconDrawableId = iconDrawableId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
