import { Platform, StyleSheet, TouchableOpacity, ScrollView, ActivityIndicator } from 'react-native';
import React, { useState, useEffect } from 'react';
import {
  HmsHealthAccount,
  HmsDataController,
  HmsSettingController,
  DataType,
  TimeUnit,
  ScopeLangItem
} from '@hmscore/react-native-hms-health';

import { View, Text } from '@/components/Themed';

// Types for Huawei Health data
export interface HealthData {
  steps?: number;
  distance?: number;
  calories?: number;
  heartRate?: number[];
  sleepHours?: number;
  bloodPressure?: {
    systolic: number;
    diastolic: number;
  };
  bloodGlucose?: number;
  oxygenSaturation?: number;
  weight?: number;
  bodyFatPercentage?: number;
  lastFetchDate: Date;
}

// Define all the permissions we need
const SCOPES = [
  { dataType: DataType.DT_CONTINUOUS_STEPS_DELTA, read: true, write: false },
  { dataType: DataType.DT_CONTINUOUS_STEPS_TOTAL, read: true, write: false },
  { dataType: DataType.DT_INSTANTANEOUS_STEPS_RATE, read: true, write: false },
  { dataType: DataType.DT_CONTINUOUS_DISTANCE_DELTA, read: true, write: false },
  { dataType: DataType.DT_CONTINUOUS_DISTANCE_TOTAL, read: true, write: false },
  { dataType: DataType.DT_CONTINUOUS_CALORIES_BURNT, read: true, write: false },
  { dataType: DataType.DT_INSTANTANEOUS_HEART_RATE, read: true, write: false },
  { dataType: DataType.DT_CONTINUOUS_SLEEP, read: true, write: false },
  { dataType: DataType.DT_INSTANTANEOUS_BLOOD_PRESSURE, read: true, write: false },
  { dataType: DataType.DT_INSTANTANEOUS_BLOOD_GLUCOSE, read: true, write: false },
  { dataType: DataType.DT_INSTANTANEOUS_OXYGEN_SATURATION, read: true, write: false },
  { dataType: DataType.DT_INSTANTANEOUS_BODY_WEIGHT, read: true, write: false },
  { dataType: DataType.DT_INSTANTANEOUS_BODY_FAT, read: true, write: false },
];

/**
 * Initialize Huawei Health and request permissions
 * @returns Promise<boolean> - True if initialization was successful
 */
export async function initializeHealthKit(): Promise<boolean> {
  if (Platform.OS !== 'android') {
    console.log('Huawei Health Kit is only available on Android devices');
    return false;
  }

  try {
    const signInResult = await HmsHealthAccount.signIn();
    if (!signInResult.result) {
      console.error('Error signing in to Huawei Health account');
      return false;
    }

    const scopeLangItems: ScopeLangItem[] = SCOPES.map(scope => ({
      dataType: scope.dataType,
      reqType: scope.read ? 'read' : 'write',
    }));

    const authResult = await HmsHealthAccount.requestAuthorization(scopeLangItems);
    if (!authResult.result) {
      console.error('Failed to get Huawei Health authorization');
      return false;
    }

    console.log('Huawei Health initialized successfully');
    return true;
  } catch (error) {
    console.error('Error initializing Huawei Health:', error);
    return false;
  }
}

/**
 * Check if Huawei Health is authorized and available
 * @returns Promise<boolean> - True if Health kit is authorized
 */
export async function isHealthKitAuthorized(): Promise<boolean> {
  if (Platform.OS !== 'android') {
    return false;
  }

  try {
    const signInResult = await HmsHealthAccount.signIn();
    if (!signInResult.result) {
      return false;
    }

    for (const scope of SCOPES) {
      const scopeLangItem: ScopeLangItem = {
        dataType: scope.dataType,
        reqType: scope.read ? 'read' : 'write',
      };

      const checkResult = await HmsHealthAccount.checkHealthAppAuthorization(scopeLangItem);
      if (!checkResult.result) {
        return false;
      }
    }

    return true;
  } catch (error) {
    console.error('Error checking Huawei Health authorization:', error);
    return false;
  }
}

/**
 * Fetch health data from Huawei Health
 * @param dataType The data type to fetch
 * @param lastFetchDate The date from which to start fetching data
 * @returns Promise with the fetched data or undefined if error
 */
async function fetchHealthData(dataType: DataType, lastFetchDate: Date): Promise<any> {
  try {
    const options = {
      dataType,
      timeUnit: TimeUnit.MILLISECONDS,
      startTime: lastFetchDate.toISOString(),
      endTime: new Date().toISOString(),
    };

    const readResult = await HmsDataController.readTotalData(options);
    if (!readResult.result) {
      return undefined;
    }

    return readResult.totalMaps?.[0]?.map?.value;
  } catch (error) {
    console.error(`Error fetching ${dataType} data:`, error);
    return undefined;
  }
}

/**
 * Fetch new data from Huawei Health if available
 * @param lastFetchDate Date of the last successful fetch to only get new data
 * @returns Promise with the new health data or null if no new data available
 */
