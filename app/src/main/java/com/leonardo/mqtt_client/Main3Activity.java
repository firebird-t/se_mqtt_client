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

    float distance;

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
                sendLocation();
            }
        });


        //Thread de envio de dados para a nuvem
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    this.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//
//                // your code here
//
//            }
//        }.start();


    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            sendLocation();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("Changed", String.valueOf(status));

            sendLocation();
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    private void meterDistanceBetweenPoints(float latA, float lngA, float latB, float lngB) {

        /*float pk = (float) (180.f/Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        double double_tmp = 6366000 * tt;
        Log.d("Distância", String.valueOf(double_tmp));
        return double_tmp;*/

        Location locationA = new Location("point A");

        locationA.setLatitude(latA);
        locationA.setLongitude(lngA);

        Location locationB = new Location("point B");

        locationB.setLatitude(latB);
        locationB.setLongitude(lngB);

        distance = locationA.distanceTo(locationB);

        Log.d("Distância", String.valueOf(distance/1000)+" Km");
    }


    private void sendLocation(){

        Log.d("PUBLISH","Dados enviados");
        Log.d("Latitude", String.valueOf(latitude));
        Log.d("Longitude", String.valueOf(longitude));

        meterDistanceBetweenPoints((float)latitude, (float)longitude, (float)-3.73539,(float)-38.59264);
        if(mqttClient != null){
            try {
                JSONObject json = new JSONObject();
                JSONObject json2 = new JSONObject();
                json2.put("lon",longitude);
                json2.put("lat", latitude);
                json2.put("distance",distance);
                json2.put("name","mobile");
                json.put("d", json2);

                mqttClient.publishToTopic("iot-2/evt/gps/fmt/json", String.valueOf(json),0,false);
            } catch (MqttException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
