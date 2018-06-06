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

    private static final String TAG = "";
    private static final String IOT_ORGANIZATION_TCP = ".messaging.internetofthings.ibmcloud.com:1883";
    private static final String IOT_ORGANIZATION_SSL = ".messaging.internetofthings.ibmcloud.com:8883";
    private static final String IOT_DEVICE_USERNAME  = "use-token-auth";

    //private static IoTClient instance;
    //private final Context context;
    private Context context;

    private String organization = "tobtpr" ;
    private String deviceType = "mobile";
    private String deviceID = "lenovok6";
    private String authorizationToken = "TmXaKBEsfc6Cvw!IbE";


    //Dados do sensor
    final String subscriptionTopic = "sensor/+";

    //Dados de usuário e senha
    String username;
    char[] password;
    String clientID = "d:" + "tobtpr" + ":" + "mobile" + ":" + "lenovok6";
    String connectionURI = "tcp://" + "tobtpr" + IOT_ORGANIZATION_TCP;


    public MqttClient(Context context){

        //Contexto da aplicação
        this.context = context;

        //Informa Contexto da aplicação, os dados de identificação do cliente e url de conexão
        mqttAndroidClient = new MqttAndroidClient(context, this.connectionURI, this.clientID);

        //Método de Autenticação
        username = IOT_DEVICE_USERNAME;

        //A senha é o token fornecido pela plataforma
        password = this.getAuthorizationToken().toCharArray();

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

    public IMqttDeliveryToken publishCommand(String command, String format, String payload, int qos, boolean retained, IMqttActionListener listener) throws MqttException {
        Log.d(TAG, ".publishCommand() entered");
        String commandTopic = getCommandTopic(command, format);
        return publish(commandTopic, payload, qos, retained, listener);
    }

    private IMqttDeliveryToken publish(String topic, String payload, int qos, boolean retained, IMqttActionListener listener) throws MqttException {
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
