package com.nitz.studio.indianrailwayinfo;

import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by nitinpoddar on 10/26/15.
 */
public class HttpManager {
    public static String getData(String data) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuffer content = new StringBuffer();
        try {
            URL url = new URL(data);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            connection.connect();
                if (connection.getResponseCode() >= 400 && connection.getResponseCode() < 500)
                {
                    return "ERROR! \n Please check the value entered and try again.";
                }
            else if(connection.getResponseCode() >= 500){
                    return  "ERROR! \n Server Unavailable. Please try after sometime.";
                }
            else {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    return content.toString();
                }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
        finally {
            if (connection!=null){
                connection.disconnect();
            }
            if (reader != null){
                reader.close();
            }
        }
    }
}
