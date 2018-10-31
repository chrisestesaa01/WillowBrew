#include <Arduino.h>
static bool ledState = true;





#include <HX711.h>

bool resetZeroFlag(int zero);
void checkZero();
bool resetCalibrateFlag(float calibration);
void checkCalibrate();
void initScale();


// HX711.DOUT	- pin #A1
// HX711.PD_SCK	- pin #A0

		// parameter "gain" is ommited; the default value 128 is used by the library

// void setup() {


//   Serial.println("HX711 Demo");

//   Serial.println("Before setting up the scale:");
//   Serial.print("read: \t\t");
//   Serial.println(scale.read());			// print a raw reading from the ADC

//   Serial.print("read average: \t\t");
//   Serial.println(scale.read_average(20));  	// print the average of 20 readings from the ADC

//   Serial.print("get value: \t\t");
//   Serial.println(scale.get_value(5));		// print the average of 5 readings from the ADC minus the tare weight (not set yet)

//   Serial.print("get units: \t\t");
//   Serial.println(scale.get_units(5), 1);	// print the average of 5 readings from the ADC minus tare weight (not set) divided 
// 						// by the SCALE parameter (not set yet)  

//   scale.set_scale(2280.f);                      // this value is obtained by calibrating the scale with known weights; see the README for details
//   scale.tare();				        // reset the scale to 0

//   Serial.println("After setting up the scale:");

//   Serial.print("read: \t\t");
//   Serial.println(scale.read());                 // print a raw reading from the ADC

//   Serial.print("read average: \t\t");
//   Serial.println(scale.read_average(20));       // print the average of 20 readings from the ADC

//   Serial.print("get value: \t\t");
//   Serial.println(scale.get_value(5));		// print the average of 5 readings from the ADC minus the tare weight, set with tare()

//   Serial.print("get units: \t\t");
//   Serial.println(scale.get_units(5), 1);        // print the average of 5 readings from the ADC minus tare weight, divided 
// 						// by the SCALE parameter set with set_scale
// ESP.wdtEnable(5000);
//   Serial.println("Readings:");
// }


//
// Copyright 2015 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

// FirebaseDemo_ESP8266 is a sample that demo the different functions
// of the FirebaseArduino API.

#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <WiFiManager.h>
#include "EEPROM.h"
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include "FirebaseCreds.h"

#define SETUP_AP_NAME "KegScaleSetup"

HX711 scale(0, 4);

/**
 * Initiate autoconnect.
 **/
void initAutoConnect() {
    WiFiManager wifiManager;
    wifiManager.setConnectTimeout(20);
    wifiManager.autoConnect(SETUP_AP_NAME);
    Serial.println("Autoconnect success.");
}

/**
 * Connect wifi.
 **/
void connectWifi() {
    Serial.print("connecting");
    while (WiFi.status() != WL_CONNECTED) {
        Serial.printf("%d", WiFi.status());
        delay(500);
    }
    Serial.println();
    Serial.print("connected: ");
    Serial.println(WiFi.localIP());
}

void setup() {

    // Start debug serial.
    Serial.begin(115200);

    // Start autoconnect.
    initAutoConnect();

    // Connect to wifi.
    connectWifi();
  
    // Init firebase.
    Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

    // Init scales.
    initScale();
}



const String ZERO_FLAG = "zeroFlag";
const String CALIBRATE_FLAG = "calibrateFlag";
const String ZERO_POINT = "zeroPoint";
const String CALIBRATION_POINT = "calibrationPoint";
const String CALIBRATION_VALUE = "calibrationValue";

int n = 0;

