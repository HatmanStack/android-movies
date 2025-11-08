# Phase 2: Database & State Management

**Goal:** Implement the local SQLite database using expo-sqlite (Room migration) and create Zustand stores for reactive state management (LiveData replacement).

**Success Criteria:**
- ✅ SQLite database schema matches Room entities exactly
- ✅ All DAO query methods migrated to TypeScript functions
- ✅ Zustand stores created for movies, videos, and reviews
- ✅ Database CRUD operations working correctly
- ✅ State management integrated with database queries
- ✅ Comprehensive tests for database queries and stores

**Estimated Tokens:** ~30,000

---

## Prerequisites

Before starting this phase:

### Previous Phases
- [ ] Phase 1 completed and verified
- [ ] API services working and tested
- [ ] TypeScript interfaces defined

### Knowledge
- [ ] Read Phase-0.md ADR-001 (Database Strategy)
- [ ] Read Phase-0.md ADR-002 (State Management)
- [ ] Understand SQLite basics
- [ ] Familiar with Zustand API
- [ ] Reviewed `MovieDao.java` from Android app

### Android Source Reference
- [ ] Have access to `app/src/main/java/gemenielabs/movies/Database/` files
- [ ] Understand Room entity structure
- [ ] Understand DAO query patterns

---

## Tasks

### Task 1: Initialize Database Schema

**Goal:** Create the SQLite database with tables matching the Room entities.

**Files to Create:**
- `Migration/expo-project/src/database/init.ts` - Database initialization
- `Migration/expo-project/src/database/schema.ts` - SQL schema definitions

**Prerequisites:**
- Task completed: Phase 1, Task 2 (expo-sqlite installed)
- Read `MovieDetails.java`, `VideoDetails.java`, `ReviewDetails.java`

**Implementation Steps:**

1. Create `src/database/schema.ts` with SQL table definitions:
   - Translate Room `@Entity` annotations to `CREATE TABLE` statements
   - Maintain exact column names and types from Room entities
   - Follow this mapping for data types:
     - Java `int` → SQLite `INTEGER`
     - Java `float` → SQLite `REAL`
     - Java `boolean` → SQLite `INTEGER` (0 = false, 1 = true)
     - Java `String` → SQLite `TEXT`

2. Define `movie_details` table schema:
   - Primary key: `id INTEGER PRIMARY KEY`
   - Boolean columns: `favorite INTEGER DEFAULT 0`, `toprated INTEGER DEFAULT 0`, `popular INTEGER DEFAULT 0`
   - Numeric columns: `vote_count INTEGER`, `popularity REAL`, `vote_average INTEGER`
   - Text columns: `title TEXT`, `overview TEXT`, `release_date TEXT`, `poster_path TEXT`, `original_language TEXT`
   - Match Room entity field names exactly

3. Define `video_details` table schema:
   - Primary key: `identity INTEGER PRIMARY KEY AUTOINCREMENT`
   - Foreign key: `id INTEGER` (references movie)
   - Text columns: `iso_639_1 TEXT`, `iso_3166_1 TEXT`, `key TEXT`, `site TEXT`, `size TEXT`, `type TEXT`, `image_url TEXT`
   - No formal foreign key constraint (match Room behavior)

4. Define `review_details` table schema:
   - Primary key: `identity INTEGER PRIMARY KEY AUTOINCREMENT`
   - Foreign key: `id INTEGER` (references movie)
   - Text columns: `author TEXT`, `content TEXT`

5. Create `src/database/init.ts` for database initialization:
   - Import `expo-sqlite` and open database: `SQLite.openDatabaseSync('movies.db')`
   - Implement `initDatabase()` function that creates tables if they don't exist
   - Use `CREATE TABLE IF NOT EXISTS` to be idempotent
   - Execute all three table creation statements
   - Add error handling for database failures

