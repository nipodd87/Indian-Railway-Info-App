package com.nitz.studio.indianrailwayinfo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nitz.studio.indianrailwayinfo.model.FareTrainModel;
import com.nitz.studio.indianrailwayinfo.model.SearchStationModel;
import com.nitz.studio.indianrailwayinfo.parser.FareTrainParser;
import com.nitz.studio.indianrailwayinfo.parser.SearchStationParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by nitinpoddar on 11/4/15.
 */
public class FareEnquiry extends ActionBarActivity {

    private AutoCompleteTextView sourceTxt;
    private AutoCompleteTextView destinationTxt;
    private EditText dateipTxt;
    private ProgressBar progressBar;
    private ListView fareList;
    private EditText trainNoTxt;
    private TextView nameTxt;
    private CheckBox tatkalCheckBox;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
    private Calendar myCalendar = Calendar.getInstance();
    public List<FareTrainModel> fareTrainModelList;

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
        setContentView(R.layout.activity_fareenquiry);


        sourceTxt = (AutoCompleteTextView) findViewById(R.id.sourceTxt);
        destinationTxt = (AutoCompleteTextView) findViewById(R.id.destinationTxt);
        sourceTxt.setThreshold(2);
        destinationTxt.setThreshold(2);
        sourceTxt.setAdapter(new StationSearchAdapter(this));
        destinationTxt.setAdapter(new StationSearchAdapter(this));
        sourceTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchStationModel searchStationModel = (SearchStationModel) parent.getItemAtPosition(position);
                sourceTxt.setText(searchStationModel.getStationCode());
            }
        });
        destinationTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchStationModel searchStationModel = (SearchStationModel) parent.getItemAtPosition(position);
                destinationTxt.setText(searchStationModel.getStationCode());
            }
        });
        dateipTxt = (EditText) findViewById(R.id.dateipTxt);
        trainNoTxt = (EditText) findViewById(R.id.trainNoTxt);
        nameTxt = (TextView) findViewById(R.id.nameTxt);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        fareList = (ListView) findViewById(R.id.fareList);

        progressBar.setVisibility(View.INVISIBLE);
        nameTxt.setVisibility(View.INVISIBLE);

        tatkalCheckBox = (CheckBox) findViewById(R.id.tatkalCheckBox);
        dateipTxt.setText(sdf.format(myCalendar.getTime()));

        dateipTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(FareEnquiry.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }
    public void getFare(View view){

        String isTatkal = isTatkalChecked();
        String dateSelected = sdf2.format(myCalendar.getTime());

        String url = "http://api.railwayapi.com/fare/train/" +
                trainNoTxt.getText().toString()+"/source/"+sourceTxt.getText().toString()+"/dest/"+
                destinationTxt.getText().toString()+"/age/18/quota/"+isTatkal+"/doj/"+dateSelected+
                "/apikey/"+IndianRailwayInfo.API_KEY+"/";

        MyTask task = new MyTask();
        task.execute(url);
    }
    public void updateLabel(){
        dateipTxt.setText(sdf.format(myCalendar.getTime()));
    }

    public String isTatkalChecked(){
        if (tatkalCheckBox.isChecked())
            return "PT";
        else
            return "0";
    }
    public class MyTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            fareList.setVisibility(View.INVISIBLE);
            nameTxt.setVisibility(View.INVISIBLE);

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
            fareList.setVisibility(View.VISIBLE);
            nameTxt.setVisibility(View.VISIBLE);

            fareTrainModelList = null;
            fareTrainModelList = FareTrainParser.parseFeed(s);

            String TrainNameNo = FareTrainModel.trainNo + " / " + FareTrainModel.trainName;
            nameTxt.setText(TrainNameNo);

            ArrayAdapter<FareTrainModel> adapter = new FareTrainAdapter();
            fareList.setAdapter(adapter);
        }
    }
        public class FareTrainAdapter extends ArrayAdapter<FareTrainModel>{

            public FareTrainAdapter() {
                super(FareEnquiry.this, R.layout.train_fare_item_layout, fareTrainModelList);
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemView = convertView;
                if(itemView == null){
                    itemView = getLayoutInflater().inflate(R.layout.train_fare_item_layout, parent, false);
                }

                TextView classCode = (TextView)itemView.findViewById(R.id.classCode);
                TextView className = (TextView)itemView.findViewById(R.id.className);
                TextView farePrice = (TextView)itemView.findViewById(R.id.farePrice);

                FareTrainModel model = fareTrainModelList.get(position);

                classCode.setText(model.getClassCode());
                className.setText(model.getClassName());
                farePrice.setText(model.getClassFare());

                return itemView;
            }
        }
        public class StationSearchAdapter extends BaseAdapter implements Filterable{

            public List<SearchStationModel> resultList = new ArrayList<SearchStationModel>();
            private Context mContext;

            public StationSearchAdapter(Context context){
                mContext = context;
            }
            @Override
            public int getCount() {
                return resultList.size();
            }

            @Override
            public SearchStationModel getItem(int position) {
                return resultList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null){
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
                }
                SearchStationModel model = resultList.get(position);
                TextView stationName = (TextView)convertView.findViewById(android.R.id.text1);
                stationName.setText(model.getStationName()+"-"+model.getStationCode());

                return convertView;
            }

            @Override
            public Filter getFilter() {
                Filter filter = new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults filterResults = new FilterResults();
                        if(constraint != null){
                            List<SearchStationModel> searchStationModels = findStationName(mContext, constraint.toString());
                            filterResults.values = searchStationModels;
                            filterResults.count = searchStationModels.size();
                        }
                        return filterResults;
                        }
                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        if (results != null && results.count>0){
                            resultList = (List<SearchStationModel>) results.values;
                            notifyDataSetChanged();
                        } else{
                            notifyDataSetInvalidated();
                        }
                    }
                };
                return filter;
            }
            private List<SearchStationModel> findStationName(Context context, String stationCode){
                String stationUrl = "http://api.railwayapi.com/suggest_station/name/"+stationCode+"/apikey/"+
                        IndianRailwayInfo.API_KEY+"/";
                try {
                    String content = HttpManager.getData(stationUrl);
                    List<SearchStationModel> resultModel = SearchStationParser.parseFeed(content);
                    return resultModel;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

            }
        }
}
