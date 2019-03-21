#include <Arduino.h>
#include <HX711.h>
#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <WiFiManager.h>
#include "EEPROM.h"
#include <DNSServer.h>
#include <ESP8266WebServer.h>

// For firebase project host and auth key.
#include "FirebaseCreds.h"

// Function sigs.
void initAutoConnect();
void connectWifi();
void initScales();
void readScale(int index);
void checkData(int index, float newValue);
String pathToScale(int scaleNum, String value);
void initScale(int index);
void checkZero(int index);
bool resetZeroFlag(int index, int zero);
void checkCalibrate(int index);
bool resetCalibrateFlag(int index, float calibration);

// AP to start up if wifi auto connect fails.
#define SETUP_AP_NAME "KegScaleSetup"

// Total number of scales.
#define NUM_SCALES 1

// Blinker for LED.
bool ledState = true;

// The scales.
HX711 scales[NUM_SCALES] = { HX711(0, 4) };

// Paths to firebase values.
const String ZERO_FLAG = "calibration/zeroFlag";
const String CALIBRATE_FLAG = "calibration/calibrateFlag";
const String ZERO_POINT = "calibration/zeroPoint";
const String CALIBRATION_POINT = "calibration/calibrationPoint";
const String CALIBRATION_VALUE = "calibration/calibrationValue";
const char* SCALE_PATH = "scales/%d";

// Max scale value for sanity checking.
const int MAX_SCALE_VAL = 100000;

// Minimum change to push to firebase.
const int MIN_CHANGE_VAL = 5;

// Last values read from scales to check change value.
float lastVals[NUM_SCALES] = { 0 };

// Counter to cycle through reading the scales.
int loopCounter = 0;

/**
 * Setup method.
 **/
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
    initScales();

    // Init LED pin.
    pinMode(LED_BUILTIN, OUTPUT);
}

/**
 * Main loop.
 **/
void loop() {

    // Check signals and read scale.
    checkZero(loopCounter);
    checkCalibrate(loopCounter);
    readScale(loopCounter);

    // Blink LED.
    digitalWrite(LED_BUILTIN, ledState);
    ledState = !ledState;

    // Increment counter.
    loopCounter++;
    loopCounter = loopCounter % NUM_SCALES;
    
    // Wait a bit.
    delay(500);
}

#pragma region Other methods:

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

/**
 * Initialize all scales
 **/
void initScales() {
    for (int x = 0; x < NUM_SCALES; x++) {
        initScale(x);
    }
}

/**
 * Read a scale and check if the value needs to be updated.
 **/
void readScale(int index) {
    float reading = 0;
    scales[index].power_up();
    reading = scales[index].get_units(10);
    scales[index].power_down();
    Serial.printf("Reading scale %d: %f\r\n", index, reading);
    checkData(index, reading);
}

/**
 * Check a data point from a scale and push to firebase if needed.
 **/
void checkData(int index, float newValue) {

    // Range check the new value.
    float _newValue = newValue;
    if (_newValue < 0) { 
        _newValue = 0;
    } else if (_newValue > MAX_SCALE_VAL) {
        _newValue = MAX_SCALE_VAL;
    }

    // If the new value has changed by more than some minimum amount...
    if (abs(_newValue - lastVals[index]) > MIN_CHANGE_VAL) {

        // Create JSON object with new value and a timestamp.
        DynamicJsonBuffer buff;
        JsonObject& scaleVal = buff.createObject();
        JsonObject& timeStamp = scaleVal.createNestedObject("timeStamp");
        timeStamp[".sv"] = "timestamp";
        scaleVal["value"] = _newValue;

        // Push new val to firebase and update the check value.
        Firebase.push(pathToScale(index, "weightData"), scaleVal);
        lastVals[index] = _newValue;
    }
}

/**
 * Get firebase path to a value for a specific scale number.
 * @value Data value to get path to.
 * @index Scale number to get value for.
 **/
String pathToScale(int scaleNum, String value) {
    char pathStr[20];
    sprintf(pathStr, SCALE_PATH, scaleNum);
    return String(pathStr) + "/" + value;
}

