import React, { useState, useEffect } from 'react';
import { StyleSheet, TouchableOpacity, ScrollView, ActivityIndicator, Platform } from 'react-native';
import { Text, View } from '@/components/Themed';

// Mock HealthKit interface - in a real app, you would import from 'react-native-health'
interface HealthData {
  steps?: number;
  distance?: number;
  calories?: number;
  heartRate?: number;
  sleepHours?: number;
  bloodPressure?: {
    systolic: number;
    diastolic: number;
  };
  oxygenSaturation?: number;
  weight?: number;
  bodyFatPercentage?: number;
  lastFetchDate: Date;
}

export default function AppleHealthComponent() {
  const [loading, setLoading] = useState<boolean>(false);
  const [authorized, setAuthorized] = useState<boolean | null>(null);
  const [healthData, setHealthData] = useState<HealthData | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // Check authorization on component mount
    checkAuthorization();
  }, []);

  const checkAuthorization = async () => {
    if (Platform.OS !== 'ios') {
      setError('Apple HealthKit is only available on iOS devices');
      return;
    }

    // In a real app, you would check HealthKit authorization here
    setLoading(true);
    try {
      // Mock authorization check
      setTimeout(() => {
        setAuthorized(true);
        setLoading(false);
      }, 1000);
    } catch (err) {
      setError('Failed to check HealthKit authorization');
      setLoading(false);
    }
  };

  const authorize = async () => {
    if (Platform.OS !== 'ios') {
      setError('Apple HealthKit is only available on iOS devices');
      return;
    }

    setLoading(true);
    setError(null);
    try {
      // In a real app, you would request HealthKit permissions here
      setTimeout(() => {
        setAuthorized(true);
        fetchData();
      }, 1000);
    } catch (err) {
      setError('Error during HealthKit authorization');
      setLoading(false);
    }
  };

  const fetchData = async () => {
    if (Platform.OS !== 'ios') {
      setError('Apple HealthKit is only available on iOS devices');
      return;
    }

    setLoading(true);
    setError(null);
    try {
      // In a real app, you would fetch actual HealthKit data here
      // This is mocked data for demonstration
      setTimeout(() => {
        const mockData: HealthData = {
          steps: 9876,
          distance: 7564, // meters
          calories: 432,
          heartRate: 68,
          sleepHours: 7.2,
          bloodPressure: {
            systolic: 120,
            diastolic: 80
          },
          oxygenSaturation: 98,
          weight: 70.5,
          bodyFatPercentage: 18.2,
          lastFetchDate: new Date()
        };
        setHealthData(mockData);
        setLoading(false);
      }, 1500);
    } catch (err) {
      setError('Error fetching health data');
      setLoading(false);
    }
  };

  const openHealthAppSettings = () => {
    // In a real app, you would use a deep link to open the Health app
    alert('This would open Health app settings on a real device');
  };

  if (Platform.OS !== 'ios') {
    return (
      <View style={styles.container}>
        <Text style={styles.title}>Apple HealthKit</Text>
        <Text style={styles.errorText}>
          Apple HealthKit is only available on iOS devices.
        </Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Apple HealthKit</Text>
      
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
            <Text style={styles.buttonText}>Connect to Apple Health</Text>
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
            
            {healthData.heartRate !== undefined && (
              <View style={styles.dataRow}>
                <Text style={styles.dataLabel}>Heart Rate:</Text>
                <Text style={styles.dataValue}>{healthData.heartRate} BPM</Text>
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