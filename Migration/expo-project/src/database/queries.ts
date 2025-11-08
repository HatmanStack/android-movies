/**
 * Database query functions
 * Migrated from MovieDao.java Room interface
 */

import { getDatabase } from './init';
import { MovieDetails } from '../models/types';

/**
 * Helper function to map database row to MovieDetails object
 * Handles INTEGER to boolean conversion for favorite, toprated, popular
 */
function mapRowToMovie(row: any): MovieDetails {
  return {
    id: row.id,
    title: row.title,
    overview: row.overview,
    poster_path: row.poster_path,
    release_date: row.release_date,
    vote_average: row.vote_average,
    vote_count: row.vote_count,
    popularity: row.popularity,
    original_language: row.original_language,
    favorite: Boolean(row.favorite), // INTEGER → boolean
    toprated: Boolean(row.toprated), // INTEGER → boolean
    popular: Boolean(row.popular), // INTEGER → boolean
  };
}

/**
 * Insert or update a movie
 * Migrates: @Insert(onConflict = OnConflictStrategy.REPLACE) void insertAll(MovieDetails movieDetails)
 *
 * @param movie - Movie to insert/update
 */
export async function insertMovie(movie: MovieDetails): Promise<void> {
  const db = getDatabase();

  try {
    await db.runAsync(
      `INSERT OR REPLACE INTO movie_details
       (id, title, overview, poster_path, release_date, vote_average, vote_count, popularity, original_language, favorite, toprated, popular)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [
        movie.id,
        movie.title,
        movie.overview,
        movie.poster_path,
        movie.release_date,
        movie.vote_average,
        movie.vote_count,
        movie.popularity,
        movie.original_language,
        movie.favorite ? 1 : 0, // boolean → INTEGER
        movie.toprated ? 1 : 0, // boolean → INTEGER
        movie.popular ? 1 : 0, // boolean → INTEGER
      ]
    );
  } catch (error) {
    console.error('Failed to insert movie:', error);
    throw new Error(
      `Failed to insert movie: ${error instanceof Error ? error.message : 'Unknown error'}`
    );
  }
}

/**
 * Get movie by ID
 * Migrates: @Query("SELECT * FROM movie_details WHERE id = :id") MovieDetails loadMovieID(int id)
 *
 * @param movieId - Movie ID to retrieve
 * @returns Movie details or null if not found
 */
export async function getMovieById(movieId: number): Promise<MovieDetails | null> {
  const db = getDatabase();

  try {
    const row = await db.getFirstAsync<any>(
      'SELECT * FROM movie_details WHERE id = ?',
      [movieId]
    );

    return row ? mapRowToMovie(row) : null;
  } catch (error) {
    console.error('Failed to get movie by ID:', error);
    throw new Error(
      `Failed to get movie by ID: ${error instanceof Error ? error.message : 'Unknown error'}`
    );
  }
}

/**
 * Get all favorite movies
 * Migrates: @Query("SELECT * FROM movie_details WHERE favorite = 1") List<MovieDetails> loadFavorites()
 *
 * @returns Array of favorite movies
 */
export async function getFavoriteMovies(): Promise<MovieDetails[]> {
  const db = getDatabase();

  try {
    const rows = await db.getAllAsync<any>('SELECT * FROM movie_details WHERE favorite = 1');

    return rows.map(mapRowToMovie);
  } catch (error) {
    console.error('Failed to get favorite movies:', error);
    throw new Error(
      `Failed to get favorite movies: ${error instanceof Error ? error.message : 'Unknown error'}`
    );
  }
}

/**
 * Get all popular movies
 * Migrates: @Query("SELECT * FROM movie_details WHERE popular = 1") List<MovieDetails> loadPopular()
 *
 * @returns Array of popular movies
 */
export async function getPopularMovies(): Promise<MovieDetails[]> {
  const db = getDatabase();

  try {
    const rows = await db.getAllAsync<any>('SELECT * FROM movie_details WHERE popular = 1');

    return rows.map(mapRowToMovie);
  } catch (error) {
    console.error('Failed to get popular movies:', error);
    throw new Error(
      `Failed to get popular movies: ${error instanceof Error ? error.message : 'Unknown error'}`
    );
  }
}

/**
 * Get all top-rated movies
 * Migrates: @Query("SELECT * FROM movie_details WHERE toprated = 1") List<MovieDetails> loadTopRated()
 *
 * @returns Array of top-rated movies
 */
export async function getTopRatedMovies(): Promise<MovieDetails[]> {
  const db = getDatabase();

  try {
    const rows = await db.getAllAsync<any>('SELECT * FROM movie_details WHERE toprated = 1');

    return rows.map(mapRowToMovie);
  } catch (error) {
    console.error('Failed to get top-rated movies:', error);
    throw new Error(
      `Failed to get top-rated movies: ${error instanceof Error ? error.message : 'Unknown error'}`
    );
  }
}

/**
 * Get all movies
 * Migrates: @Query("SELECT * FROM movie_details") List<MovieDetails> getAll()
 *
 * @returns Array of all movies
 */
export async function getAllMovies(): Promise<MovieDetails[]> {
  const db = getDatabase();

  try {
    const rows = await db.getAllAsync<any>('SELECT * FROM movie_details');

    return rows.map(mapRowToMovie);
  } catch (error) {
    console.error('Failed to get all movies:', error);
    throw new Error(
      `Failed to get all movies: ${error instanceof Error ? error.message : 'Unknown error'}`
    );
  }
}

/**
 * Delete a movie by ID
 * Migrates: @Delete void delete(MovieDetails movieDetails)
 *
 * @param movieId - Movie ID to delete
 */
export async function deleteMovie(movieId: number): Promise<void> {
  const db = getDatabase();

  try {
    await db.runAsync('DELETE FROM movie_details WHERE id = ?', [movieId]);
  } catch (error) {
    console.error('Failed to delete movie:', error);
    throw new Error(
      `Failed to delete movie: ${error instanceof Error ? error.message : 'Unknown error'}`
    );
  }
}
