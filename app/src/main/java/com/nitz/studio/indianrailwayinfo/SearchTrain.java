package com.nitz.studio.indianrailwayinfo;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.nitz.studio.indianrailwayinfo.model.TrainModel;
import com.nitz.studio.indianrailwayinfo.parser.SearchTrainParser;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by nitinpoddar on 11/2/15.
 */
public class SearchTrain extends ActionBarActivity {

    private EditText sourceTxt;
    private EditText destinationTxt;
    private EditText dateipTxt;
    private ProgressBar progressBar;
    private TextView availableTrainTxt;
    private TextView totalTrain;
    private TextView totalTrainTxt;
    private ListView listView2;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("ddMM");
    private Calendar myCalendar = Calendar.getInstance();
    public List<TrainModel> trainModelList;

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchtrain);

        sourceTxt = (EditText) findViewById(R.id.sourceTxt);
        destinationTxt = (EditText) findViewById(R.id.destinationTxt);

        dateipTxt = (EditText) findViewById(R.id.dateipTxt);
        updateLabel();
        dateipTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SearchTrain.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        availableTrainTxt = (TextView) findViewById(R.id.availableTrainTxt);
        availableTrainTxt.setVisibility(View.INVISIBLE);

        totalTrain = (TextView) findViewById(R.id.totalTrain);
        totalTrain.setVisibility(View.INVISIBLE);

        totalTrainTxt = (TextView) findViewById(R.id.totalTrainTxt);
        totalTrainTxt.setVisibility(View.INVISIBLE);

        listView2 = (ListView) findViewById(R.id.listView2);
        listView2.setVisibility(View.INVISIBLE);

    }

    public void getTrainList(View view) {

        availableTrainTxt.setVisibility(View.INVISIBLE);
        totalTrain.setVisibility(View.INVISIBLE);
        totalTrainTxt.setVisibility(View.INVISIBLE);
        listView2.setVisibility(View.INVISIBLE);

        String tempDate = dateipTxt.getText().toString();
        String selectedDate = tempDate.substring(0,2) +"-"+ tempDate.substring(3,5);

        String url = "http://api.railwayapi.com/between/source/" + sourceTxt.getText().toString() + "/dest/" + destinationTxt.getText().toString()
                + "/date/"+selectedDate+ "/apikey/" + IndianRailwayInfo.API_KEY + "/";
        MyTask task = new MyTask();
        task.execute(url);
    }


    protected void updateLabel(){

        dateipTxt.setText(sdf.format(myCalendar.getTime()));
    }


    public class MyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
            availableTrainTxt.setVisibility(View.INVISIBLE);
            totalTrain.setVisibility(View.INVISIBLE);
            totalTrainTxt.setVisibility(View.INVISIBLE);
            listView2.setVisibility(View.INVISIBLE);
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
            progressBar.setVisibility(View.INVISIBLE);

            availableTrainTxt.setVisibility(View.VISIBLE);
            totalTrain.setVisibility(View.VISIBLE);
            totalTrainTxt.setVisibility(View.VISIBLE);
            listView2.setVisibility(View.VISIBLE);

            trainModelList = SearchTrainParser.parseFeed(s);
            totalTrainTxt.setText(""+TrainModel.totalTrain);

            ArrayAdapter<TrainModel> adapter = new TrainModelAdapter();
            listView2.setAdapter(adapter);
        }

        public class TrainModelAdapter extends ArrayAdapter<TrainModel>{

            public TrainModelAdapter() {
                super(SearchTrain.this, R.layout.train_search_item_layout, trainModelList);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemView = convertView;
                if(itemView == null){
                    itemView = getLayoutInflater().inflate(R.layout.train_search_item_layout, parent, false);
                }

                TextView trainNumber = (TextView) itemView.findViewById(R.id.trainNumberTxt);
                TextView trainName = (TextView) itemView.findViewById(R.id.nameTxt);
                TextView deptTime = (TextView) itemView.findViewById(R.id.dept);
                TextView arrivesTime = (TextView) itemView.findViewById(R.id.arrives);
                TextView runDays = (TextView) itemView.findViewById(R.id.runsOn);

                TrainModel trainModel = trainModelList.get(position);

                trainNumber.setText(trainModel.getTrainNumber());
                trainName.setText(trainModel.getTrainName());
                deptTime.setText(trainModel.getDeptTime());
                arrivesTime.setText(trainModel.getArrivalTime());
                runDays.setText(trainModel.getDaysRun());

                return itemView;
            }
        }
    }
}