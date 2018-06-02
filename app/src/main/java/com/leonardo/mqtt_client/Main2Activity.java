package com.leonardo.mqtt_client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

//import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.*;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class Main2Activity extends AppCompatActivity {

    MqttClient mqttHelper;
    TextView dataReceived;
    Chart mChart;
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        dataReceived = (TextView) findViewById(R.id.dataReceived);
        chart = (LineChart) findViewById(R.id.chart);
        mChart = new Chart(chart);

        startMqtt();
    }

    private void startMqtt() {
        //Cria instância da classe
        mqttHelper = new MqttClient(getApplicationContext());


        //Configura métodos de callback
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug", mqttMessage.toString());
                dataReceived.setText(mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
 }

