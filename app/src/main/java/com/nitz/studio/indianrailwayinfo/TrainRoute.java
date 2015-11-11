package com.nitz.studio.indianrailwayinfo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nitz.studio.indianrailwayinfo.model.TrainRouteStn;
import com.nitz.studio.indianrailwayinfo.parser.TrainRouteParser;
import java.io.IOException;
import java.util.List;

/**
 * Created by nitinpoddar on 10/31/15.
 */
public class TrainRoute extends ActionBarActivity {

    private ProgressBar progressBar;
    private ListView trainRoutList;
    private EditText trTxt;
    private TextView tr_nameTxt;
    private TextView tr_name;
    private TextView from_stnTxt;
    private TextView from_stn;
    private TextView daysTxt;
    private TextView dayRun;
    private Button statusButton;
    public List<TrainRouteStn> trainRouteStnList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainroute);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        trainRoutList = (ListView) findViewById(R.id.trainRoutList);
        trTxt = (EditText) findViewById(R.id.trTxt);
        statusButton = (Button) findViewById(R.id.statusButton);
        trainRoutList = (ListView) findViewById(R.id.trainRoutList);

        tr_nameTxt = (TextView) findViewById(R.id.tr_nameTxt);
        tr_name = (TextView) findViewById(R.id.tr_name);
        from_stnTxt = (TextView) findViewById(R.id.from_stnTxt);
        from_stn = (TextView) findViewById(R.id.from_stn);
        daysTxt = (TextView) findViewById(R.id.daysTxt);
        dayRun = (TextView) findViewById(R.id.dayRun);

        tr_nameTxt.setVisibility(View.INVISIBLE);
        tr_name.setVisibility(View.INVISIBLE);
        from_stnTxt.setVisibility(View.INVISIBLE);
        from_stn.setVisibility(View.INVISIBLE);
        daysTxt.setVisibility(View.INVISIBLE);
        dayRun.setVisibility(View.INVISIBLE);
        trainRoutList.setVisibility(View.INVISIBLE);

        trainRoutList = (ListView) findViewById(R.id.trainRoutList);

        progressBar.setVisibility(View.INVISIBLE);
    }

    public void getTrainRoute(View view){
        String trainNo = trTxt.getText().toString();
        if (trainNo.length()!=5){
            Toast.makeText(this, "Please enter valid 10 digit PNR no.", Toast.LENGTH_LONG).show();
        } else {
                if (isConnected()){
                    String url = "http://api.railwayapi.com/route/train/"+trainNo+"/apikey/"+IndianRailwayInfo.API_KEY+"/";
                    MyTask task = new MyTask();
                    task.execute(url);
                }else{
                    Toast.makeText(this, "No Network Connection", Toast.LENGTH_LONG).show();
                }
        }
    }
    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()){
            return true;
        }else {
            return false;
        }
    }

    public class MyTask extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            tr_nameTxt.setVisibility(View.INVISIBLE);
            tr_name.setVisibility(View.INVISIBLE);
            from_stnTxt.setVisibility(View.INVISIBLE);
            from_stn.setVisibility(View.INVISIBLE);
            daysTxt.setVisibility(View.INVISIBLE);
            dayRun.setVisibility(View.INVISIBLE);
            trainRoutList.setVisibility(View.INVISIBLE);
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                String content = HttpManager.getData(params[0]);
                return content;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.INVISIBLE);

            if (s == null){
                Toast.makeText(TrainRoute.this, "Unable to fecth response from server", Toast.LENGTH_LONG).show();
                return;
            }
            else if (s.contains("ERROR"))
            {
                    Toast.makeText(TrainRoute.this, s, Toast.LENGTH_LONG).show();
                    return;
            } else
            {
                trainRouteStnList = TrainRouteParser.parseFeed(s);
                if (TrainRouteStn.responseCode != 200 ){
                    Toast.makeText(TrainRoute.this, "Invalid Train Number", Toast.LENGTH_LONG).show();
                    return;
                } else{
                    tr_nameTxt.setVisibility(View.VISIBLE);
                    tr_name.setVisibility(View.VISIBLE);
                    from_stnTxt.setVisibility(View.VISIBLE);
                    from_stn.setVisibility(View.VISIBLE);
                    daysTxt.setVisibility(View.VISIBLE);
                    dayRun.setVisibility(View.VISIBLE);
                    trainRoutList.setVisibility(View.VISIBLE);

                    tr_name.setTextColor(Color.WHITE);
                    tr_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    tr_name.setText(TrainRouteStn.trainName);

                    from_stn.setTextColor(Color.BLACK);
                    from_stn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                    from_stn.setText(TrainRouteStn.fromStation);

                    dayRun.setTextColor(Color.WHITE);
                    dayRun.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                    dayRun.setText(TrainRouteStn.days);

                    ArrayAdapter<TrainRouteStn> adapter = new TrainRouteAdapter();
                    trainRoutList.setAdapter(adapter);
                }
            }
        }
    }

    public class TrainRouteAdapter extends ArrayAdapter<TrainRouteStn>{

        public TrainRouteAdapter() {
            super(TrainRoute.this, R.layout.train_route_item_layout, trainRouteStnList);
        }
            @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.train_route_item_layout, parent, false);
            }
            TextView stationName = (TextView) itemView.findViewById(R.id.stationNameTxt);
            TextView arrivesAt = (TextView) itemView.findViewById(R.id.arrivesAtTxt);
            TextView distance = (TextView) itemView.findViewById(R.id.distanceTxt);
            TextView day = (TextView) itemView.findViewById(R.id.dayTxt);

            TrainRouteStn trainRouteStn = trainRouteStnList.get(position);
            stationName.setText(trainRouteStn.getStationName());
            arrivesAt.setText(trainRouteStn.getScheduleArrival());
            distance.setText(""+trainRouteStn.getDistance());
            day.setText(""+trainRouteStn.getDay());
            return itemView;
            }
        }
    }