6. Add database version tracking (for future migrations):
   - Create `database_version` table to track schema version
   - Current version: 1 (matches Android Room version)
   - Prepare for future schema changes

7. Export database instance for use throughout the app:
   - Create singleton pattern to ensure one database connection
   - Export `getDatabase()` function that returns initialized database

**Verification Checklist:**
- [ ] `src/database/schema.ts` defines all three table schemas
- [ ] `src/database/init.ts` implements database initialization
- [ ] Column names match Room entities exactly
- [ ] Data types correctly mapped from Java to SQLite
- [ ] Database creation is idempotent (can run multiple times safely)
- [ ] TypeScript compilation passes

**Testing Instructions:**

```typescript
import { initDatabase, getDatabase } from './src/database/init';

// Initialize database
await initDatabase();

// Verify tables exist
const db = getDatabase();
const tables = await db.getAllAsync(`
  SELECT name FROM sqlite_master WHERE type='table'
`);
console.log(tables); // Should show movie_details, video_details, review_details

// Verify movie_details schema
const schema = await db.getAllAsync(`PRAGMA table_info(movie_details)`);
console.log(schema); // Should show all columns with correct types
```

**Commit Message Template:**
```
feat(database): initialize SQLite schema from Room entities

- Create movie_details table (migrated from MovieDetails.java)
- Create video_details table (migrated from VideoDetails.java)
- Create review_details table (migrated from ReviewDetails.java)
- Implement database initialization with singleton pattern
- Add database version tracking
```

**Estimated Tokens:** ~4,000

---

### Task 2: Implement DAO Query Functions (Part 1: MovieDetails)

**Goal:** Migrate all MovieDao query methods for MovieDetails to TypeScript functions.

**Files to Create:**
- `Migration/expo-project/src/database/queries.ts` - Query functions

**Prerequisites:**
- Task 1 completed (database schema created)
- Read `MovieDao.java` interface
- Understand prepared statements for SQL injection prevention

**Implementation Steps:**

1. Create `src/database/queries.ts` and import database:
   - Import `getDatabase()` from init.ts
   - Define TypeScript return types using interfaces from `src/models/types.ts`

2. Implement `insertMovie()` function:
   - Migrates: `@Insert void insertAll(MovieDetails movieDetails)`
   - Conflict strategy: REPLACE (UPDATE if exists, INSERT if not)
   - Parameters: `MovieDetails` object
   - Use prepared statement: `INSERT OR REPLACE INTO movie_details (...) VALUES (?, ?, ...)`
   - Map TypeScript boolean → SQLite INTEGER (true = 1, false = 0)
   - Return type: `Promise<void>`

3. Implement `getMovieById()` function:
   - Migrates: `@Query("SELECT * FROM movie_details WHERE id = :id")`
   - Parameter: `movieId: number`
   - Use prepared statement with `?` placeholder
   - Map SQLite INTEGER → TypeScript boolean (1 = true, 0 = false)
   - Return type: `Promise<MovieDetails | null>`

4. Implement `getFavoriteMovies()` function:
   - Migrates: `@Query("SELECT * FROM movie_details WHERE favorite = 1")`
   - No parameters
   - Return type: `Promise<MovieDetails[]>`
   - Map INTEGER to boolean in results

5. Implement `getPopularMovies()` function:
   - Migrates: `@Query("SELECT * FROM movie_details WHERE popular = 1")`
   - No parameters
   - Return type: `Promise<MovieDetails[]>`

6. Implement `getTopRatedMovies()` function:
   - Migrates: `@Query("SELECT * FROM movie_details WHERE toprated = 1")`
   - No parameters
   - Return type: `Promise<MovieDetails[]>`

7. Implement `getAllMovies()` function:
   - Migrates: `@Query("SELECT * FROM movie_details")`
   - No parameters
   - Return type: `Promise<MovieDetails[]>`

