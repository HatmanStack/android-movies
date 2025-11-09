import React from 'react';
import { View, StyleSheet } from 'react-native';
import { Text } from 'react-native-paper';

/**
 * Home Screen (Placeholder)
 * Will be implemented fully in Task 3
 */
export default function HomeScreen(): React.JSX.Element {
  return (
    <View style={styles.container}>
      <Text variant="headlineMedium">Home Screen</Text>
      <Text variant="bodyMedium">Movie grid will be implemented in Task 3</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
});
