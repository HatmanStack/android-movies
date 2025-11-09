/**
 * Database initialization and connection management
 * Implements singleton pattern for SQLite database access
 */

import * as SQLite from 'expo-sqlite';
import { SCHEMA_STATEMENTS, CURRENT_DB_VERSION } from './schema';

/**
 * Singleton database instance
 */
let database: SQLite.SQLiteDatabase | null = null;

/**
 * Database name
 */
const DB_NAME = 'movies.db';

/**
 * Handle database schema migrations
 *
 * @param db - Database instance
 * @param fromVersion - Current version
 * @param toVersion - Target version
 */
async function migrateDatabase(
  db: SQLite.SQLiteDatabase,
  fromVersion: number,
  toVersion: number
): Promise<void> {
  console.log(`Migrating database from v${fromVersion} to v${toVersion}`);

  // Migration from v1 to v2: Fix column types
  if (fromVersion === 1 && toVersion >= 2) {
    try {
      // SQLite doesn't support ALTER COLUMN, so we recreate tables
      // Step 1: Rename old tables
      await db.execAsync('ALTER TABLE movie_details RENAME TO movie_details_old');
      await db.execAsync('ALTER TABLE video_details RENAME TO video_details_old');

      // Step 2: Create new tables with correct schema
      await db.execAsync(`
        CREATE TABLE movie_details (
          id INTEGER PRIMARY KEY,
          title TEXT,
          overview TEXT,
          poster_path TEXT,
          release_date TEXT,
          vote_average REAL,
          vote_count INTEGER,
          popularity REAL,
          original_language TEXT,
          favorite INTEGER DEFAULT 0,
          toprated INTEGER DEFAULT 0,
          popular INTEGER DEFAULT 0
        )
      `);

      await db.execAsync(`
        CREATE TABLE video_details (
          identity INTEGER PRIMARY KEY AUTOINCREMENT,
          id INTEGER,
          image_url TEXT,
          iso_639_1 TEXT,
          iso_3166_1 TEXT,
          key TEXT,
          site TEXT,
          size INTEGER,
          type TEXT
        )
      `);

      // Step 3: Copy data (CAST size to INTEGER for video_details)
      await db.execAsync(`
        INSERT INTO movie_details
        SELECT id, title, overview, poster_path, release_date,
               CAST(vote_average AS REAL), vote_count, popularity,
               original_language, favorite, toprated, popular
        FROM movie_details_old
      `);

      await db.execAsync(`
        INSERT INTO video_details
        SELECT identity, id, image_url, iso_639_1, iso_3166_1,
               key, site, CAST(size AS INTEGER), type
        FROM video_details_old
      `);

      // Step 4: Drop old tables
      await db.execAsync('DROP TABLE movie_details_old');
      await db.execAsync('DROP TABLE video_details_old');

      // Step 5: Recreate indexes
      await db.execAsync('CREATE INDEX IF NOT EXISTS idx_movie_favorite ON movie_details(favorite)');
      await db.execAsync('CREATE INDEX IF NOT EXISTS idx_movie_popular ON movie_details(popular)');
      await db.execAsync('CREATE INDEX IF NOT EXISTS idx_movie_toprated ON movie_details(toprated)');
      await db.execAsync('CREATE INDEX IF NOT EXISTS idx_video_movie_id ON video_details(id)');
      await db.execAsync('CREATE INDEX IF NOT EXISTS idx_video_trailer ON video_details(id, type)');

      console.log('Migration v1 -> v2 completed successfully');
    } catch (error) {
      console.error('Migration v1 -> v2 failed:', error);
      throw error;
    }
  }
}

/**
 * Initialize the database
 * - Creates tables if they don't exist
 * - Sets up database version tracking
 * - Idempotent (safe to call multiple times)
 *
 * @returns Promise<void>
 */
export async function initDatabase(): Promise<void> {
  try {
    const db = getDatabase();

    // Execute all schema creation statements
    for (const statement of SCHEMA_STATEMENTS) {
      await db.execAsync(statement);
    }

    // Set or verify database version
    const versionRow = await db.getFirstAsync<{ version: number }>(
      'SELECT version FROM database_version LIMIT 1'
    );

    if (!versionRow) {
      // First time initialization
      await db.runAsync('INSERT INTO database_version (version) VALUES (?)', [
        CURRENT_DB_VERSION,
      ]);
    } else if (versionRow.version < CURRENT_DB_VERSION) {
      // Handle migrations
      await migrateDatabase(db, versionRow.version, CURRENT_DB_VERSION);

      // Update version
      await db.runAsync('UPDATE database_version SET version = ?', [CURRENT_DB_VERSION]);
    }

    console.log('Database initialized successfully');
  } catch (error) {
    console.error('Failed to initialize database:', error);
    throw new Error(
      `Database initialization failed: ${error instanceof Error ? error.message : 'Unknown error'}`
    );
  }
}

/**
 * Get database instance (singleton pattern)
 * Opens database connection if not already open
 *
 * @returns SQLiteDatabase instance
 */
export function getDatabase(): SQLite.SQLiteDatabase {
  if (!database) {
    try {
      database = SQLite.openDatabaseSync(DB_NAME);
    } catch (error) {
      console.error('Failed to open database:', error);
      throw new Error(
        `Failed to open database: ${error instanceof Error ? error.message : 'Unknown error'}`
      );
    }
  }
  return database;
}

/**
 * Close database connection (for cleanup/testing)
 */
export function closeDatabase(): void {
  if (database) {
    try {
      database.closeSync();
      database = null;
    } catch (error) {
      console.error('Failed to close database:', error);
    }
  }
}

/**
 * Reset database (for testing only)
 * Drops all tables and reinitializes
 */
export async function resetDatabase(): Promise<void> {
  try {
    const db = getDatabase();

    // Drop all tables
    await db.execAsync('DROP TABLE IF EXISTS movie_details');
    await db.execAsync('DROP TABLE IF EXISTS video_details');
    await db.execAsync('DROP TABLE IF EXISTS review_details');
    await db.execAsync('DROP TABLE IF EXISTS database_version');

    // Reinitialize
    await initDatabase();
  } catch (error) {
    console.error('Failed to reset database:', error);
    throw error;
  }
}