8. Implement `deleteMovie()` function:
   - Migrates: `@Delete void delete(MovieDetails movieDetails)`
   - Parameter: `movieId: number` (simpler than passing full object)
   - Use prepared statement: `DELETE FROM movie_details WHERE id = ?`
   - Return type: `Promise<void>`

9. Create helper function `mapRowToMovie()`:
   - Converts SQLite row to MovieDetails TypeScript object
   - Maps INTEGER to boolean for favorite, toprated, popular
   - Handles null values gracefully
   - Reusable across all movie query functions

**Verification Checklist:**
- [ ] All MovieDao query methods migrated
- [ ] All queries use prepared statements (no SQL injection)
- [ ] Boolean mapping (INTEGER ↔ boolean) handled correctly
- [ ] Return types match LiveData data types from Android
- [ ] Error handling for database failures
- [ ] TypeScript strict mode passes

**Testing Instructions:**

```typescript
import {
  insertMovie,
  getMovieById,
  getFavoriteMovies,
  getPopularMovies
} from './src/database/queries';

// Test insert
const testMovie: MovieDetails = {
  id: 1,
  title: 'Test Movie',
  overview: 'Test description',
  poster_path: '/test.jpg',
  release_date: '2024-01-01',
  vote_average: 8,
  vote_count: 100,
  popularity: 50.5,
  original_language: 'en',
  favorite: true,
  toprated: false,
  popular: true
};

await insertMovie(testMovie);

// Test get by ID
const movie = await getMovieById(1);
console.log(movie?.title); // "Test Movie"
console.log(movie?.favorite); // true (should be boolean, not 1)

// Test get favorites
const favorites = await getFavoriteMovies();
console.log(favorites.length); // Should be 1
console.log(favorites[0].favorite); // true

// Test get popular
const popular = await getPopularMovies();
console.log(popular.length); // Should be 1
```

**Commit Message Template:**
```
feat(database): implement movie query functions from MovieDao

- Add insertMovie with REPLACE conflict strategy
- Add getMovieById, getFavoriteMovies, getPopularMovies
- Add getTopRatedMovies, getAllMovies, deleteMovie
- Implement boolean to INTEGER mapping
- Use prepared statements for SQL injection prevention
```

**Estimated Tokens:** ~6,000

---

### Task 3: Implement DAO Query Functions (Part 2: VideoDetails & ReviewDetails)

**Goal:** Migrate remaining MovieDao query methods for videos and reviews.

**Files to Modify:**
- `Migration/expo-project/src/database/queries.ts` - Add video and review queries

**Prerequisites:**
- Task 2 completed (movie queries implemented)
- Read `MovieDao.java` video and review methods

**Implementation Steps:**

1. Implement `insertVideo()` function:
   - Migrates: `@Insert void insertVideoDetails(VideoDetails videoDetails)`
   - Conflict strategy: REPLACE
   - Parameter: `VideoDetails` object (without identity, auto-generated)
   - Return type: `Promise<void>`

2. Implement `getVideosForMovie()` function:
   - Migrates: `@Query("SELECT * FROM video_details WHERE id = :id")`
   - Parameter: `movieId: number`
   - Return type: `Promise<VideoDetails[]>`
   - Returns all videos for a specific movie

3. Implement `getTrailersForMovie()` function:
   - Migrates: `@Query("SELECT * FROM video_details WHERE type = 'Trailer' AND id = :id")`
   - Parameter: `movieId: number`
   - Return type: `Promise<VideoDetails[]>`
   - Filters for type = 'Trailer' only

4. Implement `insertReview()` function:
   - Migrates: `@Insert void insertReviewDetails(ReviewDetails reviewDetails)`
   - Conflict strategy: REPLACE
   - Parameter: `ReviewDetails` object (without identity, auto-generated)
   - Return type: `Promise<void>`

