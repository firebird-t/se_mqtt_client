package com.leonardo.mqtt_client;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import static android.location.LocationManager.GPS_PROVIDER;

public class Main3Activity extends AppCompatActivity {

    MqttClient mqttClient = null;
    Button button, btconnect;
    LocationListener locationListenerResult;
    double longitude;
    double latitude;

    //topic publish iot-2/type/device_type/id/device_id/cmd/command_id/fmt/format_string
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        //Localização
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = null;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

        //Conectar a IBM
        btconnect = findViewById(R.id.btconnect);
        btconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqttClient = new MqttClient(getApplicationContext(), "device", "publish");
            }
        });

        //Enviar Localização
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    JSONObject json = new JSONObject();
                    JSONObject json2 = new JSONObject();
                    json2.put("long",longitude * (-1));
                    json2.put("lat", latitude * (-1));
                    json.put("d", json2);

                    mqttClient.publishToTopic("iot-2/evt/gps/fmt/json", String.valueOf(json),0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Log.d("Latitude", String.valueOf(latitude));
                Log.d("Longitude", String.valueOf(latitude));
            }
        });


    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();


            Log.d("Latitude", String.valueOf(latitude));
            Log.d("Longitude", String.valueOf(latitude));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("Changed", String.valueOf(status));
            if(mqttClient != null){
                try {
                    JSONObject json = new JSONObject();
                    JSONObject json2 = new JSONObject();
                    json2.put("long",longitude * (-1));
                    json2.put("lat", latitude * (-1));
                    json.put("d", json2);

                    mqttClient.publishToTopic("iot-2/evt/gps/fmt/json", String.valueOf(json),0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            Log.d("Latitude", String.valueOf(latitude));
            Log.d("Longitude", String.valueOf(latitude));
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public static void setTimeoutSync(Runnable runnable, int delay) {
        try {
            Thread.sleep(delay);
            runnable.run();
        }
        catch (Exception e){
            System.err.println(e);
        }
    }
}
