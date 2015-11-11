package com.nitz.studio.indianrailwayinfo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by nitinpoddar on 11/7/15.
 */
public class CancelledTrain extends ActionBarActivity {

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled);

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        webView = (WebView) findViewById(R.id.webview01);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebClient());
        webView.loadUrl("http://enquiry.indianrail.gov.in/ntes/");

    }

    public class MyWebClient extends WebViewClient{

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            progressBar.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

            webView.loadUrl("javascript:(function(){" +
                    "var l=document.getElementById('ui-id-5');" +
                    "var e=document.createEvent('HTMLEvents');" +
                    "for(var i=0;i<l.length;i++) {" +
                    "var e1 = l[i];" +
                    "if (e1.href ==#tabs-5" +
                    "e.initEvent('click',true,true);" +
                    "l.dispatchEvent(e);" +
                    "}})()");
            progressBar.setVisibility(View.GONE);
        }


    }

    // To handle "Back" key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

