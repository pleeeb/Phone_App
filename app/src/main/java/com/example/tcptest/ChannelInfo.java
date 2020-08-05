package com.example.tcptest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;

public class ChannelInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_info);

        final TextView channel_name = findViewById(R.id.ChannelName);
        TextView game_name = findViewById(R.id.GameName);
        TextView view_count = findViewById(R.id.ViewCount);

        Bundle b = getIntent().getExtras();

        channel_name.setText(b.get("channel").toString());
        game_name.setText(b.get("game").toString());
        view_count.setText(b.get("view").toString());

        SeekBar volBar = findViewById(R.id.seekBar);
        volBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double vol = progress/100.0;
                Settings.returnInstance().changeSettings(2,channel_name.getText().toString(),vol,false);
                String payload = new Gson().toJson(Settings.returnInstance());
                new StartConnection().execute(payload);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button refresh = findViewById(R.id.refreshButton);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.returnInstance().setCommand(4);
                String payload = new Gson().toJson(Settings.returnInstance());
                new StartConnection().execute(payload);
            }
        });
        Button fullScreen = findViewById(R.id.fullScreenButton);
        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.returnInstance().setCommand(3);
                Settings.returnInstance().setFullScreen(true);
                String payload = new Gson().toJson(Settings.returnInstance());
                new StartConnection().execute(payload);
            }
        });


    }
}