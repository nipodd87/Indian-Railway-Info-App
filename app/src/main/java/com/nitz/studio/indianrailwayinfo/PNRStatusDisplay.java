package com.nitz.studio.indianrailwayinfo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nitz.studio.indianrailwayinfo.model.PNRStatusModel;
import com.nitz.studio.indianrailwayinfo.parser.PNRParser;

import java.io.IOException;
import java.util.List;

/**
 * Created by nitinpoddar on 10/27/15.
 */
public class PNRStatusDisplay extends ActionBarActivity {

    private ProgressBar progressBar;
    private TextView trainNoTxt;
    private TextView trainNo;
    private TextView trainNameTxt;
    private TextView trainName;
    private TextView boardingDateTxt;
    private TextView boardingDate;
    private TextView fromTxt;
    private TextView from;
    private TextView toTxt;
    private TextView to;
    private TextView reservedUptoTxt;
    private TextView reservedUpto;
    private TextView boardingPointTxt;
    private TextView boardingPoint;
    private TextView class1Txt;
    private TextView class1;
    private TextView journeyDetail;
    private TextView passengerInfo;
    private TextView chartPrepTxt;

    private ListView pnrStatusList;
    public List<PNRStatusModel> pnrStatusModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnrdisplay);

        trainNo = (TextView) findViewById(R.id.trainNo);
        trainName = (TextView) findViewById(R.id.trainName);
        boardingDate = (TextView) findViewById(R.id.boardingDate);
        from = (TextView) findViewById(R.id.from);
        to = (TextView) findViewById(R.id.to);
        reservedUpto = (TextView) findViewById(R.id.reservedUpto);
        boardingPoint = (TextView) findViewById(R.id.boardingPoint);
        class1 = (TextView) findViewById(R.id.class1);
        journeyDetail = (TextView) findViewById(R.id.journeyDetail);
        passengerInfo = (TextView) findViewById(R.id.passengerInfo);

        trainNoTxt = (TextView) findViewById(R.id.trainNoTxt);
        trainNameTxt = (TextView) findViewById(R.id.trainNameTxt);
        boardingDateTxt = (TextView) findViewById(R.id.boardingDateTxt);
        fromTxt = (TextView) findViewById(R.id.fromTxt);
        toTxt = (TextView) findViewById(R.id.toTxt);
        reservedUptoTxt = (TextView) findViewById(R.id.reservedUptoTxt);
        boardingPointTxt = (TextView) findViewById(R.id.boardingPointTxt);
        class1Txt = (TextView) findViewById(R.id.class1Txt);
        chartPrepTxt = (TextView) findViewById(R.id.chartPrepTxt);

        pnrStatusList = (ListView) findViewById(R.id.pnrStatusList);
        pnrStatusList.setVisibility(View.INVISIBLE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        trainNo.setVisibility(View.INVISIBLE);
        trainName.setVisibility(View.INVISIBLE);
        boardingDate.setVisibility(View.INVISIBLE);
        from.setVisibility(View.INVISIBLE);
        to.setVisibility(View.INVISIBLE);
        reservedUpto.setVisibility(View.INVISIBLE);
        boardingPoint.setVisibility(View.INVISIBLE);
        journeyDetail.setVisibility(View.INVISIBLE);
        passengerInfo.setVisibility(View.INVISIBLE);
        chartPrepTxt.setVisibility(View.INVISIBLE);


        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        MyTask task = new MyTask();
        task.execute(url);
    }

    public class MyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            pnrStatusList.setVisibility(View.INVISIBLE);

            trainNoTxt.setVisibility(View.INVISIBLE);
            trainNameTxt.setVisibility(View.INVISIBLE);
            boardingDateTxt.setVisibility(View.INVISIBLE);
            fromTxt.setVisibility(View.INVISIBLE);
            toTxt.setVisibility(View.INVISIBLE);
            reservedUptoTxt.setVisibility(View.INVISIBLE);
            boardingPointTxt.setVisibility(View.INVISIBLE);
            class1Txt.setVisibility(View.INVISIBLE);
            journeyDetail.setVisibility(View.INVISIBLE);
            passengerInfo.setVisibility(View.INVISIBLE);

            trainNo.setVisibility(View.INVISIBLE);
            trainName.setVisibility(View.INVISIBLE);
            boardingDate.setVisibility(View.INVISIBLE);
            from.setVisibility(View.INVISIBLE);
            to.setVisibility(View.INVISIBLE);
            reservedUpto.setVisibility(View.INVISIBLE);
            boardingPoint.setVisibility(View.INVISIBLE);
            class1.setVisibility(View.INVISIBLE);
            chartPrepTxt.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String data = null;
            try {
                data = HttpManager.getData(params[0]);
                return data;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {

            String jsonData = s;
            if (s == null){
                Toast.makeText(PNRStatusDisplay.this, "Unable to fecth response from server", Toast.LENGTH_LONG).show();
                return;
            }
            if (s.contains("ERROR!")){
                Toast.makeText(PNRStatusDisplay.this, s, Toast.LENGTH_LONG).show();
            }else {

                pnrStatusModelList = PNRParser.parseFeed(jsonData);

                progressBar.setVisibility(View.INVISIBLE);

                if (PNRStatusModel.error == true) {
                    Toast.makeText(PNRStatusDisplay.this, "Error in fetching status !! \n Please verify input and try again", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(PNRStatusDisplay.this, PNRStatus.class);
                    startActivity(intent);

                } else {
                    pnrStatusList.setVisibility(View.VISIBLE);
                    //make details visible
                    trainNoTxt.setVisibility(View.VISIBLE);
                    trainNameTxt.setVisibility(View.VISIBLE);
                    boardingDateTxt.setVisibility(View.VISIBLE);
                    fromTxt.setVisibility(View.VISIBLE);
                    toTxt.setVisibility(View.VISIBLE);
                    reservedUptoTxt.setVisibility(View.VISIBLE);
                    boardingPointTxt.setVisibility(View.VISIBLE);
                    class1Txt.setVisibility(View.VISIBLE);

                    trainNo.setVisibility(View.VISIBLE);
                    trainName.setVisibility(View.VISIBLE);
                    boardingDate.setVisibility(View.VISIBLE);
                    from.setVisibility(View.VISIBLE);
                    to.setVisibility(View.VISIBLE);
                    reservedUpto.setVisibility(View.VISIBLE);
                    boardingPoint.setVisibility(View.VISIBLE);
                    class1.setVisibility(View.VISIBLE);
                    journeyDetail.setVisibility(View.VISIBLE);
                    passengerInfo.setVisibility(View.VISIBLE);
                    chartPrepTxt.setVisibility(View.VISIBLE);


                    //Update Journey Details
                    trainNoTxt.setText(PNRStatusModel.train_num);
                    trainNameTxt.setText(PNRStatusModel.train_name);
                    boardingDateTxt.setText(PNRStatusModel.doj);
                    fromTxt.setText(PNRStatusModel.from_station_name);
                    toTxt.setText(PNRStatusModel.to_station_name);
                    reservedUptoTxt.setText(PNRStatusModel.reserved_upto_name);
                    boardingPointTxt.setText(PNRStatusModel.boarding_point_name);
                    class1Txt.setText(PNRStatusModel.class1);
                    if (PNRStatusModel.chart_prepared.equals("Y")){
                        chartPrepTxt.setText("CHART PREPARED");
                    }else{
                        chartPrepTxt.setText("CHART NOT PREPARED");
                    }

                    //Update Passengers Details
                    ArrayAdapter<PNRStatusModel> adapter = new PNRAdapter();
                    pnrStatusList.setAdapter(adapter);
                }
            }
        }
    }

    public class PNRAdapter extends ArrayAdapter<PNRStatusModel>{

        public PNRAdapter() {
            super(PNRStatusDisplay.this, R.layout.pnr_status_layout, pnrStatusModelList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.pnr_status_layout, parent, false);
            }
            TextView slNOTxt = (TextView) itemView.findViewById(R.id.slNOTxt);
            TextView bookingStatusTxt = (TextView) itemView.findViewById(R.id.bookingStatusTxt);
            TextView currentStatusTxt = (TextView) itemView.findViewById(R.id.currentStatusTxt);
            PNRStatusModel model = pnrStatusModelList.get(position);
            slNOTxt.setText(""+model.getSerialNo());
            bookingStatusTxt.setText(model.getBookingStatus());
            currentStatusTxt.setText(model.getCurrentStatus());

            return itemView;
        }
    }
}
