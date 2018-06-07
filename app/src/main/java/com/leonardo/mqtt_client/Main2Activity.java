package com.leonardo.mqtt_client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

//import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.*;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;


public class Main2Activity extends AppCompatActivity {

    MqttClient mqttHelper;
    TextView dataReceived;
    Chart mChart;
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        startMqtt();

        dataReceived = (TextView) findViewById(R.id.dataReceived);
        chart = (LineChart) findViewById(R.id.chart);
        mChart = new Chart(chart);
    }

    private void startMqtt() {
        //Cria instância da classe
        mqttHelper = new MqttClient(getApplicationContext(), "device");


        //Configura métodos de callback
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("Conexão mqtt: ", s);
                mqttHelper.subscribeToTopic("iot-2/type/+/id/+/evt/+/fmt/json", 0);
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.w("Conexão perdida",throwable);
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                //Log.w("Debug", mqttMessage.toString());

                JSONObject jObject = new JSONObject(mqttMessage.toString());
                JSONObject jObject2 = new JSONObject(String.valueOf(jObject.get("d")));
                System.out.println(jObject2.get("Illumination"));
                //dataReceived.setText(mqttMessage.toString());
                dataReceived.setText("Valor da iluminação do Ambiente: "+String.valueOf(jObject2.get("Illumination")));
                //mChart.addEntry(Float.valueOf(mqttMessage.toString()));
                mChart.addEntry(Float.valueOf(String.valueOf(jObject2.get("Illumination"))));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                String message;
                try {
                    message = iMqttDeliveryToken.getMessage().toString();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }
 }

