/**
 * Zustand Filter Store
 * Replaces SharedPreferences for filter preferences
 */

import { create } from 'zustand';
import { MovieFilter } from './movieStore';

/**
 * Filter Store interface
 * Manages user preferences for which movie types to display
 */
interface FilterStore {
  // State
  showPopular: boolean;
  showTopRated: boolean;
  showFavorites: boolean;

  // Actions
  togglePopular: () => void;
  toggleTopRated: () => void;
  toggleFavorites: () => void;
  getActiveFilter: () => MovieFilter;
  setActiveFilter: (filter: MovieFilter) => void;
}

/**
 * Create Zustand store for filter preferences
 * Replaces SharedPreferences from Android app
 */
export const useFilterStore = create<FilterStore>((set, get) => ({
  // Initial state (matches Android defaults)
  showPopular: true,
  showTopRated: true,
  showFavorites: false,

  /**
   * Toggle popular movies filter
   * When toggled on, sets other filters off
   */
  togglePopular: () => {
    const current = get().showPopular;
    set({
      showPopular: !current,
      showTopRated: current, // If turning on popular, turn off others
      showFavorites: current,
    });
  },

  /**
   * Toggle top-rated movies filter
   * When toggled on, sets other filters off
   */
  toggleTopRated: () => {
    const current = get().showTopRated;
    set({
      showPopular: current, // If turning on toprated, turn off others
      showTopRated: !current,
      showFavorites: current,
    });
  },

  /**
   * Toggle favorites filter
   * When toggled on, sets other filters off
   */
  toggleFavorites: () => {
    const current = get().showFavorites;
    set({
      showPopular: current, // If turning on favorites, turn off others
      showTopRated: current,
      showFavorites: !current,
    });
  },

  /**
   * Get the currently active filter
   * Returns which filter is currently enabled
   *
   * @returns Active filter type
   */
  getActiveFilter: (): MovieFilter => {
    const { showPopular, showTopRated, showFavorites } = get();

    if (showFavorites) return 'favorites';
    if (showTopRated) return 'toprated';
    if (showPopular) return 'popular';

    // Default to popular if none selected
    return 'popular';
  },

  /**
   * Set active filter programmatically
   * Updates toggle states to match the specified filter
   *
   * @param filter - Filter type to activate
   */
  setActiveFilter: (filter: MovieFilter) => {
    set({
      showPopular: filter === 'popular',
      showTopRated: filter === 'toprated',
      showFavorites: filter === 'favorites',
    });
  },
}));
