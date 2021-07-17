package com.sample.coronafree;

public class UserHelperClass {
    private String name;
    private String place;
    boolean infected = false;
    Double latitude = 0.0;
    Double longitude = 0.0;
    String id = "";
    String fsm = "";

//    public UserHelperClass(String name, String place, boolean infected, Double latitude, Double longitude, String id, String fsm) {
//        this.name = name;
//        this.place = place;
//        this.infected = infected;
//        this.latitude = latitude;
//        this.longitude = longitude;
//        this.id = id;
//        this.fsm = fsm;
//    }

    public UserHelperClass(){

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public boolean isInfected() {
        return infected;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFsm() {
        return fsm;
    }

    public void setFsm(String fsm) {
        this.fsm = fsm;
    }
}
