package com.leonardo.mqtt_client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.eclipse.paho.client.mqttv3.MqttException;

import static android.location.LocationManager.GPS_PROVIDER;

public class Main3Activity extends AppCompatActivity {

    MqttClient mqttClient;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        mqttClient = new MqttClient(getApplicationContext(), "device");

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(GPS_PROVIDER);

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mqttClient.publishToTopic("","",0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });


    }
}