5. Implement `getReviewsForMovie()` function:
   - Migrates: `@Query("SELECT * FROM review_details WHERE id = :id")`
   - Parameter: `movieId: number`
   - Return type: `Promise<VideoDetails[]>`
   - Returns all reviews for a specific movie

6. Create helper functions for mapping:
   - `mapRowToVideo(row: any): VideoDetails` - Converts SQLite row to VideoDetails
   - `mapRowToReview(row: any): ReviewDetails` - Converts SQLite row to ReviewDetails
   - Handle null values and type conversions

**Verification Checklist:**
- [ ] All video-related DAO methods migrated
- [ ] All review-related DAO methods migrated
- [ ] Prepared statements used for all queries
- [ ] Helper mapping functions created
- [ ] TypeScript types enforced
- [ ] No SQL injection vulnerabilities

**Testing Instructions:**

```typescript
import {
  insertVideo,
  getVideosForMovie,
  getTrailersForMovie,
  insertReview,
  getReviewsForMovie
} from './src/database/queries';

// Test video insert
const testVideo: Omit<VideoDetails, 'identity'> = {
  id: 1, // Movie ID
  iso_639_1: 'en',
  iso_3166_1: 'US',
  key: 'dQw4w9WgXcQ',
  site: 'YouTube',
  size: '1080',
  type: 'Trailer',
  image_url: 'https://example.com/thumbnail.jpg'
};

await insertVideo(testVideo);

// Test get videos
const videos = await getVideosForMovie(1);
console.log(videos.length); // Should be 1

// Test get trailers only
const trailers = await getTrailersForMovie(1);
console.log(trailers[0].type); // "Trailer"

// Test review insert
const testReview: Omit<ReviewDetails, 'identity'> = {
  id: 1, // Movie ID
  author: 'John Doe',
  content: 'Great movie!'
};

await insertReview(testReview);

// Test get reviews
const reviews = await getReviewsForMovie(1);
console.log(reviews[0].author); // "John Doe"
```

**Commit Message Template:**
```
feat(database): implement video and review query functions

- Add insertVideo and getVideosForMovie
- Add getTrailersForMovie with type filtering
- Add insertReview and getReviewsForMovie
- Create helper mapping functions for videos and reviews
```

**Estimated Tokens:** ~4,000

---

### Task 4: Create Zustand Movie Store (LiveData Replacement)

**Goal:** Create a Zustand store for movie state management, replacing LiveData functionality.

**Files to Create:**
- `Migration/expo-project/src/store/movieStore.ts` - Movie state store

**Prerequisites:**
- Task 3 completed (all database queries implemented)
- Read Phase-0.md ADR-002 (State Management)
- Understand Zustand API: `create`, `set`, `get`

**Implementation Steps:**

1. Create `src/store/movieStore.ts` and define store interface:
   - Define `MovieStore` interface with TypeScript
   - State: `movies: MovieDetails[]`, `loading: boolean`, `error: string | null`
   - Actions: Functions to load and manipulate movies

2. Implement core state properties:
   - `movies: MovieDetails[]` - Current list of movies (replaces LiveData<List<MovieDetails>>)
   - `loading: boolean` - Loading state for async operations
   - `error: string | null` - Error message if operation fails
   - `filter: 'popular' | 'toprated' | 'favorites' | 'all'` - Current filter

3. Implement `loadMovies()` action:
   - Loads movies from database based on current filter
   - Sets loading state before query
   - Handles errors and sets error state
   - Updates movies array on success
   - Async function returning Promise<void>

4. Implement `loadPopularMovies()` action:
   - Calls `getPopularMovies()` from database queries
   - Updates state with results
   - Sets filter to 'popular'
   - Replaces Android's `loadPopular()` DAO query + LiveData update

5. Implement `loadTopRatedMovies()` action:
   - Calls `getTopRatedMovies()` from database queries
   - Updates state with results
   - Sets filter to 'toprated'