export async function fetchNewHealthKitData(lastFetchDate: Date): Promise<HealthData | null> {
  if (Platform.OS !== 'android') {
    console.log('Huawei Health is only available on Android devices');
    return null;
  }

  try {
    const isAuthorized = await isHealthKitAuthorized();
    if (!isAuthorized) {
      const initialized = await initializeHealthKit();
      if (!initialized) {
        console.log('Failed to initialize Huawei Health');
        return null;
      }
    }

    const currentDate = new Date();
    const [
      steps,
      distance,
      calories,
      heartRate,
      sleepHours,
      bloodPressure,
      bloodGlucose,
      oxygenSaturation,
      weight,
      bodyFatPercentage
    ] = await Promise.all([
      fetchHealthData(DataType.DT_CONTINUOUS_STEPS_TOTAL, lastFetchDate),
      fetchHealthData(DataType.DT_CONTINUOUS_DISTANCE_TOTAL, lastFetchDate),
      fetchHealthData(DataType.DT_CONTINUOUS_CALORIES_BURNT, lastFetchDate),
      fetchHealthData(DataType.DT_INSTANTANEOUS_HEART_RATE, lastFetchDate),
      fetchHealthData(DataType.DT_CONTINUOUS_SLEEP, lastFetchDate),
      fetchHealthData(DataType.DT_INSTANTANEOUS_BLOOD_PRESSURE, lastFetchDate),
      fetchHealthData(DataType.DT_INSTANTANEOUS_BLOOD_GLUCOSE, lastFetchDate),
      fetchHealthData(DataType.DT_INSTANTANEOUS_OXYGEN_SATURATION, lastFetchDate),
      fetchHealthData(DataType.DT_INSTANTANEOUS_BODY_WEIGHT, lastFetchDate),
      fetchHealthData(DataType.DT_INSTANTANEOUS_BODY_FAT, lastFetchDate)
    ]);

    const healthData: HealthData = {
      lastFetchDate: currentDate
    };

    if (steps !== undefined) healthData.steps = steps;
    if (distance !== undefined) healthData.distance = distance;
    if (calories !== undefined) healthData.calories = calories;
    if (heartRate !== undefined) healthData.heartRate = heartRate;
    if (sleepHours !== undefined) healthData.sleepHours = sleepHours;
    if (bloodPressure !== undefined) healthData.bloodPressure = bloodPressure;
    if (bloodGlucose !== undefined) healthData.bloodGlucose = bloodGlucose;
    if (oxygenSaturation !== undefined) healthData.oxygenSaturation = oxygenSaturation;
    if (weight !== undefined) healthData.weight = weight;
    if (bodyFatPercentage !== undefined) healthData.bodyFatPercentage = bodyFatPercentage;

    return healthData;
  } catch (error) {
    console.error('Error fetching Huawei Health data:', error);
    return null;
  }
}

