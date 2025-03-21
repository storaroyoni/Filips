#include <OneWire.h>
#include <DallasTemperature.h>
#include <WiFi.h>

#define ONE_WIRE_BUS 4
#define MP503_PIN 34
#define MQ9_PIN 35

float RL = 10000.0;
float R0 = 10000.0;

const char* ssid = "Moni net";     
const char* password = "na_M0N1-netA"; 

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

void setup() {
  Serial.begin(115200);

  sensors.begin();
  
  sensors.requestTemperatures();
  delay(750);  
  
  int deviceCount = sensors.getDeviceCount();
  Serial.print("DS18B20 sensors found: ");
  Serial.println(deviceCount);

  if (deviceCount == 0) {
    Serial.println("⚠️ No DS18B20 sensors detected! Check wiring.");
  }

  analogReadResolution(12);

  WiFi.begin(ssid, password);
  int wifiAttempts = 0;
  
  while (WiFi.status() != WL_CONNECTED && wifiAttempts < 20) {  
    delay(500);
    Serial.print(".");
    wifiAttempts++;
  }

  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\nConnected to WiFi");
  } else {
    Serial.println("\n⚠️ Failed to connect to WiFi.");
  }
}

void loop() {
  sensors.requestTemperatures();
  float temperatureC = sensors.getTempCByIndex(0);

  int retryCount = 0;
  while ((temperatureC == 85 || temperatureC == -127) && retryCount < 5) {
    Serial.println("⚠️ Invalid temperature, retrying...");
    delay(750);  
    sensors.requestTemperatures();
    temperatureC = sensors.getTempCByIndex(0);
    retryCount++;
  }

  if (temperatureC == -127) {
    Serial.println(" Error: DS18B20 sensor not detected! Check wiring.");
  } else if (temperatureC == 85) {
    Serial.println("⚠️ Sensor not initialized properly. Try resetting.");
  } else {
    Serial.print("Temperature: ");
    Serial.print(temperatureC);
    Serial.println(" °C");
  }

  int airQuality = analogRead(MP503_PIN);
  int mq9Value = analogRead(MQ9_PIN);
  float voltage = mq9Value * (3.3 / 4095.0);
  float rs = (RL * (3.3 - voltage)) / voltage;
  float ratio = rs / R0;

  Serial.print("Air Quality: ");
  Serial.println(airQuality);
  Serial.print("Gas Ratio: ");
  Serial.println(ratio);

  interpretAirQuality(airQuality);
  interpretMQ9(ratio);

  delay(2000);
}

void interpretAirQuality(int value) {
  Serial.print("Air Quality Status: ");
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