package com.example.tcptest;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Queue {
    private static Queue instance;
    private static Context ctx;
    private RequestQueue requestQueue;

    private Queue(Context context){
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static Queue getInstance(Context context){
        if (instance == null){
            instance = new Queue(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue(){
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }
}
