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
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class MqttClient {
    public MqttAndroidClient mqttAndroidClient;
    public MqttAndroidClient watsonClient;

    private static final String TAG = "Conexão mqtt";
    private static final String IOT_ORGANIZATION_TCP = ".messaging.internetofthings.ibmcloud.com:1883";
    private static final String IOT_ORGANIZATION_SSL = ".messaging.internetofthings.ibmcloud.com:8883";
    private static final String IOT_DEVICE_USERNAME  = "use-token-auth";

    //private static IoTClient instance;
    //private final Context context;
    private Context context;

    private String organization;
    private String deviceType;
    private String deviceID;
    private String authorizationToken = "lenovok6";

    //Dados do sensor
    final String subscriptionTopic = "sensor/+";

    //Dados de usuário e senha
    String username;
    char[] password;
    //String clientID
    String clientID;
    String ApiKey = "a-tobtpr-s5ygbb9mb4";
    String connectionURI = "tcp://" + "tobtpr" + IOT_ORGANIZATION_TCP;


    public MqttClient(Context context, String connectionType){

        //Contexto da aplicação
        this.context = context;

        //Define o tipo de conexão
        connectionType(connectionType);

        //Informa Contexto da aplicação, os dados de identificação do cliente e url de conexão
        mqttAndroidClient = new MqttAndroidClient(context, this.connectionURI, this.clientID);

        //Método de Conexão
        connect();
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect(){

        //Cria instância para configurar as propriedades da conexão
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();

        //Configura as propriedades no método de conexão
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password);

        try {
            //Conexão ao servidor
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

    private void connectionType(String value){
        //Device
        if(value.equals("device")){
            this.clientID = "d:" + this.organization + ":" + this.deviceType + ":" + this.deviceID;
            this.organization = "tobtpr";
            this.deviceType = "mobile";
            this.deviceID = "gps";
            this.username = IOT_DEVICE_USERNAME;
            this.password = ("s(Gc5_tgw@QfXFJNHd").toCharArray();
        }
        //Application
        else if(value.equals("app")){
            this.clientID = "a:tobtpr:s5ygbb9mb4";
            this.organization = "tobtpr";
            this.deviceType = "";
            this.deviceID = "";
            this.username = this.ApiKey;
            this.password = ("NZCheTg7O2D_jZR@uj").toCharArray();
        }
    }


    public void subscribeToTopic(String topic, int qos) {
        if(isMqttConnected()){
            try {
                mqttAndroidClient.subscribe(topic, qos, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.w("Mqtt","Subscrito!");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.w("Mqtt", exception);
                    }
                });

            } catch (MqttException ex) {
                System.err.println("Houve um problema ao subscrever");
                ex.printStackTrace();
            }
        }
    }

    private boolean isMqttConnected() {
        Log.d(TAG, ".isMqttConnected() entered");
        boolean connected = false;
        try {
            if ((mqttAndroidClient != null) && (mqttAndroidClient.isConnected())) {
                connected = true;
            }
        } catch (Exception e) {
            // swallowing the exception as it means the client is not connected
        }
        Log.d(TAG, ".isMqttConnected() - returning " + connected);
        return connected;
    }

    /**
     * Subscribe to an MQTT topic
     *
     * @param topic         The MQTT topic string to subscribe to
     * @param qos           The Quality of Service to use for the subscription
     * @param userContext   The context to associate with the subscribe call
     * @param listener      The IoTActionListener object to register with the Mqtt Token
     *
     * @@return IMqttToken The token returned by the Mqtt Subscribe call
     *
     * @throws MqttException
     */
    private IMqttToken subscribe(String topic, int qos, Object userContext, IMqttActionListener listener) throws MqttException {
        Log.d(TAG, ".subscribe() entered");
        if (isMqttConnected()) {
            try {
                return mqttAndroidClient.subscribe(topic, qos, userContext, listener);
            } catch (MqttException e) {
                Log.e(TAG, "Exception caught while attempting to subscribe to topic " + topic, e.getCause());
                throw e;
            }
        }
        return null;
    }

    /**
     * Unsubscribe from an MQTT topic
     *
     * @param topic         The MQTT topic string to unsubscribe from
     * @param userContext   The context to associate with the unsubscribe call
     * @param listener      The IoTActionListener object to register with the Mqtt Token
     *
     * @@return IMqttToken The token returned by the Mqtt Unsubscribe call
     *
     * @throws MqttException
     */
    private IMqttToken unsubscribe(String topic, Object userContext, IMqttActionListener listener) throws MqttException {
        Log.d(TAG, ".unsubscribe() entered");
        if (isMqttConnected()) {
            try {
                return mqttAndroidClient.unsubscribe(topic, userContext, listener);
            } catch (MqttException e) {
                Log.e(TAG, "Exception caught while attempting to subscribe to topic " + topic, e.getCause());
                throw e;
            }
        }
        return null;
    }

    //IMqttActionListener listener
    public IMqttDeliveryToken publishCommand(String command, String format, String payload, int qos, boolean retained) throws MqttException {
        Log.d(TAG, ".publishCommand() entered");
        String commandTopic = getCommandTopic(command, format);
        return publish(commandTopic, payload, qos, retained);
    }

    //IMqttActionListener listener
    private IMqttDeliveryToken publish(String topic, String payload, int qos, boolean retained) throws MqttException {
        Log.d(TAG, ".publish() entered");

        // check if client is connected
        if (isMqttConnected()) {
            // create a new MqttMessage from the message string
            MqttMessage mqttMsg = new MqttMessage(payload.getBytes());
            // set retained flag
            mqttMsg.setRetained(retained);
            // set quality of service
            mqttMsg.setQos(qos);
            try {
                // create ActionListener to handle message published results
                Log.d(TAG, ".publish() - Publishing " + payload + " to: " + topic + ", with QoS: " + qos + " with retained flag set to " + retained);
                return mqttAndroidClient.publish(topic, mqttMsg);
            } catch (MqttPersistenceException e) {
                Log.e(TAG, "MqttPersistenceException caught while attempting to publish a message", e.getCause());
                throw e;
            } catch (MqttException e) {
                Log.e(TAG, "MqttException caught while attempting to publish a message", e.getCause());
                throw e;
            }
        }
        return null;
    }
    /**
     * @param event     The event to create a topic string for
     * @param format    The format of the data sent to this topic
     *
     * @return The event topic for the specified event string
     */
    public static String getEventTopic(String event, String format) {
        return "iot-2/evt/" + event + "/fmt/json";
    }

    /**
     * @param command   The command to create a topic string for
     * @param format    The format of the data sent to this topic
     *
     * @return The command topic for the specified command string
     */
    public static String getCommandTopic(String command, String format) {
        return "iot-2/cmd/" + command + "/fmt/json";
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }
}
