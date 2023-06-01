    #include <Arduino.h>
    #include <WiFi.h>
    #include <Firebase_ESP_Client.h>
    #include <Wire.h>
    #include <SPI.h>
    #include <Adafruit_Sensor.h>
    #include <Adafruit_BME280.h>
     
    //Provide the token generation process info.
    #include "addons/TokenHelper.h"
    //Provide the RTDB payload printing info and other helper functions.
    #include "addons/RTDBHelper.h"
     
    // Insert your network credentials
    #define WIFI_SSID "redmi"
    #define WIFI_PASSWORD "12345678"

    // Define the sea level pressure in order to calculate altitude properly
    #define SEALEVELPRESSURE_HPA (1013.25)

    // 1 for using I2C, 0 for using SPI for receiving data
    #define USEIIC 1

    #if(USEIIC)
      Adafruit_BME280 bme;
    #else
      #define SPI_SCK 13
      #define SPI_MISO 12
      #define SPI_MOSI 11
      #define SPI_CS 10
      Adafruit_BME280 bme(SPI_CS, SPI_MOSI, SPI_MISO, SPI_SCK);
    #endif
     
    // Insert Firebase project API Key
    #define API_KEY "AIzaSyC5NGsrzLd34QnsjADno7t3qVTw4uYw9ek"
     
    // Insert RTDB URLefine the RTDB URL */
    #define DATABASE_URL "https://weatherly-local-station-default-rtdb.europe-west1.firebasedatabase.app/" 
     
    //Define Firebase Data object
    FirebaseData fbdo;
    FirebaseAuth auth;
    FirebaseConfig config;
     
    unsigned long sendDataPrevMillis = 0;
    float temp_val; 
    int humidity_val, pressure_val;
    bool signupOK = false;
    unsigned long delayTime;
     
  void setup(){
    Serial.begin(115200);

    // Init the BME280 sensor
    bool rslt;
    rslt = bme.begin();  
    if (!rslt) {
      Serial.println("Could not initialize sensor, make sure it is properly connected!");
      while(1);
    }
    Serial.println("BME280 successfully initialized");
    Serial.println("Temperature           Pressure             Humidity      UV value (0-10)");
    delayTime = 40;

    // Start connecting to WiFi network
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("Connecting to Wi-Fi");
    while (WiFi.status() != WL_CONNECTED){
      Serial.print(".");
      delay(300);
    }
    Serial.println();
    Serial.print("Connected with IP: ");
    Serial.println(WiFi.localIP());
    Serial.println();
    
    /* Assign the api key (required) */
    config.api_key = API_KEY;
    
    /* Assign the RTDB URL (required) */
    config.database_url = DATABASE_URL;
    
    /* Sign up */
    if (Firebase.signUp(&config, &auth, "", "")){
      Serial.println("ok");
      signupOK = true;
    }
    else{
      Serial.printf("%s\n", config.signer.signupError.message.c_str());
    }
    
    /* Assign the callback function for the long running token generation task */
    config.token_status_callback = tokenStatusCallback; //see addons/TokenHelper.h
    
    Firebase.begin(&config, &auth);
    Firebase.reconnectWiFi(true);
  }
     
  void loop(){
    
      // You can test with random values if sensors are unavailable
      /*temp_val = 0.3 + random(0,100);
      humidity_val = random(20,100);
      pressure_val = random(998,1050);*/

      // Print values in terminal for easy debugging
      printValues();
      sendValues();

      // Values are updated with set time delay
      //delay(delayTime);
  }

  // Function to print values, for easy debugging
  void printValues() {
    Serial.print("temperature:");
    Serial.print(bme.readTemperature());
    Serial.print("*C   ");

    Serial.print("pressure:");
    Serial.print(bme.readPressure()/100.0F);
    Serial.print("hPa   ");

    Serial.print("humidity:");
    Serial.print(bme.readHumidity());
    Serial.print("%   ");
  
    Serial.print("altitude:");
    Serial.print(bme.readAltitude(SEALEVELPRESSURE_HPA));
    Serial.println("m");
  }      

  void sendValues() {
    // Write temperature value on the database path temperature (Celsius)
    if (Firebase.RTDB.setFloat(&fbdo, "temperature", bme.readTemperature())){
      Serial.println("PASSED");
      Serial.println("PATH: " + fbdo.dataPath());
      Serial.println("TYPE: " + fbdo.dataType());
    }
    else {
      Serial.println("FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }

    // Write humidity value on the database path humidity (%)
    if (Firebase.RTDB.setFloat(&fbdo, "humidity", bme.readHumidity())){
      Serial.println("PASSED");
      Serial.println("PATH: " + fbdo.dataPath());
      Serial.println("TYPE: " + fbdo.dataType());
    }
    else {
      Serial.println("FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }

    // Write atm pressure value on the database path pressure (%)
    if (Firebase.RTDB.setFloat(&fbdo, "pressure", bme.readPressure()/100.0F)){
      Serial.println("PASSED");
      Serial.println("PATH: " + fbdo.dataPath());
      Serial.println("TYPE: " + fbdo.dataType());
    }
    else {
      Serial.println("FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }

    // Write altitude value on the database path altitude (meters)
    if (Firebase.RTDB.setFloat(&fbdo, "altitude", bme.readAltitude(SEALEVELPRESSURE_HPA))){
      Serial.println("PASSED");
      Serial.println("PATH: " + fbdo.dataPath());
      Serial.println("TYPE: " + fbdo.dataType());
    }
    else {
      Serial.println("FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }
  }      
    


