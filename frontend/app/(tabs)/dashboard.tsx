import React, { useState, useEffect } from 'react';
import { StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { AntDesign } from '@expo/vector-icons';

import { Text, View } from '@/components/Themed';

interface DashboardCard {
  title: string;
  value: string;
  icon: string;
  unit?: string;
  color: string;
}

export default function DashboardScreen() {
  const [dashboardData, setDashboardData] = useState<DashboardCard[]>([
    { 
      title: 'Steps Today', 
      value: '8,453', 
      icon: 'stepforward', 
      unit: 'steps',
      color: '#4CAF50'
    },
    { 
      title: 'Sleep Last Night', 
      value: '7.5', 
      icon: 'eye', 
      unit: 'hours',
      color: '#9C27B0'
    },
    { 
      title: 'Heart Rate', 
      value: '72', 
      icon: 'heart', 
      unit: 'bpm',
      color: '#E91E63'
    },
    { 
      title: 'Calories Burned', 
      value: '387', 
      icon: 'fire', 
      unit: 'kcal',
      color: '#FF9800'
    },
    { 
      title: 'Water Intake', 
      value: '1.8', 
      icon: 'dropbox', 
      unit: 'liters',
      color: '#2196F3'
    },
    { 
      title: 'Activity Minutes', 
      value: '45', 
      icon: 'clockcircle', 
      unit: 'min',
      color: '#795548'
    },
  ]);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Dashboard</Text>
      <View style={styles.separator} lightColor="#eee" darkColor="rgba(255,255,255,0.1)" />
      
      <ScrollView contentContainerStyle={styles.scrollViewContent}>
        <View style={styles.cardsContainer}>
          {dashboardData.map((card, index) => (
            <TouchableOpacity key={index} style={styles.card}>
              <View style={[styles.cardHeader, { backgroundColor: card.color }]}>
                <AntDesign name={card.icon as any} size={24} color="white" />
                <Text style={styles.cardTitle}>{card.title}</Text>
              </View>
              <View style={styles.cardBody}>
                <Text style={styles.cardValue}>{card.value}</Text>
                {card.unit && <Text style={styles.cardUnit}>{card.unit}</Text>}
              </View>
            </TouchableOpacity>
          ))}
        </View>
        
        <View style={styles.sectionContainer}>
          <Text style={styles.sectionTitle}>Recent Activities</Text>
          <View style={styles.activityContainer}>
            <Text style={styles.activityEmptyText}>No recent activities to display</Text>
          </View>
        </View>
        
        <View style={styles.sectionContainer}>
          <Text style={styles.sectionTitle}>Health Goals</Text>
          <View style={styles.goalsContainer}>
            <Text style={styles.goalText}>• Walk 10,000 steps daily</Text>
            <Text style={styles.goalText}>• Sleep 8 hours each night</Text>
            <Text style={styles.goalText}>• Drink 2.5 liters of water</Text>
            <Text style={styles.goalText}>• 30 minutes of exercise</Text>
          </View>
        </View>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  scrollViewContent: {
    padding: 16,
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
  cardsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  card: {
    width: '48%',
    backgroundColor: 'white',
    borderRadius: 8,
    marginBottom: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
    overflow: 'hidden',
  },
  cardHeader: {
    padding: 12,
    flexDirection: 'row',
    alignItems: 'center',
  },
  cardTitle: {
    color: 'white',
    fontWeight: '600',
    marginLeft: 8,
    fontSize: 14,
  },
  cardBody: {
    padding: 16,
    alignItems: 'center',
  },
  cardValue: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
  },
  cardUnit: {
    fontSize: 14,
    color: '#666',
    marginTop: 4,
  },
  sectionContainer: {
    marginTop: 24,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 12,
  },
  activityContainer: {
    backgroundColor: 'white',
    borderRadius: 8,
    padding: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: 100,
  },
  activityEmptyText: {
    color: '#666',
    fontStyle: 'italic',
  },
  goalsContainer: {
    backgroundColor: 'white',
    borderRadius: 8,
    padding: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  goalText: {
    fontSize: 16,
    color: '#333',
    marginBottom: 8,
  },
}); 