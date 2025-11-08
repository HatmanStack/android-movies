/**
 * Zustand Movie Store
 * Replaces LiveData functionality from Android app
 */

import { create } from 'zustand';
import { MovieDetails } from '../models/types';
import {
  getAllMovies,
  getPopularMovies,
  getTopRatedMovies,
  getFavoriteMovies,
  insertMovie,
  getMovieById,
} from '../database/queries';

/**
 * Filter types for movie loading
 */
export type MovieFilter = 'popular' | 'toprated' | 'favorites' | 'all';

/**
 * Movie Store interface
 * Replaces ViewModel + LiveData pattern from Android
 */
interface MovieStore {
  // State
  movies: MovieDetails[];
  loading: boolean;
  error: string | null;
  filter: MovieFilter;

  // Actions
  loadMovies: (filter: MovieFilter) => Promise<void>;
  loadPopularMovies: () => Promise<void>;
  loadTopRatedMovies: () => Promise<void>;
  loadFavoriteMovies: () => Promise<void>;
  loadAllMovies: () => Promise<void>;
  toggleFavorite: (movieId: number) => Promise<void>;
  refreshMovie: (movieId: number) => Promise<void>;
  clearError: () => void;
}

/**
 * Create Zustand store
 * Implements reactive state management (LiveData replacement)
 */
export const useMovieStore = create<MovieStore>((set, get) => ({
  // Initial state
  movies: [],
  loading: false,
  error: null,
  filter: 'popular',

  /**
   * Load movies based on filter
   * Generic loader that calls appropriate database query
   */
  loadMovies: async (filter: MovieFilter) => {
    set({ loading: true, error: null, filter });

    try {
      let movies: MovieDetails[];

      switch (filter) {
        case 'popular':
          movies = await getPopularMovies();
          break;
        case 'toprated':
          movies = await getTopRatedMovies();
          break;
        case 'favorites':
          movies = await getFavoriteMovies();
          break;
        case 'all':
          movies = await getAllMovies();
          break;
        default:
          movies = [];
      }

      set({ movies, loading: false });
    } catch (error) {
      const errorMessage =
        error instanceof Error ? error.message : 'Failed to load movies';
      set({ error: errorMessage, loading: false, movies: [] });
    }
  },

  /**
   * Load popular movies
   * Replaces: loadPopular() DAO query + LiveData update
   */
  loadPopularMovies: async () => {
    await get().loadMovies('popular');
  },

  /**
   * Load top-rated movies
   * Replaces: loadTopRated() DAO query + LiveData update
   */
  loadTopRatedMovies: async () => {
    await get().loadMovies('toprated');
  },

  /**
   * Load favorite movies
   * Replaces: loadFavorites() DAO query + LiveData update
   */
  loadFavoriteMovies: async () => {
    await get().loadMovies('favorites');
  },

  /**
   * Load all movies
   * Replaces: getAll() DAO query + LiveData update
   */
  loadAllMovies: async () => {
    await get().loadMovies('all');
  },

  /**
   * Toggle favorite status for a movie
   * Implements optimistic update: update UI immediately, rollback on error
   *
   * Replaces: toggle favorite → update database → LiveData notifies UI
   */
  toggleFavorite: async (movieId: number) => {
    const { movies } = get();

    // Find the movie in current state
    const movieIndex = movies.findIndex((m) => m.id === movieId);
    if (movieIndex === -1) {
      set({ error: 'Movie not found in current list' });
      return;
    }

    const movie = movies[movieIndex];
    const newFavoriteStatus = !movie.favorite;

    // Optimistic update: update UI immediately
    const optimisticMovies = movies.map((m) =>
      m.id === movieId ? { ...m, favorite: newFavoriteStatus } : m
    );

    set({ movies: optimisticMovies });

    try {
      // Update database
      await insertMovie({ ...movie, favorite: newFavoriteStatus });

      // If we're viewing favorites and just unfavorited, remove from list
      if (get().filter === 'favorites' && !newFavoriteStatus) {
        set({ movies: movies.filter((m) => m.id !== movieId) });
      }
    } catch (error) {
      // Rollback on error
      set({ movies });
      const errorMessage =
        error instanceof Error ? error.message : 'Failed to toggle favorite';
      set({ error: errorMessage });
    }
  },

  /**
   * Refresh a single movie from database
   * Useful after updating a movie's details
   */
  refreshMovie: async (movieId: number) => {
    try {
      const updatedMovie = await getMovieById(movieId);
      if (!updatedMovie) return;

      const { movies } = get();
      const updatedMovies = movies.map((m) => (m.id === movieId ? updatedMovie : m));

      set({ movies: updatedMovies });
    } catch (error) {
      const errorMessage =
        error instanceof Error ? error.message : 'Failed to refresh movie';
      set({ error: errorMessage });
    }
  },

  /**
   * Clear error message
   */
  clearError: () => {
    set({ error: null });
  },
}));
