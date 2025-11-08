/**
 * Zustand Movie Store tests
 * Tests all state management actions and error handling
 */

import { useMovieStore } from '../../src/store/movieStore';
import { MovieDetails } from '../../src/models/types';
import * as queries from '../../src/database/queries';

// Mock database queries
jest.mock('../../src/database/queries');

const mockQueries = queries as jest.Mocked<typeof queries>;

// Test data
const mockMovies: MovieDetails[] = [
  {
    id: 1,
    title: 'Test Movie 1',
    overview: 'Test description 1',
    poster_path: '/test1.jpg',
    release_date: '2024-01-01',
    vote_average: 8,
    vote_count: 100,
    popularity: 50.5,
    original_language: 'en',
    favorite: true,
    toprated: false,
    popular: true,
  },
  {
    id: 2,
    title: 'Test Movie 2',
    overview: 'Test description 2',
    poster_path: '/test2.jpg',
    release_date: '2024-01-02',
    vote_average: 7,
    vote_count: 80,
    popularity: 40.5,
    original_language: 'en',
    favorite: false,
    toprated: true,
    popular: false,
  },
];

describe('MovieStore', () => {
  beforeEach(() => {
    // Reset store to initial state
    useMovieStore.setState({
      movies: [],
      loading: false,
      error: null,
      filter: 'popular',
    });

    // Clear all mock calls
    jest.clearAllMocks();
  });

  describe('Initial State', () => {
    it('should have correct initial state', () => {
      const state = useMovieStore.getState();

      expect(state.movies).toEqual([]);
      expect(state.loading).toBe(false);
      expect(state.error).toBeNull();
      expect(state.filter).toBe('popular');
    });
  });

  describe('loadPopularMovies', () => {
    it('should load popular movies successfully', async () => {
      const popularMovies = [mockMovies[0]]; // Only popular movies
      mockQueries.getPopularMovies.mockResolvedValue(popularMovies);

      const store = useMovieStore.getState();
      await store.loadPopularMovies();

      const state = useMovieStore.getState();
      expect(state.movies).toEqual(popularMovies);
      expect(state.loading).toBe(false);
      expect(state.error).toBeNull();
      expect(state.filter).toBe('popular');
      expect(mockQueries.getPopularMovies).toHaveBeenCalledTimes(1);
    });

    it('should set loading state while loading', async () => {
      mockQueries.getPopularMovies.mockImplementation(
        () => new Promise((resolve) => setTimeout(() => resolve([]), 100))
      );

      const store = useMovieStore.getState();
      const loadPromise = store.loadPopularMovies();

      // Check loading state is true while async operation is in progress
      expect(useMovieStore.getState().loading).toBe(true);

      await loadPromise;

      // Check loading state is false after completion
      expect(useMovieStore.getState().loading).toBe(false);
    });

    it('should handle errors gracefully', async () => {
      const errorMessage = 'Database connection failed';
      mockQueries.getPopularMovies.mockRejectedValue(new Error(errorMessage));

      const store = useMovieStore.getState();
      await store.loadPopularMovies();

      const state = useMovieStore.getState();
      expect(state.error).toBe(errorMessage);
      expect(state.loading).toBe(false);
      expect(state.movies).toEqual([]);
    });
  });

  describe('loadTopRatedMovies', () => {
    it('should load top-rated movies successfully', async () => {
      const topRatedMovies = [mockMovies[1]]; // Only top-rated movies
      mockQueries.getTopRatedMovies.mockResolvedValue(topRatedMovies);

      const store = useMovieStore.getState();
      await store.loadTopRatedMovies();

      const state = useMovieStore.getState();
      expect(state.movies).toEqual(topRatedMovies);
      expect(state.filter).toBe('toprated');
      expect(mockQueries.getTopRatedMovies).toHaveBeenCalledTimes(1);
    });

    it('should handle errors gracefully', async () => {
      mockQueries.getTopRatedMovies.mockRejectedValue(new Error('Failed to load'));

      const store = useMovieStore.getState();
      await store.loadTopRatedMovies();

      const state = useMovieStore.getState();
      expect(state.error).not.toBeNull();
      expect(state.movies).toEqual([]);
    });
  });

  describe('loadFavoriteMovies', () => {
    it('should load favorite movies successfully', async () => {
      const favoriteMovies = [mockMovies[0]]; // Only favorites
      mockQueries.getFavoriteMovies.mockResolvedValue(favoriteMovies);

      const store = useMovieStore.getState();
      await store.loadFavoriteMovies();

      const state = useMovieStore.getState();
      expect(state.movies).toEqual(favoriteMovies);
      expect(state.filter).toBe('favorites');
      expect(mockQueries.getFavoriteMovies).toHaveBeenCalledTimes(1);
    });
  });

  describe('loadAllMovies', () => {
    it('should load all movies successfully', async () => {
      mockQueries.getAllMovies.mockResolvedValue(mockMovies);

      const store = useMovieStore.getState();
      await store.loadAllMovies();

      const state = useMovieStore.getState();
      expect(state.movies).toEqual(mockMovies);
      expect(state.filter).toBe('all');
      expect(mockQueries.getAllMovies).toHaveBeenCalledTimes(1);
    });
  });

  describe('toggleFavorite', () => {
    beforeEach(() => {
      // Set up initial state with movies
      useMovieStore.setState({ movies: mockMovies, filter: 'popular' });
      mockQueries.insertMovie.mockResolvedValue();
    });

    it('should toggle favorite status optimistically', async () => {
      const store = useMovieStore.getState();
      await store.toggleFavorite(1);

      const state = useMovieStore.getState();
      const movie = state.movies.find((m) => m.id === 1);

      // Movie 1 was favorite=true, should now be false
      expect(movie?.favorite).toBe(false);
      expect(mockQueries.insertMovie).toHaveBeenCalledWith(
        expect.objectContaining({
          id: 1,
          favorite: false,
        })
      );
    });

    it('should update database with new favorite status', async () => {
      const store = useMovieStore.getState();
      await store.toggleFavorite(2);

      expect(mockQueries.insertMovie).toHaveBeenCalledWith(
        expect.objectContaining({
          id: 2,
          favorite: true, // Was false, toggled to true
        })
      );
    });

    it('should handle movie not found in list', async () => {
      const store = useMovieStore.getState();
      await store.toggleFavorite(999);

      const state = useMovieStore.getState();
      expect(state.error).toBe('Movie not found in current list');
      expect(mockQueries.insertMovie).not.toHaveBeenCalled();
    });

    it('should rollback on database error', async () => {
      mockQueries.insertMovie.mockRejectedValue(new Error('Database error'));

      const originalMovies = [...mockMovies];
      const store = useMovieStore.getState();
      await store.toggleFavorite(1);

      const state = useMovieStore.getState();
      // Should rollback to original state
      expect(state.movies).toEqual(originalMovies);
      expect(state.error).toContain('Failed to toggle favorite');
    });

    it('should remove movie from list when unfavoriting in favorites view', async () => {
      // Set filter to favorites
      useMovieStore.setState({ movies: [mockMovies[0]], filter: 'favorites' });

      const store = useMovieStore.getState();
      await store.toggleFavorite(1); // Unfavorite movie 1

      const state = useMovieStore.getState();
      expect(state.movies).toHaveLength(0); // Removed from list
    });
  });

  describe('refreshMovie', () => {
    beforeEach(() => {
      useMovieStore.setState({ movies: mockMovies });
    });

    it('should refresh a single movie from database', async () => {
      const updatedMovie = { ...mockMovies[0], title: 'Updated Title' };
      mockQueries.getMovieById.mockResolvedValue(updatedMovie);

      const store = useMovieStore.getState();
      await store.refreshMovie(1);

      const state = useMovieStore.getState();
      const movie = state.movies.find((m) => m.id === 1);

      expect(movie?.title).toBe('Updated Title');
      expect(mockQueries.getMovieById).toHaveBeenCalledWith(1);
    });

    it('should handle movie not found in database', async () => {
      mockQueries.getMovieById.mockResolvedValue(null);

      const store = useMovieStore.getState();
      await store.refreshMovie(999);

      // Should not error, just skip update
      const state = useMovieStore.getState();
      expect(state.movies).toEqual(mockMovies); // Unchanged
    });

    it('should handle refresh errors', async () => {
      mockQueries.getMovieById.mockRejectedValue(new Error('Database error'));

      const store = useMovieStore.getState();
      await store.refreshMovie(1);

      const state = useMovieStore.getState();
      expect(state.error).toContain('Failed to refresh movie');
    });
  });

  describe('clearError', () => {
    it('should clear error message', () => {
      useMovieStore.setState({ error: 'Test error' });

      const store = useMovieStore.getState();
      store.clearError();

      const state = useMovieStore.getState();
      expect(state.error).toBeNull();
    });
  });
});
