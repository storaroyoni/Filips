import React, { useEffect, useState } from 'react';
import { withLayoutContext } from 'expo-router';
import { AntDesign, EvilIcons, Ionicons, FontAwesome } from '@expo/vector-icons';
import {
  createMaterialTopTabNavigator,
  MaterialTopTabNavigationOptions,
  MaterialTopTabNavigationEventMap,
} from '@react-navigation/material-top-tabs';
import { ParamListBase, TabNavigationState } from '@react-navigation/native';
import { ActivityIndicator, Keyboard, View } from 'react-native';

import Colors from '@/constants/Colors';
import { useColorScheme } from '@/components/useColorScheme';
import { useClientOnlyValue } from '@/components/useClientOnlyValue';

const { Navigator } = createMaterialTopTabNavigator();

export const MaterialTopTabs = withLayoutContext<
  MaterialTopTabNavigationOptions,
  typeof Navigator,
  TabNavigationState<ParamListBase>,
  MaterialTopTabNavigationEventMap
>(Navigator);

// You can explore the built-in icon families and icons on the web at https://icons.expo.fyi/
function TabBarIcon(props: {
  name: React.ComponentProps<typeof FontAwesome>['name'];
  color: string;
}) {
  return <FontAwesome size={28} style={{ marginBottom: -3 }} {...props} />;
}

export default function TabLayout() {
  const colorScheme = useColorScheme();
  const [keyboardVisible, setKeyboardVisible] = useState(false);

  useEffect(() => {
    const keyboardDidShowListener = Keyboard.addListener(
      'keyboardDidShow',
      () => {
        setKeyboardVisible(true);
      }
    );
    const keyboardDidHideListener = Keyboard.addListener(
      'keyboardDidHide',
      () => {
        setKeyboardVisible(false);
      }
    );

    return () => {
      keyboardDidShowListener.remove();
      keyboardDidHideListener.remove();
    };
  }, []);

  const tabBarDisplay: "none" | undefined = keyboardVisible
    ? "none"
    : undefined;

  return (
    <MaterialTopTabs
      tabBarPosition="bottom"
      initialRouteName="index"
      screenOptions={{
        animationEnabled: false,
        lazy: true,
        lazyPreloadDistance: 0,
        lazyPlaceholder: () => (
          <View
            style={{
              flex: 1,
              backgroundColor: 'white',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            <ActivityIndicator
              size="large"
              color={Colors[colorScheme ?? 'light'].tint}
            />
          </View>
        ),
        tabBarInactiveTintColor: 'gray',
        tabBarActiveTintColor: Colors[colorScheme ?? 'light'].tint,
        tabBarShowIcon: true,
        tabBarShowLabel: false,
        tabBarGap: 0,
        tabBarStyle: {
          display: tabBarDisplay,
          backgroundColor: Colors[colorScheme ?? 'light'].background,
          borderTopColor: 'rgba(0, 0, 0, 0.1)',
          borderTopWidth: 1,
        },
        tabBarIndicatorStyle: {
          backgroundColor: Colors[colorScheme ?? 'light'].tint,
          position: 'absolute',
          top: -1,
        },
      }}
    >
      <MaterialTopTabs.Screen
        name="index"
        options={{
          lazy: true,
          title: 'Home',
          tabBarLabel: 'Home',
          tabBarIcon: ({ color }) => (
            <AntDesign
              name="home"
              style={{ marginBottom: -3, fontSize: 26 }}
              color={color}
            />
          ),
        }}
      />
      <MaterialTopTabs.Screen
        name="dashboard"
        options={{
          lazy: true,
          tabBarLabel: 'Dashboard',
          tabBarIcon: ({ color }) => (
            <AntDesign
              name="dashboard"
              style={{ marginBottom: -3, fontSize: 26 }}
              color={color}
            />
          ),
        }}
      />
      <MaterialTopTabs.Screen
        name="health"
        options={{
          lazy: true,
          tabBarLabel: 'Health',
          tabBarIcon: ({ color }) => (
            <FontAwesome
              name="heartbeat"
              style={{ marginBottom: -3, fontSize: 26 }}
              color={color}
            />
          ),
        }}
      />
      <MaterialTopTabs.Screen
        name="explore"
        options={{
          lazy: true,
          tabBarLabel: 'Explore',
          tabBarIcon: ({ color }) => (
            <EvilIcons
              name="search"
              style={{ fontSize: 30 }}
              color={color}
            />
          ),
        }}
      />
      <MaterialTopTabs.Screen
        name="profile"
        options={{
          lazy: true,
          tabBarLabel: 'Profile',
          tabBarIcon: ({ color }) => (
            <EvilIcons
              name="user"
              style={{
                fontSize: 30,
                marginLeft: -1,
              }}
              color={color}
            />
          ),
        }}
      />
    </MaterialTopTabs>
  );
}