#pragma endregion

#pragma region Calibrate methods

// Flags to indicate firebase flag reset success.
bool zeroResetFlags[NUM_SCALES] = {};
bool calibrateResetFlags[NUM_SCALES] = {};

/**
 * Get zero and offset values for a scale from firebase.
 **/
void initScale(int index) {
    zeroResetFlags[index] = true;
    calibrateResetFlags[index] = true;
    int zeroPoint = Firebase.getInt(pathToScale(index, ZERO_POINT));
    float calPoint = Firebase.getFloat(pathToScale(index, CALIBRATION_POINT));
    printf("Initializing scale %d. Zero: %d, Cal: %f\r\n", index, zeroPoint, calPoint);
    scales[index].set_offset(zeroPoint);
    scales[index].set_scale(calPoint);
}

/**
 * Check for signal to zero scales and zero them if signal received.
 **/
void checkZero(int index) {
    
    // If previous reset failed, then try again.
    if (!zeroResetFlags[index]) {
        zeroResetFlags[index] = resetZeroFlag(index, 0);

        // If reset still failed, then bail.
        if (!zeroResetFlags[index]) { return; }
    }

    // If last reset success, and get is true, then we have another request to zero.
    if(Firebase.getBool(pathToScale(index, ZERO_FLAG))) {

        // Zero the scales and reset the zero flag.
        Serial.printf("Zeroing scale %d... ", index);
        scales[index].power_up();
        scales[index].set_scale();
        scales[index].tare();
        int zero = scales[index].read_average();
        scales[index].power_down();
        Serial.printf("complete. Zero point: %d\r\n", zero);
        zeroResetFlags[index] = resetZeroFlag(index, zero);
    }
}

/**
 * Reset the flag to zero the scales.
 **/
bool resetZeroFlag(int index, int zero) {

    // Clear the zero flag and get the result of the clear operation.
    bool success = false;
    Firebase.setInt(pathToScale(index, ZERO_POINT), zero);
    Firebase.setBool(pathToScale(index, ZERO_FLAG), false);
    success = Firebase.success();

    // If clearing fails, then log the error.
    if (!success) {
        Serial.printf("Zero flag reset failed for scale %d", index);
        Serial.println(Firebase.error());
    }

    // Return the result of the clear operation.
    return success;
}

/**
 * Check for signal to calibrate scales and calibrate them if signal received.
 **/
void checkCalibrate(int index) {
    
    // If previous reset failed, then try again.
    if (!calibrateResetFlags[index]) {
        calibrateResetFlags[index] = resetCalibrateFlag(index, 0);

        // If reset still failed, then bail.
        if (!calibrateResetFlags[index]) { return; }
    }

    // If last reset success, and get is true, then we have another request to calibrate.
    if(Firebase.getBool(pathToScale(index, CALIBRATE_FLAG))) {

        yield();

        // Calibrate the scales and reset the calibrate flag.
        Serial.printf("Calibrating scale %d... ", index);
        float calValue = Firebase.getFloat(pathToScale(index, CALIBRATION_VALUE));
        if (calValue <= 0) { calValue = 1; }
        scales[index].power_up();
        int raw = scales[index].get_units(10);
        float result = raw / calValue;
        scales[index].set_scale(result);
        scales[index].power_down();
        Serial.printf("Success! Target: %fg, Raw: %d, Result: %f\r\n", calValue, raw, result);
        calibrateResetFlags[index] = resetCalibrateFlag(index, result);
    }
}

/**
 * Reset the flag to calibrate the scales.
 **/
bool resetCalibrateFlag(int index, float calibration) {

    // Clear the calibrate flag and get the result of the clear operation.
    bool success = false;
    Firebase.setFloat(pathToScale(index, CALIBRATION_POINT), calibration);
    Firebase.setBool(pathToScale(index, CALIBRATE_FLAG), false);
    success = Firebase.success();

    // If clearing fails, then log the error.
    if (!success) {
        Serial.printf("Calibration flag reset failed for scale %d", index);
        Serial.println(Firebase.error());
    }

    // Return the result of the clear operation.
    return success;
}

#pragma endregion
