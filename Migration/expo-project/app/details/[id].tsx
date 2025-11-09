import React from 'react';
import { View, StyleSheet } from 'react-native';
import { Text } from 'react-native-paper';
import { useLocalSearchParams } from 'expo-router';

/**
 * Movie Details Screen (Placeholder)
 * Will be implemented fully in Task 4
 */
export default function DetailsScreen(): React.JSX.Element {
  const params = useLocalSearchParams<{ id: string }>();
  const movieId = params.id;

  return (
    <View style={styles.container}>
      <Text variant="headlineMedium">Movie Details</Text>
      <Text variant="bodyMedium">Details for movie ID: {movieId}</Text>
      <Text variant="bodySmall">Full implementation in Task 4</Text>
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
