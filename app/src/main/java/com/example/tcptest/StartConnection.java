package com.example.tcptest;

import android.os.AsyncTask;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class StartConnection extends AsyncTask<String,Void,Void> {

    private Socket socket;

    @Override
    protected Void doInBackground(String... params) {

        try {
            socket = null;
            socket = new Socket("192.168.0.28", 13215);
            OutputStream out = socket.getOutputStream();
            PrintWriter output = new PrintWriter(out);

            System.out.println("Sending Data to " + socket.getRemoteSocketAddress().toString());
            output.println(params[0]);
            output.flush();
            output.close();
            System.out.println("Data sent to " + socket.getRemoteSocketAddress().toString());

            socket.close();
            System.out.println("Socket Closed");

        } catch (UnknownHostException e) {
            System.out.println("Unknown Host Exception " + e + " occurred");
        } catch (IOException i) {
            System.out.println("IO Exception " + i + " occurred");
        }
        return null;
    }
}


