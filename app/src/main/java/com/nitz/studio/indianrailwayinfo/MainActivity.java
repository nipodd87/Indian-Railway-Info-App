package com.nitz.studio.indianrailwayinfo;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nitz.studio.indianrailwayinfo.model.MainListModel;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private ListView listView;
    public int [] imgResource={R.drawable.pnr_status, R.drawable.train_status, R.drawable.train_route, R.drawable.search_train,
            R.drawable.fare_train,R.drawable.pnr_status,R.drawable.pnr_status,};

    public String [] mainListItem;
    //={"Let Us C","c++","JAVA","Jsp","Microsoft .Net","Android","PHP","Jquery","JavaScript"};
    public List<MainListModel> mainListModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        listView.setBackgroundColor(Color.TRANSPARENT);
        Resources res = getResources();
        mainListItem = res.getStringArray(R.array.status_name);
        mainListModelList = new ArrayList<>();
        int i=0;
        for (int imgR: imgResource){
            MainListModel model = new MainListModel();
            model.setImgResource(imgR);
            model.setMenuItem(mainListItem[i]);
            i++;
            mainListModelList.add(model);
        }
        listView.setAdapter(new CustomAdapter());
        listView.setOnItemClickListener(new CustomAdapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class CustomAdapter extends ArrayAdapter<MainListModel> implements AdapterView.OnItemClickListener {

        public CustomAdapter() {
            super(MainActivity.this, R.layout.activity_main_list, mainListModelList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.activity_main_list, parent, false);
            }

            ImageView imgRes = (ImageView) itemView.findViewById(R.id.imageView);
            TextView mainListItem = (TextView) itemView.findViewById(R.id.mainListItem);

            MainListModel model = mainListModelList.get(position);

            imgRes.setImageResource(model.getImgResource());
            mainListItem.setText(model.getMenuItem());

            return itemView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String selected = mainListItem[position];
            takeAction(selected);
        }

        private void takeAction(String selection) {

            switch (selection){
                case "PNR Status":
                    Intent intent = new Intent(MainActivity.this, PNRStatus.class);
                    startActivity(intent);
                    //   Toast.makeText(this, "PNR Status selected", Toast.LENGTH_LONG).show();
                    break;
                case "Train Live Status":
                    Intent liveStatusIntent = new Intent(MainActivity.this, TrainLiveStatus.class);
                    startActivity(liveStatusIntent);
                    break;
                case "Train Route":
                    Intent trainRouteIntent = new Intent(MainActivity.this, TrainRoute.class);
                    startActivity(trainRouteIntent);
                    break;
                case "Search Trains":
                    Intent searchTrainIntent = new Intent(MainActivity.this, SearchTrain.class);
                    startActivity(searchTrainIntent);
                    break;
                case "Fare Enquiry":
                    Intent fareEnquiryIntent = new Intent(MainActivity.this, FareEnquiry.class);
                    startActivity(fareEnquiryIntent);
                    break;
                case "Seat Availability":
                    Intent seatAvailIntent = new Intent(MainActivity.this, SeatAvail.class);
                    startActivity(seatAvailIntent);
                    break;
                case "Cancelled Trains":
                    Intent cancelledTrainIntent = new Intent(MainActivity.this, CancelledTrain.class);
                    startActivity(cancelledTrainIntent);
                    break;
            }
        }

    }
}
