#include <OneWire.h>
#include <DallasTemperature.h>
#include <WiFi.h>
#include <PubSubClient.h>

#define ONE_WIRE_BUS 4
#define MP503_PIN 34
#define MQ9_PIN 35

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

const char* ssid = "wifiq na moni";
const char* password = "parolata na moni";
const char* mqtt_server = "broker.hivemq.com"; 
const int mqtt_port = 1883;                   
const char* mqtt_user = "";                 
const char* mqtt_password = "";     

WiFiClient espClient;
PubSubClient client(espClient);

float R0 = 10000.0;
float RL = 10000.0;

void setup() {
  Serial.begin(115200);
  sensors.begin();
  analogReadResolution(12);
  
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("Connected to WiFi");

  client.setServer(mqtt_server, mqtt_port);
  while (!client.connected()) {
    if (client.connect("ESP32Client", mqtt_user, mqtt_password)) {
      Serial.println("Connected to MQTT Broker");
    } else {
      delay(5000);
    }
  }
}

void loop() {
  sensors.requestTemperatures();
  float temperatureC = sensors.getTempCByIndex(0);
  int airQuality = analogRead(MP503_PIN);
  int mq9Value = analogRead(MQ9_PIN);
  float voltage = mq9Value * (3.3 / 4095.0);
  float rs = ((3.3 * RL) / voltage) - RL;
  float ratio = rs / R0;

  client.publish("home/temperature", String(temperatureC).c_str());
  client.publish("home/air_quality", String(airQuality).c_str());
  client.publish("home/gas_ratio", String(ratio).c_str());

  interpretAirQuality(airQuality);
  interpretMQ9(ratio);

  delay(2000);
}

void interpretAirQuality(int value) {
  Serial.print("Качество на въздуха: ");
  if (value < 1000) {
    Serial.println("Отлично");
  } else if (value < 2000) {
    Serial.println("Добро");
  } else if (value < 3000) {
    Serial.println("Средно");
  } else {
    Serial.println("Лошо");
  }
}

void interpretMQ9(float ratio) {
  Serial.print("Наличие на газове: ");
  if (ratio < 1) {
    Serial.println("Високо ниво на газове!");
  } else if (ratio < 3) {
    Serial.println("Средно ниво на газове");
  } else {
    Serial.println("Ниско ниво на газове");
  }
}