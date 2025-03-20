#include <OneWire.h>
#include <DallasTemperature.h>
#include <WiFi.h>

#define ONE_WIRE_BUS 4
#define MP503_PIN 34
#define MQ9_PIN 35

const char* ssid = "moni wifi";     
const char* password = "moni parola"; 

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
