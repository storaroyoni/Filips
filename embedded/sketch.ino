#include <OneWire.h>
#include <DallasTemperature.h>

#define ONE_WIRE_BUS 4      
#define MP503_PIN 34       
#define MQ9_PIN 35         

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensors(&oneWire);

float R0 = 10000.0;    
float RL = 10000.0;  

void setup() {
  Serial.begin(115200);
  
  sensors.begin();
  
  analogReadResolution(12); 
  
  Serial.println("Инициализация на сензорите...");
  delay(2000); 
}

void loop() {
  sensors.requestTemperatures();
  float temperatureC = sensors.getTempCByIndex(0);
  
  int airQuality = analogRead(MP503_PIN);
  
  int mq9Value = analogRead(MQ9_PIN);
  float voltage = mq9Value * (3.3 / 4095.0); 
  float rs = ((3.3 * RL) / voltage) - RL;    
  float ratio = rs / R0;                     
  
  Serial.println("--------------------");
  Serial.print("Температура: ");
  Serial.print(temperatureC);
  Serial.println(" °C");
  
  Serial.print("Качество на въздуха (MP503): ");
  Serial.println(airQuality);
  
  Serial.print("MQ-9 сурова стойност: ");
  Serial.println(mq9Value);
  Serial.print("MQ-9 съотношение: ");
  Serial.println(ratio);
  
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