6. Implement `loadFavoriteMovies()` action:
   - Calls `getFavoriteMovies()` from database queries
   - Updates state with results
   - Sets filter to 'favorites'

7. Implement `toggleFavorite()` action:
   - Parameter: `movieId: number`
   - Gets current movie from store
   - Toggles favorite boolean
   - Updates database with `insertMovie()` (REPLACE strategy)
   - Updates store state immediately for optimistic UI
   - Rollback on database error

8. Implement `syncWithAPI()` action (optional but recommended):
   - Fetches fresh data from TMDb API
   - Saves to database
   - Refreshes store state
   - Combines API and database layers

9. Create Zustand store with `create<MovieStore>()`:
   - Use TypeScript generic for type safety
   - Implement all actions with `set` and `get` functions
   - Follow immutable update patterns

**Verification Checklist:**
- [ ] `MovieStore` interface fully typed
- [ ] All state properties defined (movies, loading, error, filter)
- [ ] All actions implemented (load, toggle, etc.)
- [ ] Immutable updates (no state mutation)
- [ ] Error handling in all async actions
- [ ] Loading states managed correctly
- [ ] TypeScript strict mode passes

**Testing Instructions:**

```typescript
import { useMovieStore } from './src/store/movieStore';

// Get store instance (outside React component for testing)
const store = useMovieStore.getState();

// Test loading popular movies
await store.loadPopularMovies();
console.log(store.movies.length); // Should show popular movies

// Test loading favorites
await store.loadFavoriteMovies();
console.log(store.movies.length); // Should show favorites

// Test toggle favorite
const movieId = store.movies[0].id;
await store.toggleFavorite(movieId);
// Verify favorite toggled in database and store

// Test error handling
// (manually cause database error)
// Should set error state without crashing
```

**Commit Message Template:**
```
feat(store): create Zustand movie store (LiveData replacement)

- Add MovieStore with movies, loading, error state
- Implement loadPopularMovies, loadTopRatedMovies, loadFavoriteMovies
- Implement toggleFavorite with optimistic updates
- Add error handling for all async operations
- Use immutable state updates
```

**Estimated Tokens:** ~5,000

---

### Task 5: Create Zustand Filter Store

**Goal:** Create a Zustand store for filter preferences, replacing SharedPreferences.

**Files to Create:**
- `Migration/expo-project/src/store/filterStore.ts` - Filter preferences store

**Prerequisites:**
- Task 4 completed (movie store created)
- Understand Android SharedPreferences usage in `SearchFragment.java`

**Implementation Steps:**

1. Create `src/store/filterStore.ts` and define interface:
   - Define `FilterStore` interface
   - State: `showPopular: boolean`, `showTopRated: boolean`, `showFavorites: boolean`
   - Actions: Toggle functions for each filter

2. Implement filter state properties:
   - `showPopular: boolean` - Default true
   - `showTopRated: boolean` - Default true
   - `showFavorites: boolean` - Default false
   - Matches Android SharedPreferences keys

3. Implement `togglePopular()` action:
   - Toggles `showPopular` boolean
   - Triggers movie store to reload based on active filters
   - Replaces SharedPreferences.edit().putBoolean()

4. Implement `toggleTopRated()` action:
   - Toggles `showTopRated` boolean
   - Triggers movie store reload

5. Implement `toggleFavorites()` action:
   - Toggles `showFavorites` boolean
   - Triggers movie store reload

6. Implement `getActiveFilter()` selector:
   - Returns 'popular' | 'toprated' | 'favorites' | 'all'
   - Based on which toggle is active
   - Helper for determining what to load from database

7. Add persistence (bonus):
   - Use `AsyncStorage` to persist filter preferences
   - Load preferences on app start
   - Save preferences on toggle
   - Replaces SharedPreferences persistence

**Verification Checklist:**
- [ ] FilterStore interface fully typed
- [ ] All filter state properties defined
- [ ] Toggle actions implemented
- [ ] Integration with movie store for reloading
- [ ] (Optional) Persistence with AsyncStorage
- [ ] TypeScript compilation passes

