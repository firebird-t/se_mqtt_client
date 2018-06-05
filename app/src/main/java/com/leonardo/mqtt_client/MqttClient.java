package com.leonardo.mqtt_client;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttClient {
    public MqttAndroidClient mqttAndroidClient;
    public MqttAndroidClient watsonClient;

    private static final String TAG = "";
    private static final String IOT_ORGANIZATION_TCP = ".messaging.internetofthings.ibmcloud.com:1883";
    private static final String IOT_ORGANIZATION_SSL = ".messaging.internetofthings.ibmcloud.com:8883";
    private static final String IOT_DEVICE_USERNAME  = "use-token-auth";

    //private static IoTClient instance;
    private MqttAndroidClient client;
    //private final Context context;
    //private final Context context;

    private String organization = "tobtpr" ;
    private String deviceType = "mobile";
    private String deviceID = "lenovok6";
    private String authorizationToken = "TmXaKBEsfc6Cvw!IbE";


    //Dados do sensor
    final String subscriptionTopic = "sensor/+";

    //Dados de usuário e senha
    //final String username = "oubvlxlo";
    //final String password = "1qrkhzaMzUoN";
    String username;
    char[] password;
    String clientID = "d:" + "tobtpr" + ":" + "mobile" + ":" + "lenovok6";
    String connectionURI = "tcp://" + "tobtpr" + IOT_ORGANIZATION_TCP;


    public MqttClient(Context context){
        mqttAndroidClient = new MqttAndroidClient(context, this.connectionURI, this.clientID);

        username = IOT_DEVICE_USERNAME;
        password = this.getAuthorizationToken().toCharArray();

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setUserName(username);
        options.setPassword(password);

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("mqtt", s);
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Mqtt", mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        connect();
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect(){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password);

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    //Configurações para armazenamento de mensagens offline
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);

                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    //subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Falha ao se conectar: " + connectionURI + exception.toString());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }




    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Mqtt","Subscrito!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "falha ao subscrever!");
                }
            });

        } catch (MqttException ex) {
            System.err.println("Houve um problema ao subscrever");
            ex.printStackTrace();
        }
    }

    public void publishMessage(){

//        try {
//            MqttMessage message = new MqttMessage();
//            message.setPayload(publishMessage.getBytes());
//            mqttAndroidClient.publish(publishTopic, message);
//            addToHistory("Message Published");
//            if(!mqttAndroidClient.isConnected()){
//                addToHistory(mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
//            }
//        } catch (MqttException e) {
//            System.err.println("Error Publishing: " + e.getMessage());
//            e.printStackTrace();
//        }
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }
}
