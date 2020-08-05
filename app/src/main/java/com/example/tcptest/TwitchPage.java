package com.example.tcptest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Set;

public class TwitchPage extends AppCompatActivity{
    private TwitchConnection t;
    private TableLayout live_table;
    private Handler handler;
    private Runnable tableUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitch_page);

        t = TwitchConnection.getInstance(getApplicationContext());
        live_table = findViewById(R.id.live_table);


        Button recreate = findViewById(R.id.recreatebutton);
        recreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                live_table.removeAllViews();
                Refresh();
            }
        });

        System.out.println(t.live_channels);

        handler = new Handler();

        tableUpdate = new Runnable() {
            @Override
            public void run() {
                System.out.println("Table refreshed");
                GenTable();
                handler.postDelayed(this,1000);
                if(live_table.getChildCount()==t.live_channels.size()&&!TwitchConnection.games.isEmpty()){
                    System.out.println("Handler removed");
                    handler.removeCallbacks(tableUpdate);
                }
            }
        };
        handler.post(tableUpdate);
    }

    @Override
    protected void onStop() {
        System.out.println("Stop called");
        super.onStop();
    }


    public void Refresh(){
        t.GetFollowers("");
        handler.post(tableUpdate);
    }

    public void GenTable() {
        live_table.removeAllViews();
        System.out.println(t.live_channels.size());
        for (int i = 0; i < t.live_channels.size(); i++) {
            TableRow row = new TableRow(this.getApplicationContext());

            TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT
            );
            tableRowParams.setMargins(10, 0, 10, 50);
            row.setLayoutParams(tableRowParams);

            final TextView channel_name = new TextView(this.getApplicationContext());
            channel_name.setPadding(0, 0, 30, 0);

            final TextView game = new TextView(this.getApplicationContext());
            game.setPadding(0, 0, 20, 0);

            final TextView viewers = new TextView(this.getApplicationContext());
            viewers.setPadding(0, 0, 10, 0);

            channel_name.setText(t.live_channels.get(i).get("user_name").toString());
            game.setText(t.games.get(t.live_channels.get(i).get("game_id").getAsString()));
            viewers.setText(t.live_channels.get(i).get("viewer_count").toString());
            System.out.println(channel_name.getText() + "    " + game.getText()+ "    "+viewers.getText());


            row.addView(channel_name);
            row.addView(game);
            row.addView(viewers);
            row.setBackgroundResource(R.drawable.table_border);
            row.setMinimumHeight(200);
            row.setVerticalGravity(Gravity.CENTER_VERTICAL);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Settings.returnInstance().changeSettings(1,channel_name.getText().toString(),20.0,false);
                    String payload = new Gson().toJson(Settings.returnInstance());
                    new StartConnection().execute(payload);
                    Intent intent = new Intent(TwitchPage.this, ChannelInfo.class);
                    intent.putExtra("channel",channel_name.getText());
                    intent.putExtra("game",game.getText());
                    intent.putExtra("view",viewers.getText());
                    startActivity(intent);
                }
            });
            live_table.addView(row, tableRowParams);
        }
    }
}
