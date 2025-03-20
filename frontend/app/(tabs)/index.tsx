import React, { useState, useEffect } from 'react';
import { StyleSheet, ScrollView, TouchableOpacity, Image, ImageBackground } from 'react-native';
import { useRouter, Link } from 'expo-router';
import { MaterialIcons, FontAwesome } from '@expo/vector-icons';

import { Text, View } from '@/components/Themed';

interface Workout {
  id: string;
  title: string;
  duration: string;
  image: string;
}

interface HealthTip {
  id: string;
  title: string;
  content: string;
  image: string;
}

export default function HomeScreen() {
  const router = useRouter();
  const [userName, setUserName] = useState('Jane');
  const [todayDate, setTodayDate] = useState('');
  const [currentTime, setCurrentTime] = useState<number>(0);

  useEffect(() => {
    // Get and format current date 
    const date = new Date();
    const options: Intl.DateTimeFormatOptions = { weekday: 'long', month: 'long', day: 'numeric' };
    const formattedDate = date.toLocaleDateString('en-US', options);
    setTodayDate(formattedDate);
    
    // Get current time to determine greeting
    setCurrentTime(date.getHours());
  }, []);

  const getGreeting = () => {
    if (currentTime < 12) {
      return 'Good Morning';
    } else if (currentTime < 18) {
      return 'Good Afternoon';
    } else {
      return 'Good Evening';
    }
  };

  const recentWorkouts: Workout[] = [
    {
      id: '1',
      title: 'Morning Run',
      duration: '30 min',
      image: 'https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60'
    },
    {
      id: '2',
      title: 'Yoga Session',
      duration: '45 min',
      image: 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60'
    },
    {
      id: '3',
      title: 'Cycling',
      duration: '60 min',
      image: 'https://images.unsplash.com/photo-1517649763962-0c623066013b?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60'
    }
  ];

  const healthTips: HealthTip[] = [
    {
      id: '1',
      title: 'Stay Hydrated',
      content: 'Drink at least 8 glasses of water daily to maintain optimal health and energy levels.',
      image: 'https://images.unsplash.com/photo-1570831739435-6601aa3fa4fb?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60'
    },
    {
      id: '2',
      title: 'Mindful Breathing',
      content: 'Practice deep breathing for 5 minutes daily to reduce stress and improve mental clarity.',
      image: 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60'
    }
  ];

  return (
    <View style={styles.container}>
      <ScrollView>
        <View style={styles.header}>
          <View>
            <Text style={styles.greeting}>{getGreeting()}, {userName}</Text>
            <Text style={styles.date}>{todayDate}</Text>
          </View>
          <Link href="/(tabs)/profile" asChild>
            <TouchableOpacity>
              <Image 
                source={{ uri: 'https://randomuser.me/api/portraits/women/43.jpg' }} 
                style={styles.profileImage} 
              />
            </TouchableOpacity>
          </Link>
        </View>
        
        <View style={styles.summaryContainer}>
          <View style={styles.summaryCard}>
            <View style={styles.summaryIconContainer}>
              <FontAwesome name="heartbeat" size={24} color="white" />
            </View>
            <View>
              <Text style={styles.summaryTitle}>72 BPM</Text>
              <Text style={styles.summarySubtitle}>Heart Rate</Text>
            </View>
          </View>
          
          <View style={styles.summaryCard}>
            <View style={[styles.summaryIconContainer, { backgroundColor: '#4CAF50' }]}>
              <MaterialIcons name="directions-walk" size={24} color="white" />
            </View>
            <View>
              <Text style={styles.summaryTitle}>8,543</Text>
              <Text style={styles.summarySubtitle}>Steps</Text>
            </View>
          </View>
          
          <View style={styles.summaryCard}>
            <View style={[styles.summaryIconContainer, { backgroundColor: '#FF9800' }]}>
              <MaterialIcons name="local-fire-department" size={24} color="white" />
            </View>
            <View>
              <Text style={styles.summaryTitle}>387</Text>
              <Text style={styles.summarySubtitle}>Calories</Text>
            </View>
          </View>
        </View>
        
        <View style={styles.sectionContainer}>
          <View style={styles.sectionHeader}>
            <Text style={styles.sectionTitle}>Today's Plan</Text>
            <TouchableOpacity>
              <Text style={styles.seeAllText}>See All</Text>
            </TouchableOpacity>
          </View>
          
          <View style={styles.planCard}>
            <View style={styles.planCardLeft}>
              <Text style={styles.planTime}>08:00 AM</Text>
              <View style={styles.planTimeDot} />
              <View style={styles.planTimeLine} />
            </View>
            <View style={styles.planCardContent}>
              <Text style={styles.planCardTitle}>Morning Run</Text>
              <Text style={styles.planCardSubtitle}>30 minutes • 2.5 miles</Text>
              <View style={styles.planCardButton}>
                <Text style={styles.planCardButtonText}>Start</Text>
              </View>
            </View>
          </View>
          
          <View style={styles.planCard}>
            <View style={styles.planCardLeft}>
              <Text style={styles.planTime}>05:30 PM</Text>
              <View style={styles.planTimeDot} />
            </View>
            <View style={styles.planCardContent}>
              <Text style={styles.planCardTitle}>Evening Yoga</Text>
              <Text style={styles.planCardSubtitle}>45 minutes • Relaxation</Text>
              <View style={styles.planCardButton}>
                <Text style={styles.planCardButtonText}>Start</Text>
              </View>
            </View>
          </View>
        </View>
        
        <View style={styles.sectionContainer}>
          <View style={styles.sectionHeader}>
            <Text style={styles.sectionTitle}>Recent Workouts</Text>
            <TouchableOpacity>
              <Text style={styles.seeAllText}>See All</Text>
            </TouchableOpacity>
          </View>
          
          <ScrollView 
            horizontal
            showsHorizontalScrollIndicator={false}
            contentContainerStyle={styles.workoutsList}
          >
            {recentWorkouts.map((workout) => (
              <TouchableOpacity key={workout.id} style={styles.workoutCard}>
                <ImageBackground
                  source={{ uri: workout.image }}
                  style={styles.workoutImage}
                  imageStyle={{ borderRadius: 12 }}
                >
                  <View style={styles.workoutOverlay}>
                    <Text style={styles.workoutTitle}>{workout.title}</Text>
                    <Text style={styles.workoutDuration}>{workout.duration}</Text>
                  </View>
                </ImageBackground>
              </TouchableOpacity>
            ))}
          </ScrollView>
        </View>
        
        <View style={styles.sectionContainer}>
          <View style={styles.sectionHeader}>
            <Text style={styles.sectionTitle}>Health Tips</Text>
            <TouchableOpacity>
              <Text style={styles.seeAllText}>See All</Text>
            </TouchableOpacity>
          </View>
          
          {healthTips.map((tip) => (
            <TouchableOpacity key={tip.id} style={styles.tipCard}>
              <Image source={{ uri: tip.image }} style={styles.tipImage} />
              <View style={styles.tipContent}>
                <Text style={styles.tipTitle}>{tip.title}</Text>
                <Text style={styles.tipText} numberOfLines={2}>{tip.content}</Text>
              </View>
            </TouchableOpacity>
          ))}
        </View>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 20,
  },
  greeting: {
    fontSize: 22,
    fontWeight: 'bold',
  },
  date: {
    fontSize: 14,
    color: '#666',
    marginTop: 4,
  },
  profileImage: {
    width: 50,
    height: 50,
    borderRadius: 25,
  },
  summaryContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    marginBottom: 24,
  },
  summaryCard: {
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 12,
    flexDirection: 'row',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
    width: '31%',
  },
  summaryIconContainer: {
    backgroundColor: '#E91E63',
    borderRadius: 8,
    width: 36,
    height: 36,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 8,
  },
  summaryTitle: {
    fontSize: 14,
    fontWeight: 'bold',
  },
  summarySubtitle: {
    fontSize: 12,
    color: '#666',
  },
  sectionContainer: {
    marginBottom: 24,
    paddingHorizontal: 16,
  },
  sectionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
  },
  seeAllText: {
    color: '#2196F3',
    fontSize: 14,
  },
  planCard: {
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 16,
    flexDirection: 'row',
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },
  planCardLeft: {
    width: 80,
    alignItems: 'center',
  },
  planTime: {
    fontSize: 12,
    color: '#666',
    marginBottom: 8,
  },
  planTimeDot: {
    width: 12,
    height: 12,
    borderRadius: 6,
    backgroundColor: '#2196F3',
  },
  planTimeLine: {
    width: 2,
    flex: 1,
    backgroundColor: '#e0e0e0',
    marginTop: 4,
  },
  planCardContent: {
    flex: 1,
  },
  planCardTitle: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 4,
  },
  planCardSubtitle: {
    fontSize: 14,
    color: '#666',
    marginBottom: 12,
  },
  planCardButton: {
    backgroundColor: '#2196F3',
    alignSelf: 'flex-start',
    paddingHorizontal: 16,
    paddingVertical: 6,
    borderRadius: 20,
  },
  planCardButtonText: {
    color: 'white',
    fontWeight: '600',
    fontSize: 12,
  },
  workoutsList: {
    paddingBottom: 8,
  },
  workoutCard: {
    width: 160,
    height: 200,
    marginRight: 16,
    borderRadius: 12,
    overflow: 'hidden',
  },
  workoutImage: {
    width: '100%',
    height: '100%',
    justifyContent: 'flex-end',
  },
  workoutOverlay: {
    backgroundColor: 'rgba(0,0,0,0.5)',
    padding: 12,
  },
  workoutTitle: {
    color: 'white',
    fontWeight: 'bold',
    fontSize: 16,
    marginBottom: 4,
  },
  workoutDuration: {
    color: 'rgba(255,255,255,0.8)',
    fontSize: 14,
  },
  tipCard: {
    flexDirection: 'row',
    backgroundColor: 'white',
    borderRadius: 12,
    overflow: 'hidden',
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    elevation: 2,
  },
  tipImage: {
    width: 80,
    height: 80,
  },
  tipContent: {
    flex: 1,
    padding: 12,
  },
  tipTitle: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 4,
  },
  tipText: {
    fontSize: 14,
    color: '#666',
  },
});
