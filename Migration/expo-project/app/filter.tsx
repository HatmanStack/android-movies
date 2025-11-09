import React from 'react';
import { View, StyleSheet } from 'react-native';
import { Text } from 'react-native-paper';

/**
 * Filter Screen (Placeholder)
 * Will be implemented fully in Task 5
 */
export default function FilterScreen(): React.JSX.Element {
  return (
    <View style={styles.container}>
      <Text variant="headlineMedium">Filter Screen</Text>
      <Text variant="bodyMedium">Filter controls will be implemented in Task 5</Text>
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
