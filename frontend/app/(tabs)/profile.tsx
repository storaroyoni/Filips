import React, { useState } from 'react';
import { StyleSheet, Image, TouchableOpacity, Switch, ScrollView, Alert } from 'react-native';
import { AntDesign, MaterialIcons, Ionicons } from '@expo/vector-icons';

import { Text, View } from '@/components/Themed';

interface ProfileOption {
  icon: React.ReactNode;
  title: string;
  subtitle?: string;
  action: () => void;
  showArrow?: boolean;
  rightElement?: React.ReactNode;
}

export default function ProfileScreen() {
  const [notificationsEnabled, setNotificationsEnabled] = useState(true);
  const [darkModeEnabled, setDarkModeEnabled] = useState(false);
  const [healthSyncEnabled, setHealthSyncEnabled] = useState(true);

  const toggleNotifications = () => {
    setNotificationsEnabled(previousState => !previousState);
  };

  const toggleDarkMode = () => {
    setDarkModeEnabled(previousState => !previousState);
  };

  const toggleHealthSync = () => {
    setHealthSyncEnabled(previousState => !previousState);
  };

  const handleLogout = () => {
    Alert.alert(
      'Logout',
      'Are you sure you want to logout?',
      [
        { text: 'Cancel', style: 'cancel' },
        { text: 'Logout', style: 'destructive', onPress: () => console.log('User logged out') }
      ]
    );
  };

  const profileOptions: ProfileOption[] = [
    {
      icon: <MaterialIcons name="person" size={24} color="#2196F3" />,
      title: 'Edit Profile',
      subtitle: 'Change your personal information',
      action: () => console.log('Navigate to edit profile'),
      showArrow: true,
    },
    {
      icon: <MaterialIcons name="notifications" size={24} color="#FF9800" />,
      title: 'Notifications',
      subtitle: 'Manage your notification settings',
      action: toggleNotifications,
      rightElement: (
        <Switch 
          value={notificationsEnabled} 
          onValueChange={toggleNotifications}
          trackColor={{ false: '#767577', true: '#4CAF50' }}
        />
      ),
    },
    {
      icon: <MaterialIcons name="brightness-6" size={24} color="#9C27B0" />,
      title: 'Dark Mode',
      subtitle: 'Toggle app theme',
      action: toggleDarkMode,
      rightElement: (
        <Switch 
          value={darkModeEnabled} 
          onValueChange={toggleDarkMode}
          trackColor={{ false: '#767577', true: '#4CAF50' }}
        />
      ),
    },
    {
      icon: <Ionicons name="ios-fitness" size={24} color="#E91E63" />,
      title: 'Health Sync',
      subtitle: 'Sync with Apple Health or Google Fit',
      action: toggleHealthSync,
      rightElement: (
        <Switch 
          value={healthSyncEnabled} 
          onValueChange={toggleHealthSync}
          trackColor={{ false: '#767577', true: '#4CAF50' }}
        />
      ),
    },
    {
      icon: <MaterialIcons name="security" size={24} color="#4CAF50" />,
      title: 'Privacy & Security',
      subtitle: 'Manage your privacy settings',
      action: () => console.log('Navigate to privacy'),
      showArrow: true,
    },
    {
      icon: <MaterialIcons name="help-outline" size={24} color="#795548" />,
      title: 'Help & Support',
      subtitle: 'Get help or contact support',
      action: () => console.log('Navigate to help'),
      showArrow: true,
    },
    {
      icon: <MaterialIcons name="info-outline" size={24} color="#607D8B" />,
      title: 'About',
      subtitle: 'App version and information',
      action: () => console.log('Navigate to about'),
      showArrow: true,
    },
    {
      icon: <MaterialIcons name="logout" size={24} color="#F44336" />,
      title: 'Logout',
      action: handleLogout,
      showArrow: true,
    },
  ];

  return (
    <View style={styles.container}>
      <ScrollView>
        <View style={styles.header}>
          <View style={styles.avatarContainer}>
            <Image
              source={{ uri: 'https://randomuser.me/api/portraits/women/43.jpg' }}
              style={styles.avatar}
            />
            <TouchableOpacity style={styles.editAvatarButton}>
              <AntDesign name="camerao" size={20} color="white" />
            </TouchableOpacity>
          </View>
          <Text style={styles.userName}>Jane Doe</Text>
          <Text style={styles.userEmail}>jane.doe@example.com</Text>
          <TouchableOpacity style={styles.editProfileButton}>
            <Text style={styles.editProfileButtonText}>Edit Profile</Text>
          </TouchableOpacity>
          
          <View style={styles.statsContainer}>
            <View style={styles.statItem}>
              <Text style={styles.statValue}>253</Text>
              <Text style={styles.statLabel}>Workouts</Text>
            </View>
            <View style={styles.statDivider} />
            <View style={styles.statItem}>
              <Text style={styles.statValue}>48</Text>
              <Text style={styles.statLabel}>Goals</Text>
            </View>
            <View style={styles.statDivider} />
            <View style={styles.statItem}>
              <Text style={styles.statValue}>7.5k</Text>
              <Text style={styles.statLabel}>Steps</Text>
            </View>
          </View>
        </View>
        
        <View style={styles.optionsContainer}>
          {profileOptions.map((option, index) => (
            <TouchableOpacity 
              key={index} 
              style={[
                styles.optionItem,
                index === profileOptions.length - 1 && styles.lastOptionItem
              ]}
              onPress={option.action}
            >
              <View style={styles.optionIconContainer}>
                {option.icon}
              </View>
              <View style={styles.optionTextContainer}>
                <Text style={styles.optionTitle}>{option.title}</Text>
                {option.subtitle && (
                  <Text style={styles.optionSubtitle}>{option.subtitle}</Text>
                )}
              </View>
              <View style={styles.optionRightElement}>
                {option.rightElement ? (
                  option.rightElement
                ) : option.showArrow ? (
                  <MaterialIcons name="keyboard-arrow-right" size={24} color="#aaa" />
                ) : null}
              </View>
            </TouchableOpacity>
          ))}
        </View>
        
        <View style={styles.footerContainer}>
          <Text style={styles.versionText}>Version 1.0.0</Text>
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
    alignItems: 'center',
    paddingTop: 30,
    paddingBottom: 20,
    paddingHorizontal: 20,
  },
  avatarContainer: {
    position: 'relative',
    marginBottom: 16,
  },
  avatar: {
    width: 100,
    height: 100,
    borderRadius: 50,
  },
  editAvatarButton: {
    position: 'absolute',
    right: 0,
    bottom: 0,
    backgroundColor: '#2196F3',
    width: 36,
    height: 36,
    borderRadius: 18,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 3,
    borderColor: 'white',
  },
  userName: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 4,
  },
  userEmail: {
    fontSize: 16,
    color: '#666',
    marginBottom: 16,
  },
  editProfileButton: {
    paddingHorizontal: 20,
    paddingVertical: 8,
    backgroundColor: '#f0f0f0',
    borderRadius: 20,
    marginBottom: 20,
  },
  editProfileButtonText: {
    color: '#333',
    fontWeight: '600',
  },
  statsContainer: {
    flexDirection: 'row',
    width: '100%',
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  statItem: {
    flex: 1,
    alignItems: 'center',
  },
  statValue: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
  },
  statLabel: {
    fontSize: 14,
    color: '#666',
    marginTop: 4,
  },
  statDivider: {
    width: 1,
    height: '100%',
    backgroundColor: '#e0e0e0',
  },
  optionsContainer: {
    marginTop: 24,
    backgroundColor: 'white',
    borderRadius: 12,
    marginHorizontal: 16,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
    overflow: 'hidden',
  },
  optionItem: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 16,
    paddingHorizontal: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  lastOptionItem: {
    borderBottomWidth: 0,
  },
  optionIconContainer: {
    width: 40,
    alignItems: 'flex-start',
  },
  optionTextContainer: {
    flex: 1,
  },
  optionTitle: {
    fontSize: 16,
    fontWeight: '500',
    color: '#333',
  },
  optionSubtitle: {
    fontSize: 14,
    color: '#666',
    marginTop: 2,
  },
  optionRightElement: {
    marginLeft: 8,
  },
  footerContainer: {
    marginTop: 30,
    marginBottom: 30,
    alignItems: 'center',
  },
  versionText: {
    fontSize: 14,
    color: '#999',
  },
}); 