// React component for Huawei Health integration
export default function HuaweiHealthComponent() {
  const [loading, setLoading] = useState<boolean>(false);
  const [authorized, setAuthorized] = useState<boolean | null>(null);
  const [healthData, setHealthData] = useState<HealthData | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // Check authorization on component mount
    checkAuthorization();
  }, []);

  const checkAuthorization = async () => {
    if (Platform.OS !== 'android') {
      setError('Huawei Health is only available on Android devices');
      return;
    }

    setLoading(true);
    try {
      const isAuthorized = await isHealthKitAuthorized();
      setAuthorized(isAuthorized);
    } catch (err) {
      setError('Failed to check Huawei Health authorization');
    } finally {
      setLoading(false);
    }
  };

  const authorize = async () => {
    setLoading(true);
    setError(null);
    try {
      const isInitialized = await initializeHealthKit();
      setAuthorized(isInitialized);
      if (isInitialized) {
        fetchData();
      } else {
        setError('Failed to authorize Huawei Health');
      }
    } catch (err) {
      setError('Error during Huawei Health authorization');
    } finally {
      setLoading(false);
    }
  };

  const fetchData = async () => {
    setLoading(true);
    setError(null);
    try {
      // Use 24 hours ago as a default start time if no previous fetch date
      const startDate = healthData?.lastFetchDate || new Date(Date.now() - 24 * 60 * 60 * 1000);
      const newData = await fetchNewHealthKitData(startDate);
      if (newData) {
        setHealthData(newData);
      } else {
        setError('No health data available');
      }
    } catch (err) {
      setError('Error fetching health data');
    } finally {
      setLoading(false);
    }
  };

  // Open Huawei Health app settings
  const openHealthAppSettings = async () => {
    try {
      await HmsSettingController.openHealthAppSetting();
    } catch (err) {
      console.error('Error opening Huawei Health settings:', err);
      setError('Failed to open Huawei Health settings');
    }
  };

  if (Platform.OS !== 'android') {
    return (
      <View style={styles.container}>
        <Text style={styles.title}>Huawei Health</Text>
        <Text style={styles.errorText}>
          Huawei Health is only available on Android devices with HMS Core.
        </Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Huawei Health</Text>
      
      {loading && (
        <View style={styles.loadingContainer}>
          <ActivityIndicator size="large" color="#2196F3" />
          <Text style={styles.loadingText}>Loading...</Text>
        </View>
      )}
      
      {error && <Text style={styles.errorText}>{error}</Text>}
      
      <View style={styles.buttonContainer}>
        {authorized === false && (
          <TouchableOpacity style={styles.button} onPress={authorize}>
            <Text style={styles.buttonText}>Connect to Huawei Health</Text>
          </TouchableOpacity>
        )}
        
        {authorized === true && (
          <>
            <TouchableOpacity style={styles.button} onPress={fetchData}>
              <Text style={styles.buttonText}>Refresh Health Data</Text>
            </TouchableOpacity>
            
            <TouchableOpacity style={[styles.button, styles.secondaryButton]} onPress={openHealthAppSettings}>
              <Text style={styles.buttonText}>Open Health Settings</Text>
            </TouchableOpacity>
          </>
        )}
      </View>
      
      {healthData && (
        <ScrollView style={styles.dataContainer}>
          <Text style={styles.sectionTitle}>Health Data</Text>
          <Text style={styles.lastUpdated}>
            Last updated: {healthData.lastFetchDate.toLocaleString()}
          </Text>
          
          <View style={styles.dataCard}>
            {healthData.steps !== undefined && (
              <View style={styles.dataRow}>
                <Text style={styles.dataLabel}>Steps:</Text>
                <Text style={styles.dataValue}>{healthData.steps.toLocaleString()}</Text>
              </View>
            )}
            
            {healthData.distance !== undefined && (
              <View style={styles.dataRow}>
                <Text style={styles.dataLabel}>Distance:</Text>
                <Text style={styles.dataValue}>{(healthData.distance / 1000).toFixed(2)} km</Text>
              </View>
            )}
            
            {healthData.calories !== undefined && (
              <View style={styles.dataRow}>
                <Text style={styles.dataLabel}>Calories:</Text>
                <Text style={styles.dataValue}>{healthData.calories.toLocaleString()} kcal</Text>
              </View>
            )}
            
            {healthData.heartRate && healthData.heartRate.length > 0 && (
              <View style={styles.dataRow}>
                <Text style={styles.dataLabel}>Heart Rate:</Text>
                <Text style={styles.dataValue}>
                  {healthData.heartRate[healthData.heartRate.length - 1]} BPM
                </Text>
              </View>
            )}
            
            {healthData.sleepHours !== undefined && (
              <View style={styles.dataRow}>
                <Text style={styles.dataLabel}>Sleep:</Text>
                <Text style={styles.dataValue}>{healthData.sleepHours.toFixed(1)} hours</Text>
              </View>
            )}
            
            {healthData.bloodPressure && (
              <View style={styles.dataRow}>
                <Text style={styles.dataLabel}>Blood Pressure:</Text>
                <Text style={styles.dataValue}>
                  {healthData.bloodPressure.systolic}/{healthData.bloodPressure.diastolic} mmHg
                </Text>
              </View>
            )}
            
            {healthData.bloodGlucose !== undefined && (
              <View style={styles.dataRow}>
                <Text style={styles.dataLabel}>Blood Glucose:</Text>
                <Text style={styles.dataValue}>{healthData.bloodGlucose} mg/dL</Text>
              </View>
            )}
            
            {healthData.oxygenSaturation !== undefined && (
              <View style={styles.dataRow}>
                <Text style={styles.dataLabel}>Oxygen Saturation:</Text>
                <Text style={styles.dataValue}>{healthData.oxygenSaturation}%</Text>
              </View>
            )}
            
            {healthData.weight !== undefined && (
              <View style={styles.dataRow}>
                <Text style={styles.dataLabel}>Weight:</Text>
                <Text style={styles.dataValue}>{healthData.weight.toFixed(1)} kg</Text>
              </View>
            )}
            
            {healthData.bodyFatPercentage !== undefined && (
              <View style={styles.dataRow}>
                <Text style={styles.dataLabel}>Body Fat:</Text>
                <Text style={styles.dataValue}>{healthData.bodyFatPercentage.toFixed(1)}%</Text>
              </View>
            )}
          </View>
        </ScrollView>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  loadingContainer: {
    alignItems: 'center',
    marginVertical: 20,
  },
  loadingText: {
    marginTop: 10,
    fontSize: 16,
  },
  errorText: {
    color: 'red',
    marginBottom: 20,
  },
  buttonContainer: {
    marginBottom: 20,
  },
  button: {
    backgroundColor: '#2196F3',
    paddingVertical: 12,
    paddingHorizontal: 20,
    borderRadius: 8,
    alignItems: 'center',
    marginBottom: 10,
  },
  secondaryButton: {
    backgroundColor: '#4CAF50',
  },
  buttonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
  },
  dataContainer: {
    flex: 1,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  lastUpdated: {
    fontSize: 14,
    color: '#666',
    marginBottom: 15,
  },
  dataCard: {
    backgroundColor: 'white',
    borderRadius: 10,
    padding: 15,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
    marginBottom: 20,
  },
  dataRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  dataLabel: {
    fontSize: 16,
    color: '#333',
  },
  dataValue: {
    fontSize: 16,
    fontWeight: '600',
    color: '#2196F3',
  },
});
