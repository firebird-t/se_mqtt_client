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

        //Informa os dados de identificação do cliente e url de conexão
        mqttAndroidClient = new MqttAndroidClient(context, this.connectionURI, this.clientID);

        //Método de Autenticação
        username = IOT_DEVICE_USERNAME;

        //A senha é o token fornecido pela plataforma
        password = this.getAuthorizationToken().toCharArray();

        //Configura as opções nos métodos de conexão
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setUserName(username);
        options.setPassword(password);

        //Configuração de Callbacks
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("Conexão mqtt: ", s);
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.w("Conexão perdida",throwable);
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Mensagem Mqtt: ", mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });

        //Método de Conexão
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
