#include <OneWire.h>
#include <DallasTemperature.h>
#include <WiFi.h>
#include <HTTPClient.h>


#define ONE_WIRE_BUS 4
#define MP503_PIN 34
#define MQ9_PIN 35

float R0 = 1000.0;
float RL = 1000.0;

const char* ssid = "";     
const char* password = ""; 

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

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

  sendPostRequest();
}

void loop() {
  sensors.requestTemperatures();
  float temperatureC = sensors.getTempCByIndex(0);
  int airQuality = analogRead(MP503_PIN);
  int mq9Value = analogRead(MQ9_PIN);
  float voltage = mq9Value * (3.3 / 4095.0);
  float rs = ((3.3 * RL) / voltage) - RL;
  float ratio = rs / R0;

  Serial.print("Temperature: ");
  Serial.println(temperatureC);
  Serial.print("Air Quality: ");
  Serial.println(airQuality);
  Serial.print("Gas Ratio: ");
  Serial.println(ratio);

  interpretAirQuality(airQuality);
  interpretMQ9(ratio);

  delay(2000);
}

void interpretAirQuality(int value) {
  Serial.print("Air Quality: ");
  if (value < 1000) {
    Serial.println("Excellent");
  } else if (value < 2000) {
    Serial.println("Good");
  } else if (value < 3000) {
    Serial.println("Average");
  } else {
    Serial.println("Poor");
  }
}

void interpretMQ9(float ratio) {
  Serial.print("Gas Level: ");
  if (ratio < 1) {
    Serial.println("High gas level!");
  } else if (ratio < 3) {
    Serial.println("Medium gas level");
  } else {
    Serial.println("Low gas level");
  }
}

void sendPostRequest() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;

    String serverUrl = "http://192.168.95.90:8082/device/hello";

    http.begin(serverUrl);
    int httpResponseCode = http.POST("");

    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);

    if (httpResponseCode > 0) {
      String response = http.getString();
      Serial.println("Response:");
      Serial.println(response);
    } else {
      Serial.print("Error code: ");
      Serial.println(httpResponseCode);
    }

    http.end();
  } else {
    Serial.println("WiFi Disconnected");
  }
}

