package com.nitz.studio.indianrailwayinfo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.nitz.studio.indianrailwayinfo.model.SearchStationModel;
import com.nitz.studio.indianrailwayinfo.model.SeatAvailModel;
import com.nitz.studio.indianrailwayinfo.parser.SearchStationParser;
import com.nitz.studio.indianrailwayinfo.parser.SeatAvailParser;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by nitinpoddar on 11/6/15.
 */
public class SeatAvail extends ActionBarActivity implements AdapterView.OnItemSelectedListener{

    private AutoCompleteTextView sourceTxt;
    private AutoCompleteTextView destinationTxt;
    private EditText dateipTxt;
    private ProgressBar progressBar;
    private ListView seatAvalabilityList;
    private EditText trainNoTxt;
    private Spinner travelClass;
    private TextView nameTxt;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
    private Calendar myCalendar = Calendar.getInstance();
    private String [] classArray;
    private String [] classArrayRes = {"1A", "2A", "3A", "SL", "FC", "CC"};
    private String classSelected;
    public List<SeatAvailModel> modelList;

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
    public void updateLabel(){

        dateipTxt.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_avail);

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

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        nameTxt = (TextView) findViewById(R.id.nameTxt);
        nameTxt.setVisibility(View.INVISIBLE);

        seatAvalabilityList = (ListView) findViewById(R.id.seatList);
        seatAvalabilityList.setVisibility(View.INVISIBLE);

        dateipTxt.setText(sdf.format(myCalendar.getTime()));
        dateipTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SeatAvail.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        classArray = getResources().getStringArray(R.array.travel_class);

        // Spinner element
        travelClass = (Spinner) findViewById(R.id.travelClass);

        // Spinner click listener
        travelClass.setOnItemSelectedListener(this);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classArray);

        // attaching data adapter to spinner
        travelClass.setAdapter(dataAdapter);
    }

    public void getFare(View view){
        String dateSelected = sdf2.format(myCalendar.getTime());

        String url = "http://api.railwayapi.com/check_seat/train/" +
                trainNoTxt.getText().toString() + "/source/"+sourceTxt.getText().toString()+
                "/dest/"+destinationTxt.getText().toString()+"/date/"+dateSelected+
                "/class/"+classSelected+"/quota/GN/apikey/"+IndianRailwayInfo.API_KEY+"/";

        MyTask task =  new MyTask();
        task.execute(url);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int sid=travelClass.getSelectedItemPosition();
        classSelected = classArrayRes[sid];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        classSelected = classArrayRes[0];
    }

    public class MyTask extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            nameTxt.setVisibility(View.INVISIBLE);
            seatAvalabilityList.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String content = null;
            try {
                content = HttpManager.getData(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return content;
        }

        @Override
        protected void onPostExecute(String s) {

            progressBar.setVisibility(View.INVISIBLE);
            nameTxt.setVisibility(View.VISIBLE);
            seatAvalabilityList.setVisibility(View.VISIBLE);

            modelList = SeatAvailParser.parseFeed(s);
            nameTxt.setText(SeatAvailModel.trNo+" / "+SeatAvailModel.trName);

            ArrayAdapter<SeatAvailModel> adapter = new SeatAvailAdapter();
            seatAvalabilityList.setAdapter(adapter);
        }
    }
    public class SeatAvailAdapter extends ArrayAdapter<SeatAvailModel>{

        public SeatAvailAdapter() {
            super(SeatAvail.this, R.layout.seat_avail_item_view, modelList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.seat_avail_item_view, parent, false);
            }
            TextView travelDateTxt = (TextView) itemView.findViewById(R.id.travelDateTxt);
            TextView trainClassCodeTxt = (TextView) itemView.findViewById(R.id.trainClassCodeTxt);
            TextView travelStatusTxt = (TextView) itemView.findViewById(R.id.travelStatusTxt);

            SeatAvailModel model = modelList.get(position);

            travelDateTxt.setText(model.getTravelDate());
            travelStatusTxt.setText(model.getStatus());
            trainClassCodeTxt.setText(SeatAvailModel.classCode);

            return itemView;
        }
    }
    public class StationSearchAdapter extends BaseAdapter implements Filterable {

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
