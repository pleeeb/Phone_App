package com.example.tcptest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PowerPage extends AppCompatActivity {
    private Button power_button;
    private String test1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_page);

        Settings.returnInstance().changeSettings(0,"",50,false);

        test1 = new Gson().toJson(Settings.returnInstance());

        System.out.println(test1);


        power_button = findViewById(R.id.powerButton2);
        power_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                power_button.setBackground(getResources().getDrawable(R.drawable.power_button_off,null));
                new StartConnection().execute(test1);
            }
        });
    }
}