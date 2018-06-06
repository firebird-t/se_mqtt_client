package com.leonardo.mqtt_client;

import android.content.Context;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import static android.content.ContentValues.TAG;

public class mqttListener implements IMqttActionListener{
    private String action;


    private final static String TAG = "Mobile App";

    public void mqttListener(Context context, String action){
        this.action = action;
    }


    @Override
    public void onSuccess(IMqttToken token) {
        Log.d(TAG, ".onSuccess() entered");

        if(action.equals("CONNECTING")){
            handleConnectSuccess();
        }
        else if(action.equals("SUBSCRIBE")){
            handleSubscribeSuccess();
        }
        else if(action.equals("PUBLISH")){
            handlePublishSuccess();
        }
        else if(action.equals("DISCONNECTING")){
            handleDisconnectSuccess();
        }
        else {
            Log.d("Choose","null");
        }
    }

    /**
     * Determine the type of callback that failed.
     * @param token The MQTT Token for the completed action.
     * @param throwable The exception corresponding to the failure.
     */
    @Override
    public void onFailure(IMqttToken token, Throwable throwable) {
        Log.e(TAG, ".onFailure() entered");
        if(action.equals("CONNECTING")){
            handleConnectFailure(throwable);
        }
        else if(action.equals("SUBSCRIBE")){
            handleSubscribeFailure(throwable);
        }
        else if(action.equals("PUBLISH")){
            handlePublishFailure(throwable);
        }
        else if(action.equals("DISCONNECTING")){
            handleDisconnectFailure(throwable);
        }
        else {
            Log.d("Choose","null");
        }

    }

    private void handleDisconnectFailure(Throwable throwable) {
    }

    private void handlePublishFailure(Throwable throwable) {
    }

    private void handleSubscribeFailure(Throwable throwable) {
    }

    private void handleConnectFailure(Throwable throwable) {
    }

    private void handleDisconnectSuccess() {
    }

    private void handlePublishSuccess() {
    }

    private void handleSubscribeSuccess() {
    }

    private void handleConnectSuccess() {
        Log.d(TAG, ".handleConnectSuccess() entered");

       
        //}
    }
}
