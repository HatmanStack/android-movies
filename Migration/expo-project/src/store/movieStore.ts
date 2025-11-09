/**
 * Zustand Movie Store
 * Replaces LiveData functionality from Android app
 *
 * Note: Supports loading from multiple active filters simultaneously,
 * matching Android behavior (MainActivity.java:96-105 combines results).
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
import { MovieFilter } from './filterStore';

/**
 * Movie Store interface
 * Replaces ViewModel + LiveData pattern from Android
 */
interface MovieStore {
  // State
  movies: MovieDetails[];
  loading: boolean;
  error: string | null;

  // Actions
  loadMoviesFromFilters: (filters: MovieFilter[]) => Promise<void>;
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

  /**
   * Load movies from multiple active filters
   * Combines results from all specified filters, matching Android behavior
   *
   * Android equivalent: MainActivity.java:96-105
   * - Checks each filter independently
   * - Combines results with list.addAll()
   *
   * @param filters - Array of active filters to load from
   */
  loadMoviesFromFilters: async (filters: MovieFilter[]) => {
    set({ loading: true, error: null });

    try {
      const combinedMovies: MovieDetails[] = [];
      const movieIds = new Set<number>(); // Track IDs to avoid duplicates

      // Load from each active filter and combine results
      for (const filter of filters) {
        let filterMovies: MovieDetails[] = [];

        switch (filter) {
          case 'popular':
            filterMovies = await getPopularMovies();
            break;
          case 'toprated':
            filterMovies = await getTopRatedMovies();
            break;
          case 'favorites':
            filterMovies = await getFavoriteMovies();
            break;
          case 'all':
            filterMovies = await getAllMovies();
            break;
        }

        // Add movies, avoiding duplicates (a movie can be both popular and top-rated)
        for (const movie of filterMovies) {
          if (!movieIds.has(movie.id)) {
            movieIds.add(movie.id);
            combinedMovies.push(movie);
          }
        }
      }

      set({ movies: combinedMovies, loading: false });
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
    await get().loadMoviesFromFilters(['popular']);
  },

  /**
   * Load top-rated movies
   * Replaces: loadTopRated() DAO query + LiveData update
   */
  loadTopRatedMovies: async () => {
    await get().loadMoviesFromFilters(['toprated']);
  },

  /**
   * Load favorite movies
   * Replaces: loadFavorites() DAO query + LiveData update
   */
  loadFavoriteMovies: async () => {
    await get().loadMoviesFromFilters(['favorites']);
  },

  /**
   * Load all movies
   * Replaces: getAll() DAO query + LiveData update
   */
  loadAllMovies: async () => {
    await get().loadMoviesFromFilters(['all']);
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
    } catch (error) {
      // Rollback on error
      set({ movies });
      const errorMessage = `Failed to toggle favorite: ${error instanceof Error ? error.message : 'Unknown error'}`;
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
      const errorMessage = `Failed to refresh movie: ${error instanceof Error ? error.message : 'Unknown error'}`;
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
