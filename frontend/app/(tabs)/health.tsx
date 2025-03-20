import React, { useState } from 'react';
import { StyleSheet, Platform, TouchableOpacity } from 'react-native';
import { Text, View } from '@/components/Themed';
import HuaweiHealthComponent from '@/actions/Huawei';
import AppleHealthComponent from '@/actions/Apple';

export default function HealthScreen() {
  const [activeTab, setActiveTab] = useState<'apple' | 'huawei'>(
    Platform.OS === 'ios' ? 'apple' : 'huawei'
  );

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Health</Text>
      <View style={styles.separator} lightColor="#eee" darkColor="rgba(255,255,255,0.1)" />
      
      <View style={styles.tabContainer}>
        <TouchableOpacity 
          style={[
            styles.tabButton, 
            activeTab === 'apple' && styles.activeTabButton
          ]}
          onPress={() => setActiveTab('apple')}
        >
          <Text 
            style={[
              styles.tabButtonText, 
              activeTab === 'apple' && styles.activeTabButtonText
            ]}
          >
            Apple Health
          </Text>
        </TouchableOpacity>
        
        <TouchableOpacity 
          style={[
            styles.tabButton, 
            activeTab === 'huawei' && styles.activeTabButton
          ]}
          onPress={() => setActiveTab('huawei')}
        >
          <Text 
            style={[
              styles.tabButtonText, 
              activeTab === 'huawei' && styles.activeTabButtonText
            ]}
          >
            Huawei Health
          </Text>
        </TouchableOpacity>
      </View>
      
      <View style={styles.contentContainer}>
        {activeTab === 'apple' ? (
          <AppleHealthComponent />
        ) : (
          <HuaweiHealthComponent />
        )}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginTop: 20,
    marginHorizontal: 16,
  },
  separator: {
    marginVertical: 16,
    height: 1,
    width: '90%',
    alignSelf: 'center',
  },
  tabContainer: {
    flexDirection: 'row',
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
    marginHorizontal: 16,
  },
  tabButton: {
    paddingVertical: 12,
    paddingHorizontal: 16,
    marginRight: 8,
  },
  activeTabButton: {
    borderBottomWidth: 2,
    borderBottomColor: '#2196F3',
  },
  tabButtonText: {
    fontSize: 16,
    color: '#666',
  },
  activeTabButtonText: {
    color: '#2196F3',
    fontWeight: '600',
  },
  contentContainer: {
    flex: 1,
  },
  placeholderContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
  },
  placeholderText: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
  }
}); 