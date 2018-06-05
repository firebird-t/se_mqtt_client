// Please Open your Arduino IDE and paste the following code :

#include <ESP8266WiFi.h>
#include <PubSubClient.h> // https://github.com/knolleary/pubsubclient/releases/tag/v2.3
#include <ArduinoJson.h> // https://github.com/bblanchon/ArduinoJson/releases/tag/v5.0.7
#define led D1
#define led2 D2
#define led3 D3
int v1;

//-------- Dados do Wifi -----------
const char* ssid = "Lab 1";
const char* password = "lab2info";

//Dados do node mcu
#define ORG "tobtpr"
#define DEVICE_TYPE "nodemcu"
#define DEVICE_ID "nodemcu1"
#define TOKEN "NkKzZKJGCzlMs&YvFf"

//-------- Valores do IBM CLOUD --------
char server[] = ORG ".messaging.internetofthings.ibmcloud.com";
char authMethod[] = "use-token-auth";
char token[] = TOKEN;
char clientId[] = "d:" ORG ":" DEVICE_TYPE ":" DEVICE_ID;

//String value;
const char publishTopic[] = "iot-2/evt/status/fmt/json";
const char responseTopic[] = "iotdm-1/response";
const char manageTopic[] = "iotdevice-1/mgmt/manage";
const char updateTopic[] = "iotdm-1/device/update";
const char rebootTopic[] = "iotdm-1/mgmt/initiate/device/reboot";
const char subTopic[] = "iot-2/cmd/light/fmt/json";

//Funções e Bibliotecas
void wifiConnect();
void mqttConnect();
void initManagedDevice();
void publishData();
void handleUpdate(byte* payload) ;
void callback(char* topic, byte* payload, unsigned int payloadLength);

WiFiClient wifiClient;
PubSubClient client(server, 1883, wifiClient);
int publishInterval = 5000; // 30 seconds
long lastPublishMillis;
int sensorpin = A0;
int sensorvalue;

void setup() {
  Serial.begin(115200);
  Serial.println();
  wifiConnect();
  mqttConnect();
  initManagedDevice();
}

void loop() {
  sensorvalue = analogRead(sensorpin);
  if (millis() - lastPublishMillis > publishInterval) {
    publishData();
    lastPublishMillis = millis();
  }


  if (!client.loop()) {
    mqttConnect();
    initManagedDevice();
  }
  delay(100);
}

void wifiConnect() {
  Serial.print("Connecting to "); Serial.print(ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.print("nWiFi connected, IP address: "); Serial.println(WiFi.localIP());
}

void mqttConnect() {
  if (!!!client.connected()) {
    Serial.print("Reconnecting MQTT client to "); Serial.println(server);
    while (!!!client.connect(clientId, authMethod, token)) {
      Serial.print(".");
      delay(500);
    }
    Serial.println();
  }
}

void initManagedDevice() {
  if (client.subscribe("iotdm-1/response")) {
    Serial.println("subscribe to responses OK");
  } else {
    Serial.println("subscribe to responses FAILED");
  }

  if (client.subscribe(rebootTopic)) {
    Serial.println("subscribe to reboot OK");
  } else {
    Serial.println("subscribe to reboot FAILED");
  }

  if (client.subscribe("iotdm-1/device/update")) {
    Serial.println("subscribe to update OK");
  } else {
    Serial.println("subscribe to update FAILED");
  }
  if (client.subscribe(subTopic)) {
    Serial.println("subscribe to subtopic OK");
  } else {
    Serial.println("subscribe to update FAILED");
  }

  StaticJsonBuffer<300> jsonBuffer;
  JsonObject& root = jsonBuffer.createObject();
  JsonObject& d = root.createNestedObject("d");
  JsonObject& metadata = d.createNestedObject("metadata");
  metadata["publishInterval"] = publishInterval;
  JsonObject& supports = d.createNestedObject("supports");
  supports["deviceActions"] = true;

  char buff[300];
  root.printTo(buff, sizeof(buff));
  Serial.println("publishing device metadata:"); Serial.println(buff);
  if (client.publish(manageTopic, buff)) {
    Serial.println("device Publish ok");
  } else {
    Serial.print("device Publish failed:");
  }
}

void publishData() {

  String payload = "{\"d\":{\"Illumination\":";
  payload += sensorvalue;
  payload += "}}";

  Serial.print("Sending payload: "); Serial.println(payload);

  if (client.publish(publishTopic, (char*) payload.c_str())) {
    Serial.println("Publish OK");
  } else {
    Serial.println("Publish FAILED");
  }
}

void callback(char* topic, byte* payload, unsigned int payloadLength) {
  Serial.print("callback invoked for topic: "); Serial.println(topic);

  if (strcmp (responseTopic, topic) == 0) {
    return; // just print of response for now
  }

  if (strcmp (rebootTopic, topic) == 0) {
    Serial.println("Rebooting...");
    ESP.restart();
  }

  if (strcmp (updateTopic, topic) == 0) {
    handleUpdate(payload);
  }
  if (strcmp (subTopic, topic) == 0) {
    Serial.print("Subscribed");
    Serial.println((char*)payload);
    handleUpdate(payload);
  }
}

void handleUpdate(byte* payload) {
  StaticJsonBuffer<300> jsonBuffer;
  JsonObject& root = jsonBuffer.parseObject((char*)payload);
  if (!root.success()) {
    Serial.println("handleUpdate: payload parse FAILED");
    return;
  }
  String value = root["command"];
  int v1 = root["value1"];
  int v2 = root["value2"];
  int v3 = root["value3"];
  int va = map(v1, 0, 100, 1023, 0);
  int vb = map(v2, 0, 100, 1023, 0);
  int vc = map(v3, 0, 100, 1023, 0);

  Serial.print(v1); Serial.print(v2); Serial.println(v3);
  Serial.print(va); Serial.print(vb); Serial.print(vc);

  Serial.print("data:");
  Serial.println(value);
  if (value == "LIGHT1ON")
  {
    analogWrite(D1, va);

  }

  if (value == "LIGHT1OFF")
  {
    analogWrite(D1, 1023);
  }
  if (value == "LIGHT2ON")
  {
    analogWrite(D2, vb);
  }
  if (value == "LIGHT2OFF")
  {
    analogWrite(D2, 1023);
  }
  if (value == "LIGHT3ON")
  {
    analogWrite(D3, vc);
  }
  if (value == "LIGHT3OFF")
  {
    analogWrite(D3, 1023);
  }
  if (value == "LIGHTON")
  {
    analogWrite(D1, 0);
    analogWrite(D2, 0);
    analogWrite(D3, 0);
  }
  if (value == "LIGHTOFF")
  {
    analogWrite(D1, 1023);
    analogWrite(D2, 1023);
    analogWrite(D3, 1023);
  }
  value = "";
  Serial.println("handleUpdate payload:"); root.prettyPrintTo(Serial); Serial.println();
  JsonObject& d = root["d"];
  JsonArray& fields = d["fields"];
 
  for (JsonArray::iterator it = fields.begin(); it != fields.end(); ++it) {
    JsonObject& field = *it;
    const char* fieldName = field["field"];
    
    if (strcmp (fieldName, "metadata") == 0) {
      JsonObject& fieldValue = field["value"];
     
      if (fieldValue.containsKey("publishInterval")) {
        publishInterval = fieldValue["publishInterval"];
        Serial.print("publishInterval:"); Serial.println(publishInterval);
      }
    }
  }
}