**Testing Instructions:**

```typescript
import { useFilterStore } from './src/store/filterStore';

const store = useFilterStore.getState();

// Test initial state
console.log(store.showPopular); // true
console.log(store.showTopRated); // true
console.log(store.showFavorites); // false

// Test toggle
store.toggleFavorites();
console.log(store.showFavorites); // true

// Test active filter
const activeFilter = store.getActiveFilter();
console.log(activeFilter); // Should reflect current toggle state
```

**Commit Message Template:**
```
feat(store): create filter preferences store

- Add FilterStore with showPopular, showTopRated, showFavorites
- Implement toggle actions for each filter
- Add getActiveFilter selector
- Integrate with movie store for automatic reloading
- (Optional) Add AsyncStorage persistence
```

**Estimated Tokens:** ~3,000

---

### Task 6: Write Database Query Tests

**Goal:** Create comprehensive unit tests for all database query functions.

**Files to Create:**
- `Migration/expo-project/__tests__/database/queries.test.ts`

**Prerequisites:**
- Task 3 completed (all queries implemented)
- Jest configured for testing

**Implementation Steps:**

1. Set up test database:
   - Create in-memory SQLite database for testing
   - Initialize schema before each test
   - Clear data after each test (isolation)

2. Test movie query functions:
   - Test `insertMovie()` - verify insertion
   - Test `getMovieById()` - verify retrieval
   - Test `getFavoriteMovies()` - verify filtering
   - Test `getPopularMovies()` - verify filtering
   - Test `getTopRatedMovies()` - verify filtering
   - Test `deleteMovie()` - verify deletion
   - Test boolean mapping (INTEGER ↔ boolean)

3. Test video query functions:
   - Test `insertVideo()` - verify auto-increment identity
   - Test `getVideosForMovie()` - verify foreign key filtering
   - Test `getTrailersForMovie()` - verify type filtering

4. Test review query functions:
   - Test `insertReview()` - verify insertion
   - Test `getReviewsForMovie()` - verify foreign key filtering

5. Test edge cases:
   - Query non-existent movie (should return null)
   - Insert duplicate ID (should REPLACE)
   - Query with no results (should return empty array)
   - Insert with null values (should handle gracefully)

6. Test SQL injection prevention:
   - Attempt SQL injection in parameters
   - Verify prepared statements prevent it

**Verification Checklist:**
- [ ] All query functions have tests
- [ ] Edge cases covered
- [ ] Tests use isolated in-memory database
- [ ] Boolean mapping tested
- [ ] SQL injection prevention verified
- [ ] All tests pass: `npm test`
- [ ] Coverage >90% for database queries

**Testing Instructions:**

```bash
cd Migration/expo-project

# Run database tests
npm test -- database/queries

# Run with coverage
npm test -- --coverage database/queries

# Coverage should be >90%
```

**Commit Message Template:**
```
test(database): add comprehensive query function tests

- Add tests for all movie query functions
- Add tests for video and review query functions
- Test edge cases (null, duplicates, non-existent)
- Test boolean to INTEGER mapping
- Verify SQL injection prevention
- Achieve >90% code coverage
```

**Estimated Tokens:** ~4,000

---

### Task 7: Write Zustand Store Tests

**Goal:** Create unit tests for Zustand stores to ensure state management works correctly.

**Files to Create:**
- `Migration/expo-project/__tests__/store/movieStore.test.ts`
- `Migration/expo-project/__tests__/store/filterStore.test.ts`

**Prerequisites:**
- Task 5 completed (filter store created)
- Database query functions mocked for testing

**Implementation Steps:**

1. Set up store test utilities:
   - Mock database query functions
   - Reset store state before each test
   - Use Zustand's testing utilities

