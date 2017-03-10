package tech.hanafi.weatherapp.model;

/**
 * Created by han.afi on 10/3/17.
 */

public class CardWeather {
    private String time;
    private String summary;
    private String temp;

    public CardWeather() {
    }

    public CardWeather(String time, String summary, String temp) {
        this.time = time;
        this.summary = summary;
        this.temp = temp;
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
