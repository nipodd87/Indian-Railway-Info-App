package com.nitz.studio.indianrailwayinfo;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nitz.studio.indianrailwayinfo.model.TrainStatus;
import com.nitz.studio.indianrailwayinfo.parser.TrainStatusParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


/**
 * Created by nitinpoddar on 10/28/15.
 */
public class TrainLiveStatus extends ActionBarActivity {

    private EditText tneditTxt;
    private Button yesterdayBtn;
    private Button todayBtn;
    private Button tomorrowBtn;
    private TextView trainLiveStatusTxt;
    private Calendar todayCalendar;
    private Calendar yesterdayCalendar;
    private Calendar tomorrowCalendar;
    private ProgressBar progressBar;
    private ListView statusListView;
    public List<TrainStatus> trainList;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainlivestatus);

        tneditTxt = (EditText) findViewById(R.id.tneditTxt);
        yesterdayBtn = (Button) findViewById(R.id.yesterdayBtn);
        todayBtn = (Button) findViewById(R.id.todayBtn);
        tomorrowBtn = (Button) findViewById(R.id.tomorrowBtn);
        statusListView = (ListView) findViewById(R.id.statusListView);
        trainLiveStatusTxt = (TextView) findViewById(R.id.trainLiveStatusTxt);
        trainLiveStatusTxt.setVisibility(View.INVISIBLE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        getCurrentDate();
    }
    public void getLiveStatus(View view) {
        int id = view.getId();
        String dateSelected = determineDateSelected(id);
        String trainNo = tneditTxt.getText().toString();

        if (trainNo.length() != 5) {
            tneditTxt.setText("");
            Toast.makeText(this, "Please enter valid 5 digit Train no.", Toast.LENGTH_SHORT).show();
        } else {
            if (isConnected()){
                String url = "http://api.railwayapi.com/live/train/" + trainNo + "/doj/" + dateSelected + "/apikey/" +
                        IndianRailwayInfo.API_KEY + "/";
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
    public String determineDateSelected(int id){
        Calendar cal1 = Calendar.getInstance();
        if (id == R.id.yesterdayBtn){
            cal1.add(Calendar.DATE,-1);
            return simpleDateFormat.format(cal1.getTime());
        }else if(id == R.id.todayBtn){
            return simpleDateFormat.format(cal1.getTime());
        }else{
            cal1.add(Calendar.DATE,1);
            return simpleDateFormat.format(cal1.getTime());
        }
    }
    public void getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        todayCalendar = Calendar.getInstance();
        String today = sdf.format(todayCalendar.getTime());

        yesterdayCalendar = Calendar.getInstance();
        yesterdayCalendar.add(Calendar.DATE, -1);
        String yesterday = sdf.format(yesterdayCalendar.getTime());

        tomorrowCalendar = Calendar.getInstance();
        tomorrowCalendar.add(Calendar.DATE, 1);
        String tomorrow = sdf.format(tomorrowCalendar.getTime());

        yesterdayBtn.setText(yesterday);
        todayBtn.setText(today);
        tomorrowBtn.setText(tomorrow);
    }

    public class MyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            trainLiveStatusTxt.setVisibility(View.INVISIBLE);
            statusListView.setVisibility(View.INVISIBLE);
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                String content = HttpManager.getData(params[0]);
                if (content == null){
                    return null;
                }
                return content;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {

            progressBar.setVisibility(View.INVISIBLE);
            trainList = TrainStatusParser.parseFeed(s);

            if (s == null) {
                Toast.makeText(TrainLiveStatus.this, "Unable to fecth response from server", Toast.LENGTH_LONG).show();
                return;
            } else if (s.contains("ERROR")) {
                Toast.makeText(TrainLiveStatus.this, s, Toast.LENGTH_LONG).show();
                return;
            } else {
                trainList = TrainStatusParser.parseFeed(s);
                if (TrainStatus.errorCode.contains("Invalid")) {
                    Toast.makeText(TrainLiveStatus.this, "Invalid Train Number or Date", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    statusListView.setVisibility(View.VISIBLE);
                    trainLiveStatusTxt.setVisibility(View.VISIBLE);

                    ArrayAdapter<TrainStatus> adapter = new MyTrainStatusAdapter();
                    statusListView.setAdapter(adapter);
                }
            }
        }
    }

    private class MyTrainStatusAdapter extends ArrayAdapter<TrainStatus>{

        public MyTrainStatusAdapter(){
            super(TrainLiveStatus.this, R.layout.train_status_item_view, trainList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.train_status_item_view, parent, false);
            }
            //find the item for each row
            TextView stationName = (TextView) itemView.findViewById(R.id.item_station);
            TextView status = (TextView) itemView.findViewById(R.id.item_status);
            TextView scheduleArrival = (TextView) itemView.findViewById(R.id.item_schedule);
            TextView actualArrival = (TextView) itemView.findViewById(R.id.item_actual);

            TrainStatus trainStatus = trainList.get(position);

            //fill each item
            stationName.setText(trainStatus.getStationName());
            String trainArrivalStatus = trainStatus.getStatusOfArrival();

            if (trainArrivalStatus.contains("Late"))
                status.setTextColor(Color.RED);
            else
                status.setTextColor(Color.BLACK);

            status.setText(trainStatus.getStatusOfArrival());
            scheduleArrival.setText(trainStatus.getScheduleArrival());
            actualArrival.setText(trainStatus.getActualArrival());

            return itemView;
        }
    }
}