2. Test `movieStore.test.ts`:
   - Test initial state (empty movies, not loading, no error)
   - Test `loadPopularMovies()` - sets loading, updates movies, clears loading
   - Test `loadFavoriteMovies()` - loads correct subset
   - Test `toggleFavorite()` - updates database and store
   - Test error handling - sets error state on database failure
   - Test optimistic updates - updates UI before database confirms

3. Test `filterStore.test.ts`:
   - Test initial state (popular and toprated true, favorites false)
   - Test `togglePopular()` - updates state
   - Test `toggleTopRated()` - updates state
   - Test `toggleFavorites()` - updates state
   - Test `getActiveFilter()` - returns correct filter
   - Test integration with movie store - triggers reload

4. Test store integration:
   - Test filter changes trigger movie reloads
   - Test multiple actions in sequence
   - Test concurrent operations (race conditions)

5. Follow AAA pattern:
   - Arrange: Set up mocks and initial state
   - Act: Call store actions
   - Assert: Verify state changes

**Verification Checklist:**
- [ ] Both store test files exist
- [ ] All store actions have tests
- [ ] Error cases tested
- [ ] Loading states tested
- [ ] Store integration tested
- [ ] All tests pass
- [ ] Coverage >80% for stores

**Testing Instructions:**

```bash
cd Migration/expo-project

# Run store tests
npm test -- store

# Run with coverage
npm test -- --coverage store

# Coverage should be >80%
```

**Commit Message Template:**
```
test(store): add Zustand store tests

- Add tests for movieStore actions and state
- Add tests for filterStore toggle and selectors
- Test error handling and loading states
- Test store integration (filter → movie reload)
- Mock database queries for isolation
```

**Estimated Tokens:** ~4,000

---

## Phase Verification

Before moving to Phase 3, verify all tasks are complete:

### Functional Verification
- [ ] Database initializes without errors
- [ ] All query functions work correctly (tested manually or programmatically)
- [ ] Zustand stores update state reactively
- [ ] Filter changes trigger movie reloads
- [ ] Favorite toggle updates database and UI
- [ ] All tests pass: `npm test`

### Data Integrity Verification
- [ ] Insert movie → can retrieve by ID
- [ ] Boolean fields (favorite, popular, toprated) map correctly
- [ ] Videos link to movies via foreign key (id field)
- [ ] Reviews link to movies via foreign key (id field)
- [ ] REPLACE conflict strategy works (update instead of duplicate)

### Code Quality Verification
- [ ] No SQL injection vulnerabilities (all queries use prepared statements)
- [ ] No state mutations (all Zustand updates are immutable)
- [ ] TypeScript strict mode passes
- [ ] ESLint shows no errors
- [ ] Code coverage >80% for database and stores

### Integration Points
- [ ] Database queries integrate with Zustand stores
- [ ] Filter store triggers movie store reloads
- [ ] API services (Phase 1) can populate database (manually tested)

---

## Known Limitations & Technical Debt

**Introduced in this phase:**
- No database migration system yet (schema changes will require manual handling)
- No database performance optimization (indexes not added)
- No transaction support for batch operations
- Filter store doesn't persist across app restarts (unless AsyncStorage added)

**To be addressed in later phases:**
- Phase 4 will connect API → Database → Store → UI flow
- Phase 5 may add database indexes if performance issues arise
- Future: Implement proper migration system for schema changes

---

## Next Steps

Phase 2 is complete! You now have:
- ✅ Fully functional SQLite database matching Room schema
- ✅ All DAO queries migrated to TypeScript
- ✅ Zustand stores for reactive state management
- ✅ Comprehensive test coverage for data layer
- ✅ Foundation ready for UI integration

**Ready for Phase 3?**
Proceed to **[Phase 3: Navigation & Core UI Components](./Phase-3.md)** to build the user interface with Expo Router and React Native Paper.

---

**Estimated Total Tokens for Phase 2:** ~30,000