void loop() {
  // set value
//   Firebase.setFloat("number", 42.0);
//   // handle error
//   if (Firebase.failed()) {
//       Serial.print("setting /number failed:");
//       Serial.println(Firebase.error());  
//       return;
//   }
//   delay(1000);
  
//   // update value
//   Firebase.setFloat("number", 43.0);
//   // handle error
//   if (Firebase.failed()) {
//       Serial.print("setting /number failed:");
//       Serial.println(Firebase.error());  
//       return;
//   }
//   delay(1000);

//   // get value 
//   Serial.print("number: ");
//   Serial.println(Firebase.getFloat("number"));
//   delay(1000);

//   // remove value
//   Firebase.remove("number");
//   delay(1000);

//   // set string value
//   Firebase.setString("message", "hello world");
//   // handle error
//   if (Firebase.failed()) {
//       Serial.print("setting /message failed:");
//       Serial.println(Firebase.error());  
//       return;
//   }
//   delay(1000);
  
//   // set bool value
//   Firebase.setBool("truth", false);
//   // handle error
//   if (Firebase.failed()) {
//       Serial.print("setting /truth failed:");
//       Serial.println(Firebase.error());  
//       return;
//   }
//   delay(1000);

//   // append a new value to /logs
//   String name = Firebase.pushInt("logs", n++);
//   // handle error
//   if (Firebase.failed()) {
//       Serial.print("pushing /logs failed:");
//       Serial.println(Firebase.error());  
//       return;
//   }
//   Serial.print("pushed: /logs/");
//   Serial.println(name);
//   delay(1000);

   checkZero();
   checkCalibrate();


    Serial.print("one reading:\t");
  Serial.print(scale.read(), 1);
  Serial.print("\t| average:\t");
  Serial.println(scale.get_units(10), 1);

  scale.power_down();			        // put the ADC in sleep mode
  delay(500);
  scale.power_up();


    digitalWrite(LED_BUILTIN, ledState);
    ledState = !ledState;
    
}


#pragma region Calibrate methods

void initScale() {
    int zeroPoint = Firebase.getInt(ZERO_POINT);
    float calPoint = Firebase.getFloat(CALIBRATION_POINT);
    printf("Initializing scales. Zero: %d, Cal: %f\r\n", zeroPoint, calPoint);
    scale.set_offset(zeroPoint);
    scale.set_scale(calPoint);
}

/**
 * Check for signal to zero scales and zero them if signal received.
 **/
void checkZero() {
    
    // Static flag to check reset success.
    static bool success = true;

    // If previous reset failed, then try again.
    if (!success) {
        success = resetZeroFlag(0);
    }

    // If last reset success, and get is true, then we have another request to zero.
    if(success && Firebase.getBool(ZERO_FLAG)) {

        // Zero the scales and reset the zero flag.
        Serial.print("Zeroing scales... ");
        scale.set_scale();
        scale.tare();
        int zero = scale.read_average();
        Serial.printf("complete. Zero points: %d, %d, %d\r\n", zero, 0, 0);
        success = resetZeroFlag(zero);
    }
}

/**
 * Reset the flag to zero the scales.
 **/
bool resetZeroFlag(int zero) {

    // Clear the zero flag and get the result of the clear operation.
    bool success = false;
    Firebase.setInt(ZERO_POINT, zero);
    Firebase.setBool(ZERO_FLAG, false);
    success = Firebase.success();

    // If clearing fails, then log the error.
    if (!success) {
        Serial.println("Zero flag reset failed");
        Serial.println(Firebase.error());
    }

    // Return the result of the clear operation.
    return success;
}

/**
 * Check for signal to calibrate scales and calibrate them if signal received.
 **/
void checkCalibrate() {
    
    // Static flag to check reset success.
    static bool success = true;

    // If previous reset failed, then try again.
    if (!success) {
        success = resetCalibrateFlag(0);
    }

    if (!success) { return; }

    // If last reset success, and get is true, then we have another request to calibrate.
    if(Firebase.getBool(CALIBRATE_FLAG)) {

        // Calibrate the scales and reset the calibrate flag.
        Serial.print("Calibrating scales... ");
        float calValue = Firebase.getFloat(CALIBRATION_VALUE);
        if (calValue <= 0) { calValue = 1; }
        int raw = scale.get_units(10);
        float result = raw / calValue;
        scale.set_scale(result);
        Serial.printf("Success! Target: %fg, Raw: %d, Result: %f\r\n", calValue, raw, result);
        success = resetCalibrateFlag(result);
    }
}

/**
 * Reset the flag to calibrate the scales.
 **/
bool resetCalibrateFlag(float calibration) {

    // Clear the calibrate flag and get the result of the clear operation.
    bool success = false;
    Firebase.setFloat(CALIBRATION_POINT, calibration);
    Firebase.setBool(CALIBRATE_FLAG, false);
    success = Firebase.success();

    // If clearing fails, then log the error.
    if (!success) {
        Serial.println("Calibration flag reset failed");
        Serial.println(Firebase.error());
    }

    // Return the result of the clear operation.
    return success;
}

#pragma endregion
