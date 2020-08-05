package com.example.tcptest;

import android.content.Intent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static androidx.core.content.ContextCompat.startActivity;

public class MyWebViewClient extends WebViewClient {
    public String requestReturn = "";
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
        requestReturn = request.getUrl().toString();
        if(requestReturn.contains("access_token")){
            System.out.println(requestReturn);
            view.destroy();
            return false;
        }
        return false;
    }
}
