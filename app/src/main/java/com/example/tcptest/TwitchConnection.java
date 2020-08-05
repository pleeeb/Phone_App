package com.example.tcptest;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.webkit.WebView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TwitchConnection implements Serializable {

    private static TwitchConnection instance;

    public static String auth_tok;
    public static String client_tok;
    private static Context c;


    public static ArrayList<Integer> follow_id = new ArrayList<>();
    private int follow_it = 0;
    public static ArrayList<JsonObject> live_channels = new ArrayList<>();
    private int live_it = 0;
    public static Map<String, String> games = new HashMap<>();

    private TwitchConnection(Context context){
        c = context;

    }

    public static TwitchConnection getInstance(Context context){
        if (instance == null){
            instance = new TwitchConnection(context);
        }
        return instance;
    }


    public void GetFollowers(String p) {
        //Twitch request for channels i am following, replaces after bearer with auth token
        String url = "https://api.twitch.tv/helix/users/follows?from_id=66974295&first=100&after="+p;
        if(follow_it==0) {
            follow_id.clear();
        }


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        JsonObject reply = gson.fromJson(response, JsonObject.class);
                        JsonArray follow = reply.getAsJsonArray("data");
                        System.out.println("Response is: " + response);

                        for(JsonElement entry : follow){
                            JsonObject id = entry.getAsJsonObject();
                            follow_id.add(id.get("to_id").getAsInt());
                        }

                        if (reply.has("pagination")&&reply.getAsJsonObject("pagination").has("cursor")){
                            follow_it += 1;
                            GetFollowers(reply.getAsJsonObject("pagination").get("cursor").getAsString());
                        }
                        else {
                            follow_it = 0;
                            GetLive();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                parseError(error);
                GetAppToken();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Client-ID", TwitchConnection.client_tok);
                params.put("Authorization", TwitchConnection.auth_tok);

                return params;
            }
        };
        Queue.getInstance(c).getRequestQueue().add(stringRequest);
    }

    public void GetLive(){
        String parameter = "";
        if(live_it==0) {
            live_channels.clear();
        }
        if(follow_id.size()>0) {
            int v = 0;
            while(v<99&&follow_id.size()>0){
                parameter += "&user_id=" + follow_id.get(0).toString();
                follow_id.remove(0);
                v++;
            }
            String url = "https://api.twitch.tv/helix/streams?first=100" + parameter;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JsonArray live = parseResponse(response);


                            for (JsonElement entry : live) {
                                JsonObject id = entry.getAsJsonObject();
                                live_channels.add(id);
                            }
                            if(follow_id.size()>0){
                                live_it+=1;
                                GetLive();
                            }
                            else {
                                live_it=0;
                                GetGames();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    parseError(error);

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Client-ID", TwitchConnection.client_tok);
                    params.put("Authorization", TwitchConnection.auth_tok);

                    return params;
                }
            };
            Queue.getInstance(c).getRequestQueue().add(stringRequest);
        }
    }


    public void GetAppToken() {
        //Generates new app auth token
        System.out.println("Get Token Running!");
        String url = "https://id.twitch.tv/oauth2/token?client_id="+TwitchConnection.client_tok+"&client_secret=y1zghhkpmwiv0s3l9w9s8cjjtievqe&grant_type=client_credentials";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != "") {
                            Gson gson = new Gson();
                            JsonObject reply = gson.fromJson(response, JsonObject.class);
                            TwitchConnection.auth_tok = "Bearer "+(reply.get("access_token").toString().replace("\"",""));
                            System.out.println("Response is: " + response);
                            System.out.println("Auth Token = "+TwitchConnection.auth_tok);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Failed!");
            }
        });
        Queue.getInstance(c).getRequestQueue().add(stringRequest);
    }

    public void CancelToken() {

        String url = "https://id.twitch.tv/oauth2/revoke?client_id="+TwitchConnection.client_tok+"&token="+TwitchConnection.auth_tok;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Response is: " + response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Failed!");
                System.out.println(error);
            }
        });
        Queue.getInstance(c).getRequestQueue().add(stringRequest);
    }

    public void GetGames(){
        String url = "https://api.twitch.tv/helix/games?";
        String id = "";
        for(int i=0; i<live_channels.size(); i++){
            String game = live_channels.get(i).get("game_id").getAsString();
            id += "id="+game+"&";
        }
        id = id.substring(0,id.length()-1);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonArray games_played = parseResponse(response);

                        for(JsonElement entry : games_played){
                            JsonObject game = entry.getAsJsonObject();
                            games.put(game.get("id").getAsString(),game.get("name").getAsString());
                        }
                        System.out.println(games);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                parseError(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Client-ID", TwitchConnection.client_tok);
                params.put("Authorization", TwitchConnection.auth_tok);
                return params;
            }
        };;
        Queue.getInstance(c).getRequestQueue().add(stringRequest);
    }

    public JsonArray parseResponse(String response){
        Gson gson = new Gson();
        JsonObject reply = gson.fromJson(response, JsonObject.class);
        JsonArray data = reply.getAsJsonArray("data");
        System.out.println("Response is: " + response);
        return data;
    }

    public void parseError(VolleyError error){
        System.out.println("Failed!");
        Gson gson = new Gson();
        try {
            String result = new String(error.networkResponse.data, "UTF-8");
            JsonObject reply = gson.fromJson(result, JsonObject.class);
            System.out.println(result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    //Insert this into an activity screen for web interface to run off view.
    /*public void GetUserToken(){
        MyWebViewClient c = new MyWebViewClient();
        WebView webview = MainActivity.(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(c);
        webview.loadUrl("https://id.twitch.tv/oauth2/authorize?client_id="+TwitchConnection.client_tok+"&redirect_uri=http://localhost:3000&response_type=token&scope=user:read:broadcast");
    }*/


}
