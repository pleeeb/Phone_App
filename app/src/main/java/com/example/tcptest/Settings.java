package com.example.tcptest;

import android.content.Context;

public class Settings {
    private static Settings instance;
    private static Context ctx;

    public int Command;
    public String Name;
    public double Volume;
    public boolean FullScreen;

    public Settings(Context context){
        ctx = context;
    }

    public static Settings returnInstance(){
        if(instance==null){
            instance = new Settings(ctx);
        }
        return instance;
    }

    public int getCommand() {
        return Command;
    }

    public void setCommand(int command) {
        Command = command;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

    public boolean isFullScreen() {
        return FullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        FullScreen = fullScreen;
    }

    public void changeSettings(int command, String name, double volume, boolean fullScreen){
        setCommand(command);
        setName(name);
        setVolume(volume);
        setFullScreen(fullScreen);
    }
}
