package com.nitz.studio.indianrailwayinfo.model;

/**
 * Created by nitinpoddar on 10/31/15.
 */
public class TrainStatus {

    private int serialNo;
    private String scheduleArrival;
    private String actualArrival;
    private String stationName;
    private String statusOfArrival;
    public static int responseCode;
    public static int totalStation;
    public static String errorCode;

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public String getScheduleArrival() {
        return scheduleArrival;
    }

    public void setScheduleArrival(String scheduleArrival) {
        this.scheduleArrival = scheduleArrival;
    }

    public String getActualArrival() {
        return actualArrival;
    }

    public void setActualArrival(String actualArrival) {
        this.actualArrival = actualArrival;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStatusOfArrival() {
        return statusOfArrival;
    }

    public void setStatusOfArrival(String statusOfArrival) {
        this.statusOfArrival = statusOfArrival;
    }
}
