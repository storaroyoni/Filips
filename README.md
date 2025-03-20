# Filips

# Health App Integration

This app demonstrates integration with health platforms on iOS and Android devices:

- Apple HealthKit for iOS devices
- Huawei Health Kit for Huawei devices running HMS Core

## Features

- View health data like steps, heart rate, sleep hours, and more
- Automatic detection of device platform to show appropriate health integration
- User-friendly interface to request permissions and fetch data
- Modern UI with Material Top Tab navigation

## Setup

### Prerequisites

- Node.js and npm
- React Native environment
- For iOS: Xcode
- For Android: Android Studio
- For Huawei: HMS Core installed on device

### Installation

1. Clone the repository
2. Install dependencies:
```bash
npm install
```

3. Install specific health kit packages:
```bash
# For Apple HealthKit
npm install react-native-health

# For Huawei Health Kit
npm install @hmscore/react-native-hms-health
```

4. For iOS, install pods:
```bash
cd ios && pod install && cd ..
```

### Running the App

```bash
# Start the development server
npm start

# For iOS
npm run ios

# For Android
npm run android
```

## Usage

The app detects which platform you're on and presents the appropriate health integration option. You can also manually toggle between Apple Health and Huawei Health views.

### Apple HealthKit (iOS)

1. Tap "Connect to Apple Health"
2. Accept the permission requests
3. View your health data
4. Tap "Refresh Health Data" to update

### Huawei Health (Android with HMS)

1. Tap "Connect to Huawei Health"
2. Accept the permission requests in the HMS Core app
3. View your health data
4. Tap "Refresh Health Data" to update

## App Structure

- `frontend/app/(tabs)/health.tsx` - Main health tab with platform detection
- `frontend/actions/Apple.tsx` - Apple HealthKit integration
- `frontend/actions/Huawei.tsx` - Huawei Health integration

## Permissions

The app requests the following permissions:

### Apple HealthKit
- Steps
- Distance
- Heart Rate
- Sleep Analysis
- Active Energy
- Blood Pressure
- Blood Glucose
- Oxygen Saturation
- Body Mass
- Body Fat Percentage

### Huawei Health
- Steps
- Distance
- Calories Burned
- Heart Rate
- Sleep
- Blood Pressure
- Blood Glucose
- Oxygen Saturation
- Weight
- Body Fat Percentage

## How It Works

Both Apple HealthKit and Huawei Health integrations follow a similar pattern:

1. Check if the device can access the health platform
2. Request necessary permissions
3. Fetch data from the health platform
4. Present the data in a user-friendly interface

## Further Development

This app can be extended with:

- Ability to write workout data back to health platforms
- Graphical representation of health data over time
- More health metrics like activity minutes, workouts, etc.
- Cloud sync to maintain data between devices
