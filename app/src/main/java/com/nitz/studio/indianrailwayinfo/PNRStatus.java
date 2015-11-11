package com.nitz.studio.indianrailwayinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by nitinpoddar on 10/26/15.
 */
public class PNRStatus extends ActionBarActivity {

    private EditText pnrStatusEditTxt;
    private ListView pnrStatusList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pnrstatus);
        pnrStatusEditTxt = (EditText) findViewById(R.id.pnrStatusEditTxt);
         }

    public void pnrStatusCheck(View view){
        String pnrNo = pnrStatusEditTxt.getText().toString();
        String url = "http://api.railwayapi.com/pnr_status/pnr/" + pnrNo + "/apikey/" + IndianRailwayInfo.API_KEY + "/";
        if (pnrNo.length()!=10){
            pnrStatusEditTxt.setText("");
            Toast.makeText(this, "Please enter valid 10 digit PNR no.", Toast.LENGTH_LONG).show();
        } else{
            if(isConnected()){
                Intent pnrStatusInt = new Intent(this, PNRStatusDisplay.class);
                pnrStatusInt.putExtra("url", url);
                startActivity(pnrStatusInt);
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
}
