import React, { useState } from 'react';
import { StyleSheet, TextInput, FlatList, TouchableOpacity, ImageBackground } from 'react-native';
import { MaterialIcons, Ionicons } from '@expo/vector-icons';

import { Text, View } from '@/components/Themed';

interface Category {
  id: string;
  name: string;
  image: string;
}

interface Activity {
  id: string;
  title: string;
  description: string;
  duration: string;
  intensity: string;
  image: string;
  category: string;
}

export default function ExploreScreen() {
  const [searchQuery, setSearchQuery] = useState('');

  const categories: Category[] = [
    { id: '1', name: 'Running', image: 'https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60' },
    { id: '2', name: 'Yoga', image: 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60' },
    { id: '3', name: 'Swimming', image: 'https://images.unsplash.com/photo-1560090995-01632a28895b?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60' },
    { id: '4', name: 'Cycling', image: 'https://images.unsplash.com/photo-1517649763962-0c623066013b?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60' },
    { id: '5', name: 'Meditation', image: 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60' },
    { id: '6', name: 'Strength', image: 'https://images.unsplash.com/photo-1540497077202-7c8a3999166f?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60' },
  ];

  const activities: Activity[] = [
    {
      id: '1',
      title: 'Morning Run',
      description: 'Start your day with energy',
      duration: '30 min',
      intensity: 'Medium',
      image: 'https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60',
      category: 'Running'
    },
    {
      id: '2',
      title: 'Sunrise Yoga',
      description: 'Energize your body and mind',
      duration: '45 min',
      intensity: 'Low',
      image: 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60',
      category: 'Yoga'
    },
    {
      id: '3',
      title: 'Pool Laps',
      description: 'Full body workout in the water',
      duration: '40 min',
      intensity: 'High',
      image: 'https://images.unsplash.com/photo-1560090995-01632a28895b?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60',
      category: 'Swimming'
    },
    {
      id: '4',
      title: 'Mountain Biking',
      description: 'Adventure through nature',
      duration: '60 min',
      intensity: 'High',
      image: 'https://images.unsplash.com/photo-1517649763962-0c623066013b?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60',
      category: 'Cycling'
    },
    {
      id: '5',
      title: 'Mindfulness',
      description: 'Focus on the present moment',
      duration: '15 min',
      intensity: 'Low',
      image: 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60',
      category: 'Meditation'
    },
    {
      id: '6',
      title: 'Full Body Workout',
      description: 'Build strength and endurance',
      duration: '50 min',
      intensity: 'High',
      image: 'https://images.unsplash.com/photo-1540497077202-7c8a3999166f?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60',
      category: 'Strength'
    },
  ];

  const filteredActivities = searchQuery
    ? activities.filter(activity => 
        activity.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        activity.category.toLowerCase().includes(searchQuery.toLowerCase()) ||
        activity.description.toLowerCase().includes(searchQuery.toLowerCase())
      )
    : activities;

  const renderCategoryItem = ({ item }: { item: Category }) => (
    <TouchableOpacity style={styles.categoryItem}>
      <ImageBackground 
        source={{ uri: item.image }} 
        style={styles.categoryImage}
        imageStyle={{ borderRadius: 10 }}
      >
        <View style={styles.categoryOverlay}>
          <Text style={styles.categoryName}>{item.name}</Text>
        </View>
      </ImageBackground>
    </TouchableOpacity>
  );

  const renderActivityItem = ({ item }: { item: Activity }) => (
    <TouchableOpacity style={styles.activityItem}>
      <ImageBackground 
        source={{ uri: item.image }} 
        style={styles.activityImage}
        imageStyle={{ borderRadius: 10 }}
      >
        <View style={styles.activityOverlay}>
          <Text style={styles.activityTitle}>{item.title}</Text>
          <Text style={styles.activityCategory}>{item.category}</Text>
          <View style={styles.activityDetailsContainer}>
            <View style={styles.activityDetail}>
              <MaterialIcons name="timer" size={16} color="white" />
              <Text style={styles.activityDetailText}>{item.duration}</Text>
            </View>
            <View style={styles.activityDetail}>
              <MaterialIcons name="fitness-center" size={16} color="white" />
              <Text style={styles.activityDetailText}>{item.intensity}</Text>
            </View>
          </View>
        </View>
      </ImageBackground>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Explore</Text>
      
      <View style={styles.searchContainer}>
        <MaterialIcons name="search" size={24} color="#999" style={styles.searchIcon} />
        <TextInput
          style={styles.searchInput}
          placeholder="Search activities..."
          value={searchQuery}
          onChangeText={setSearchQuery}
          placeholderTextColor="#999"
        />
        {searchQuery.length > 0 && (
          <TouchableOpacity onPress={() => setSearchQuery('')}>
            <MaterialIcons name="clear" size={24} color="#999" />
          </TouchableOpacity>
        )}
      </View>
      
      <View style={styles.sectionContainer}>
        <Text style={styles.sectionTitle}>Categories</Text>
        <FlatList
          data={categories}
          renderItem={renderCategoryItem}
          keyExtractor={item => item.id}
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.categoriesList}
        />
      </View>
      
      <View style={styles.sectionContainer}>
        <Text style={styles.sectionTitle}>
          {searchQuery ? 'Search Results' : 'Popular Activities'}
        </Text>
        <FlatList
          data={filteredActivities}
          renderItem={renderActivityItem}
          keyExtractor={item => item.id}
          numColumns={2}
          columnWrapperStyle={styles.activitiesListColumns}
          contentContainerStyle={styles.activitiesList}
        />
      </View>
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
    marginTop: 20,
    marginBottom: 16,
  },
  searchContainer: {
    flexDirection: 'row',
    backgroundColor: '#f5f5f5',
    borderRadius: 10,
    padding: 10,
    marginBottom: 20,
    alignItems: 'center',
  },
  searchIcon: {
    marginRight: 10,
  },
  searchInput: {
    flex: 1,
    fontSize: 16,
    color: '#333',
  },
  sectionContainer: {
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 12,
  },
  categoriesList: {
    paddingBottom: 8,
  },
  categoryItem: {
    marginRight: 12,
    width: 120,
    height: 80,
    borderRadius: 10,
    overflow: 'hidden',
  },
  categoryImage: {
    width: '100%',
    height: '100%',
    justifyContent: 'flex-end',
  },
  categoryOverlay: {
    backgroundColor: 'rgba(0,0,0,0.4)',
    padding: 8,
    width: '100%',
  },
  categoryName: {
    color: 'white',
    fontWeight: 'bold',
    fontSize: 14,
  },
  activitiesList: {
    paddingBottom: 20,
  },
  activitiesListColumns: {
    justifyContent: 'space-between',
    marginBottom: 16,
  },
  activityItem: {
    width: '48%',
    height: 200,
    borderRadius: 10,
    overflow: 'hidden',
  },
  activityImage: {
    width: '100%',
    height: '100%',
    justifyContent: 'flex-end',
  },
  activityOverlay: {
    backgroundColor: 'rgba(0,0,0,0.5)',
    padding: 12,
    width: '100%',
  },
  activityTitle: {
    color: 'white',
    fontWeight: 'bold',
    fontSize: 16,
    marginBottom: 4,
  },
  activityCategory: {
    color: 'rgba(255,255,255,0.8)',
    fontSize: 14,
    marginBottom: 8,
  },
  activityDetailsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  activityDetail: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  activityDetailText: {
    color: 'white',
    marginLeft: 4,
    fontSize: 12,
  },
}); 