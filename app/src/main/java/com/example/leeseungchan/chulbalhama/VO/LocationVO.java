package com.example.leeseungchan.chulbalhama.VO;

import java.io.Serializable;

public class LocationVO implements Serializable {
    private int timeHour;
    private int timeMin;
    private double latitude;
    private double longitude;
    private String name;
    private String description;
    
    public LocationVO(){
        name = null;
        latitude = -1;
        longitude = -1;
        description = null;
        timeHour = -1;
        timeMin = -1;
    }
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    
    public void setTime(int timeHour, int timeMin) {
        this.timeHour = timeHour;
        this.timeMin = timeMin;
    }
    
    public String getTime(){
        StringBuffer time = new StringBuffer();
        if(timeHour < 0 || timeMin < 0){
            return null;
        }
        
        if(timeHour < 10){
            time.append("0");
        }
        time.append(timeHour + ":");
        
        if(timeMin < 10){
            time.append("0");
        }
        time.append(timeMin);
        
        return time.toString();
    }
    
    public int getTimeHour() {
        return timeHour;
    }
    
    public int getTimeMin() {
        return timeMin;
    }
    
    public String getCoordinate(){
        if(longitude < 0 || latitude < 0){
            return null;
        }
        return latitude + "," + longitude;
    }

    public void setLatitude(double lat){
        this.latitude = lat;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
