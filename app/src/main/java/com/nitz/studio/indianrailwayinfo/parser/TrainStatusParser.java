package com.nitz.studio.indianrailwayinfo.parser;

import com.nitz.studio.indianrailwayinfo.model.TrainStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpoddar on 10/31/15.
 */
public class TrainStatusParser {

    public static List<TrainStatus> parseFeed(String content){
        try {
            JSONObject jsonObject = new JSONObject(content);
            List<TrainStatus> trainList = new ArrayList<TrainStatus>();

            TrainStatus.errorCode = jsonObject.getString("error");
            TrainStatus.responseCode = jsonObject.getInt("response_code");
            TrainStatus.totalStation = jsonObject.getInt("total");

            JSONArray route = jsonObject.getJSONArray("route");

            for(int i=0;i<TrainStatus.totalStation;i++){
                TrainStatus trainStatus = new TrainStatus();
                JSONObject routeObject = route.getJSONObject(i);
                trainStatus.setSerialNo(routeObject.getInt("no"));
                trainStatus.setScheduleArrival(routeObject.getString("scharr"));
                trainStatus.setActualArrival(routeObject.getString("actarr"));
                trainStatus.setStationName(routeObject.getString("station"));
                trainStatus.setStatusOfArrival(routeObject.getString("status"));
                trainList.add(trainStatus);
            }
            return trainList;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
