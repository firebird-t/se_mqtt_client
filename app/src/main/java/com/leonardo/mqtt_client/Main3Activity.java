package com.leonardo.mqtt_client;

import android.Manifest;
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

public class Main3Activity extends AppCompatActivity {

    MqttClient mqttClient = null;
    Button button, btconnect, btluzgeral, btdesligageral, btliga1, btliga2, btliga3, btdesliga1, btdesliga2, btdesliga3;
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

        mqttClient= null;

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
                mqttClient = new MqttClient(getApplicationContext(), "app", "publish");
            }
        });

        btluzgeral = findViewById(R.id.ligageral);
        btluzgeral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                JSONObject json2 = new JSONObject();

                try {
                    json2.put("command","LIGHTON");
                    json.put("d", json2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    mqttClient.publishToTopic("iot-2/type/node_mcu/id/node_mcu1/cmd/light/fmt/json", String.valueOf(json2), 0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        btdesligageral = findViewById(R.id.desligargeral);
        btdesligageral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                JSONObject json2 = new JSONObject();

                try {
                    json2.put("command","LIGHTOFF");
                    json.put("d", json2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    mqttClient.publishToTopic("iot-2/type/node_mcu/id/node_mcu1/cmd/light/fmt/json", String.valueOf(json2), 0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        btliga1 = findViewById(R.id.ligaluz1);
        btliga1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                JSONObject json2 = new JSONObject();

                try {
                    json2.put("command","LIGHT1ON");
                    json2.put("value1",100);
                    json.put("d", json2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    mqttClient.publishToTopic("iot-2/type/node_mcu/id/node_mcu1/cmd/light/fmt/json", String.valueOf(json2), 0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        btdesliga1 = findViewById(R.id.desligaluz1);
        btdesliga1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                JSONObject json2 = new JSONObject();

                try {
                    json2.put("command","LIGHT1OFF");
                    json.put("d", json2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    mqttClient.publishToTopic("iot-2/type/node_mcu/id/node_mcu1/cmd/light/fmt/json", String.valueOf(json2), 0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        btliga2 = findViewById(R.id.ligaluz2);
        btliga2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                JSONObject json2 = new JSONObject();

                try {
                    json2.put("command","LIGHT2ON");
                    json2.put("value2",100);
                    json.put("d", json2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    mqttClient.publishToTopic("iot-2/type/node_mcu/id/node_mcu1/cmd/light/fmt/json", String.valueOf(json2), 0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        btdesliga2 = findViewById(R.id.desligaluz2);
        btdesliga2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                JSONObject json2 = new JSONObject();

                try {
                    json2.put("command","LIGHT2OFF");
                    json.put("d", json2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    mqttClient.publishToTopic("iot-2/type/node_mcu/id/node_mcu1/cmd/light/fmt/json", String.valueOf(json2), 0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        btliga3 = findViewById(R.id.ligaluz3);
        btliga3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                JSONObject json2 = new JSONObject();

                try {
                    json2.put("command","LIGHT3ON");
                    json2.put("value3",100);
                    json.put("d", json2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    mqttClient.publishToTopic("iot-2/type/node_mcu/id/node_mcu1/cmd/light/fmt/json", String.valueOf(json2), 0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        btdesliga3 = findViewById(R.id.desligaluz3);
        btdesliga3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                JSONObject json2 = new JSONObject();

                try {
                    json2.put("command","LIGHT3OFF");
                    json.put("d", json2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    mqttClient.publishToTopic("iot-2/type/node_mcu/id/node_mcu1/cmd/light/fmt/json", String.valueOf(json2), 0,false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
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
        //-3.7379542,-38.4972275
        //meterDistanceBetweenPoints((float)latitude, (float)longitude, (float)-3.73539,(float)-38.59264);
        meterDistanceBetweenPoints((float)latitude, (float)longitude, (float)-3.7379542,(float)-38.4972275);
        if(mqttClient != null){
            try {
                JSONObject json = new JSONObject();
                JSONObject json2 = new JSONObject();
                json2.put("lon",longitude);
                json2.put("lat", latitude);
                json2.put("distance",distance);
                json2.put("name","mobile");
                json.put("d", json2);

                mqttClient.publishToTopic("iot-2/type/mobile/id/gps/evt/gps/fmt/json", String.valueOf(json),0,false);
            } catch (MqttException e) {
               e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
