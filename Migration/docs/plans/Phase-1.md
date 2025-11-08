# Phase 1: Project Setup & API Service Layer

**Goal:** Initialize the Expo React Native project with TypeScript, implement TMDb and YouTube API services with typed responses, and establish the foundational project structure.

**Success Criteria:**
- ✅ Expo project created with TypeScript and Expo Router
- ✅ All core dependencies installed and configured
- ✅ TypeScript interfaces defined for all data models (Room entity equivalents)
- ✅ TMDb API service implemented with all required endpoints
- ✅ YouTube API service implemented for thumbnail fetching
- ✅ Environment variables configured for API keys
- ✅ API services tested and working

**Estimated Tokens:** ~25,000

---

## Prerequisites

Before starting this phase:

### Environment Setup
- [ ] Node.js v18+ installed
- [ ] npm or yarn installed
- [ ] Expo CLI installed globally: `npm install -g expo-cli`
- [ ] Git configured with your name and email

### API Keys
- [ ] TMDb API key obtained from [https://www.themoviedb.org/settings/api](https://www.themoviedb.org/settings/api)
- [ ] YouTube Data API v3 key obtained from [Google Cloud Console](https://console.cloud.google.com/)

### Knowledge
- [ ] Read Phase-0.md (Architecture & Design Decisions)
- [ ] Understand ADR-001 through ADR-007
- [ ] Familiar with TypeScript basics
- [ ] Familiar with async/await pattern

---

## Tasks

### Task 1: Initialize Expo Project

**Goal:** Create a new Expo project with TypeScript and Expo Router support.

**Files to Create:**
- `Migration/expo-project/` - Root project directory
- `Migration/expo-project/app.json` - Expo configuration
- `Migration/expo-project/package.json` - Dependencies
- `Migration/expo-project/tsconfig.json` - TypeScript configuration

**Prerequisites:**
- Expo CLI installed globally
- Current directory is `/home/user/android-movies/Migration/`

**Implementation Steps:**

1. Navigate to the Migration directory:
   ```bash
   cd /home/user/android-movies/Migration
   ```

2. Initialize a new Expo project with TypeScript and Expo Router template:
   - Use the Expo CLI to create a new project
   - Choose the "tabs" template which includes Expo Router
   - Name the project "expo-project"
   - Ensure TypeScript is enabled

3. Verify project structure was created correctly:
   - Check that `app/` directory exists (Expo Router convention)
   - Check that `tsconfig.json` exists
   - Verify `package.json` contains Expo Router dependencies

4. Clean up template files:
   - Remove example tab navigation boilerplate that won't be used
   - Keep the root `_layout.tsx` for app-wide providers
   - Prepare for custom screens to be added in Phase 3

**Verification Checklist:**
- [ ] `Migration/expo-project/` directory exists
- [ ] `app/_layout.tsx` exists (root layout file)
- [ ] `package.json` contains `expo-router` dependency
- [ ] `tsconfig.json` has `"strict": true`
- [ ] Project can be started with `npx expo start` (don't leave it running)

**Testing Instructions:**
```bash
cd Migration/expo-project
npx expo start
# Should open Expo DevTools without errors
# Press Ctrl+C to stop
```

**Commit Message Template:**
```
feat(setup): initialize Expo project with TypeScript

- Create expo-project with Expo Router template
- Configure TypeScript with strict mode
- Set up basic project structure
```

**Estimated Tokens:** ~2,000

---

### Task 2: Install Core Dependencies

**Goal:** Install and configure all required libraries for the migration.

**Files to Modify:**
- `Migration/expo-project/package.json` - Add dependencies

**Prerequisites:**
- Task 1 completed (project initialized)

**Implementation Steps:**

1. Install database and storage dependencies:
   - Install `expo-sqlite` for local database
   - Use `npx expo install` to ensure version compatibility

2. Install state management:
   - Install `zustand` for global state
   - Install `@types/zustand` if needed for TypeScript

3. Install UI components:
   - Install `react-native-paper` for Material Design components
   - Install required peer dependencies (react-native-vector-icons, etc.)

4. Install image handling:
   - Install `expo-image` for optimized image loading
   - Ensure it's the Expo version, not react-native-image

5. Verify no dependency conflicts:
   - Run the install command
   - Check for peer dependency warnings
   - Resolve any version mismatches

6. Update `package.json` scripts if needed:
   - Add convenience scripts for testing
   - Add linting scripts for code quality

**Verification Checklist:**
- [ ] `expo-sqlite` installed and listed in dependencies
- [ ] `zustand` installed (check package.json)
- [ ] `react-native-paper` installed
- [ ] `expo-image` installed
- [ ] `npm install` or `yarn install` completes without errors
- [ ] No critical peer dependency warnings

**Testing Instructions:**
```bash
cd Migration/expo-project
npm list expo-sqlite zustand react-native-paper expo-image
# All should show as installed

npm install
# Should complete successfully
```

**Commit Message Template:**
```
feat(deps): install core dependencies

- Add expo-sqlite for local database
- Add zustand for state management
- Add react-native-paper for Material Design UI
- Add expo-image for optimized image loading
```

**Estimated Tokens:** ~1,500

---

### Task 3: Configure TypeScript and Code Quality Tools

**Goal:** Set up TypeScript strict mode, ESLint, and Prettier for code quality.

**Files to Create/Modify:**
- `Migration/expo-project/tsconfig.json` - TypeScript configuration
- `Migration/expo-project/.eslintrc.js` - ESLint rules
- `Migration/expo-project/.prettierrc` - Prettier formatting

**Prerequisites:**
- Task 2 completed (dependencies installed)

**Implementation Steps:**

1. Update `tsconfig.json` for strict type checking:
   - Enable `"strict": true`
   - Enable `"noImplicitAny": true`
   - Enable `"strictNullChecks": true`
   - Add path aliases for cleaner imports (e.g., `@/api/*` → `src/api/*`)

2. Create `.eslintrc.js` with recommended rules:
   - Extend `expo` ESLint config
   - Add TypeScript-specific rules
   - Add React Hooks rules
   - Disallow `any` types
   - Warn on console.log statements

3. Create `.prettierrc` for consistent formatting:
   - Configure for 2-space indentation
   - Single quotes for strings
   - Trailing commas for ES5
   - Print width of 100 characters

4. Add lint and format scripts to `package.json`:
   - Add `"lint": "eslint ."` script
   - Add `"format": "prettier --write ."` script
   - Add `"type-check": "tsc --noEmit"` script

5. Install required ESLint and Prettier dependencies:
   - Install `eslint`, `prettier`, and necessary plugins
   - Ensure compatibility with Expo

**Verification Checklist:**
- [ ] `tsconfig.json` has `"strict": true`
- [ ] Path aliases configured in `tsconfig.json`
- [ ] `.eslintrc.js` exists and extends expo config
- [ ] `.prettierrc` exists with consistent formatting rules
- [ ] `npm run lint` executes without errors (may have warnings for now)
- [ ] `npm run type-check` executes successfully

**Testing Instructions:**
```bash
cd Migration/expo-project
npm run type-check
# Should complete with no errors

npm run lint
# May have warnings but no errors

npm run format
# Should format all files
```

**Commit Message Template:**
```
chore(config): configure TypeScript and code quality tools

- Enable TypeScript strict mode
- Configure ESLint with TypeScript rules
- Add Prettier for consistent formatting
- Add npm scripts for linting and type checking
```

**Estimated Tokens:** ~2,000

---

### Task 4: Define TypeScript Interfaces (Room Entity Migration)

**Goal:** Create TypeScript interfaces that exactly match the Room entities from the Android app.

**Files to Create:**
- `Migration/expo-project/src/models/types.ts` - Domain models
- `Migration/expo-project/src/api/types.ts` - API response types

**Prerequisites:**
- Task 3 completed (TypeScript configured)
- Access to read Android source files in `../app/src/main/java/gemenielabs/movies/Database/`

**Implementation Steps:**

1. Create `src/models/` directory for domain models

2. Define `MovieDetails` interface (migrate from `MovieDetails.java`):
   - Map all Room `@ColumnInfo` fields to TypeScript properties
   - Java types → TypeScript types (int → number, boolean → boolean, String → string)
   - Include all fields: id, title, overview, poster_path, release_date, vote_average, vote_count, popularity, original_language, favorite, toprated, popular
   - Use snake_case for field names to match TMDb API and Room columns

3. Define `VideoDetails` interface (migrate from `VideoDetails.java`):
   - Include: identity (auto-generated), id (foreign key), iso_639_1, iso_3166_1, key, site, size, type, image_url
   - Note: `identity` will be auto-generated in SQLite (not needed in TypeScript until after DB insertion)

4. Define `ReviewDetails` interface (migrate from `ReviewDetails.java`):
   - Include: identity (auto-generated), id (foreign key), author, content

5. Create `src/api/types.ts` for API-specific response types:
   - Define `TMDbDiscoverResponse` (wraps array of movies)
   - Define `TMDbMovie` (raw API response before database mapping)
   - Define `TMDbVideosResponse` and `TMDbVideo`
   - Define `TMDbReviewsResponse` and `TMDbReview`
   - Define `YouTubeVideoResponse` and `YouTubeVideoItem`

6. Document differences between API types and database models:
   - API returns snake_case, database stores snake_case (consistent)
   - Some API fields may not be stored in database
   - Add JSDoc comments explaining field purposes

**Verification Checklist:**
- [ ] `src/models/types.ts` exists with MovieDetails, VideoDetails, ReviewDetails
- [ ] All fields from Room entities are present
- [ ] Type mapping is correct (Java → TypeScript)
- [ ] `src/api/types.ts` exists with TMDb and YouTube response types
- [ ] No `any` types used
- [ ] TypeScript compiler has no errors (`npm run type-check`)

**Testing Instructions:**
```bash
cd Migration/expo-project
npm run type-check
# Should pass with no errors

# Verify interfaces can be imported
echo "import { MovieDetails } from './src/models/types';" | npx ts-node
# Should not error
```

**Commit Message Template:**
```
feat(models): define TypeScript interfaces from Room entities

- Add MovieDetails interface (migrated from MovieDetails.java)
- Add VideoDetails interface (migrated from VideoDetails.java)
- Add ReviewDetails interface (migrated from ReviewDetails.java)
- Add API response types for TMDb and YouTube
- Use strict TypeScript types (no any)
```

**Estimated Tokens:** ~3,000

---

### Task 5: Set Up Environment Variables

**Goal:** Configure environment variables for API keys using Expo's environment variable system.

**Files to Create:**
- `Migration/expo-project/.env` - Local environment variables (gitignored)
- `Migration/expo-project/.env.example` - Template for other developers
- `Migration/expo-project/.gitignore` - Ensure .env is ignored

**Prerequisites:**
- Task 4 completed (TypeScript interfaces defined)
- TMDb API key available
- YouTube API key available

**Implementation Steps:**

1. Create `.env` file with API keys:
   - Use `EXPO_PUBLIC_` prefix for variables accessible in client code
   - Add `EXPO_PUBLIC_TMDB_API_KEY=your_key_here`
   - Add `EXPO_PUBLIC_YOUTUBE_API_KEY=your_key_here`

2. Create `.env.example` template (without actual keys):
   - Same structure as `.env` but with placeholder values
   - Add comments explaining how to obtain each key
   - This file will be committed to git

3. Update `.gitignore` to exclude `.env`:
   - Ensure `.env` is listed (usually already there)
   - Verify `.env.example` is NOT ignored

4. Document environment setup in a README or comment:
   - Explain that developers need to copy `.env.example` to `.env`
   - Explain how to obtain API keys

5. Test that environment variables are accessible:
   - Create a simple test to log `process.env.EXPO_PUBLIC_TMDB_API_KEY`
   - Ensure it's not undefined

**Verification Checklist:**
- [ ] `.env` file exists with actual API keys
- [ ] `.env.example` exists with placeholder keys
- [ ] `.gitignore` includes `.env`
- [ ] Environment variables are accessible via `process.env.EXPO_PUBLIC_*`
- [ ] `.env` is NOT committed to git
- [ ] `.env.example` IS committed to git

**Testing Instructions:**
```bash
cd Migration/expo-project

# Check .gitignore contains .env
grep "\.env$" .gitignore
# Should output: .env

# Verify .env exists
ls -la | grep "\.env$"
# Should show .env file

# Test environment variable access
node -e "console.log(process.env.EXPO_PUBLIC_TMDB_API_KEY)"
# Should output your API key (or undefined if Expo needs to process it)
```

**Commit Message Template:**
```
chore(config): set up environment variables for API keys

- Add .env.example template for TMDb and YouTube keys
- Update .gitignore to exclude .env
- Document environment setup process
```

**Estimated Tokens:** ~1,500

---

### Task 6: Implement TMDb API Service

**Goal:** Create a TypeScript API service for TMDb that replicates `GetWebData.java` functionality.

**Files to Create:**
- `Migration/expo-project/src/api/tmdb.ts` - TMDb API client
- `Migration/expo-project/src/api/errors.ts` - Custom error classes

**Prerequisites:**
- Task 5 completed (environment variables configured)
- Read `GetWebData.java` to understand existing endpoint usage
- TMDb API key available

**Implementation Steps:**

1. Create `src/api/errors.ts` with custom error classes:
   - Define `APIError` class with statusCode and endpoint properties
   - Define `NetworkError` class for network failures
   - Extend the built-in Error class properly

2. Create `src/api/tmdb.ts` with base configuration:
   - Define `TMDB_BASE_URL` constant (`https://api.themoviedb.org/3`)
   - Get API key from environment: `process.env.EXPO_PUBLIC_TMDB_API_KEY`
   - Create a private helper method for GET requests with error handling

3. Implement `getPopularMovies()` method:
   - Endpoint: `/discover/movie`
   - Query params: `api_key`, `page`, `sort_by=popularity.desc`
   - Return type: `Promise<TMDbDiscoverResponse>`
   - Handle errors (network, 401, 404, 500)

4. Implement `getTopRatedTV()` method:
   - Endpoint: `/discover/tv`
   - Query params: `api_key`, `page`, `sort_by=vote_average.desc`, `vote_count.gte=100`
   - Return type: `Promise<TMDbDiscoverResponse>`
   - Matches the Android app's getMovieDetails() TV portion

5. Implement `getMovieVideos()` method:
   - Endpoint: `/movie/{movieId}/videos`
   - Query params: `api_key`
   - Return type: `Promise<TMDbVideosResponse>`
   - Fetches trailers and clips for a specific movie

6. Implement `getMovieReviews()` method:
   - Endpoint: `/movie/{movieId}/reviews`
   - Query params: `api_key`
   - Return type: `Promise<TMDbReviewsResponse>`

7. Add utility method `getPosterUrl()`:
   - Constructs full image URL from poster_path
   - Supports different sizes (w185, w342, w500)
   - Returns placeholder if poster_path is null
   - Static method for easy access

8. Add proper error handling:
   - Throw `APIError` for HTTP errors (status !== 200)
   - Throw `NetworkError` for network failures
   - Include meaningful error messages
   - Type all responses properly

**Verification Checklist:**
- [ ] `src/api/errors.ts` defines APIError and NetworkError
- [ ] `src/api/tmdb.ts` exports TMDbService class/object
- [ ] All methods are async and return typed Promises
- [ ] API key is loaded from environment variables
- [ ] Error handling is comprehensive
- [ ] No `any` types used
- [ ] JSDoc comments explain each method
- [ ] TypeScript type checking passes

**Testing Instructions:**

Create a simple test file or use a REPL to verify:

```typescript
import { TMDbService } from './src/api/tmdb';

// Test popular movies
const movies = await TMDbService.getPopularMovies();
console.log(movies.results.length); // Should be 20
console.log(movies.results[0].title); // Should show a movie title

// Test top-rated TV
const tvShows = await TMDbService.getTopRatedTV();
console.log(tvShows.results[0].title);

// Test movie videos (use a known movie ID, e.g., 550 for Fight Club)
const videos = await TMDbService.getMovieVideos(550);
console.log(videos.results.length); // Should have trailers

// Test poster URL
const posterUrl = TMDbService.getPosterUrl('/abc123.jpg');
console.log(posterUrl); // Should be full URL
```

**Commit Message Template:**
```
feat(api): implement TMDb API service

- Add TMDbService with getPopularMovies, getTopRatedTV
- Add getMovieVideos and getMovieReviews methods
- Add getPosterUrl utility for image URLs
- Implement custom error classes (APIError, NetworkError)
- Use environment variables for API key
- Add comprehensive error handling
```

**Estimated Tokens:** ~6,000

---

### Task 7: Implement YouTube API Service

**Goal:** Create a TypeScript API service for YouTube Data API v3 to fetch video thumbnails.

**Files to Create:**
- `Migration/expo-project/src/api/youtube.ts` - YouTube API client

**Prerequisites:**
- Task 6 completed (TMDb API service implemented)
- YouTube API key available in environment
- Read `GetWebData.java` YouTube integration portion

**Implementation Steps:**

1. Create `src/api/youtube.ts` with base configuration:
   - Define `YOUTUBE_BASE_URL` constant (`https://www.googleapis.com/youtube/v3`)
   - Get API key from environment: `process.env.EXPO_PUBLIC_YOUTUBE_API_KEY`
   - Reuse the same error classes from TMDb service

2. Implement `getVideoThumbnail()` method:
   - Endpoint: `/videos`
   - Query params: `id={videoKey}`, `key={apiKey}`, `part=snippet`
   - Return type: `Promise<string>` (thumbnail URL)
   - Extract high-quality thumbnail from response
   - Fallback to default YouTube thumbnail if API fails: `https://img.youtube.com/vi/{videoKey}/hqdefault.jpg`

3. Add utility methods for YouTube URLs:
   - `getWatchUrl(videoKey: string)`: Returns `https://www.youtube.com/watch?v={videoKey}`
   - `getEmbedUrl(videoKey: string)`: Returns `https://www.youtube.com/embed/{videoKey}` (for WebView)
   - Static methods for easy access throughout the app

4. Handle API errors gracefully:
   - If YouTube API fails, fall back to default thumbnail
   - Log warning but don't throw error (degrade gracefully)
   - YouTube API can be rate-limited or fail without breaking the app

**Verification Checklist:**
- [ ] `src/api/youtube.ts` exports YouTubeService
- [ ] `getVideoThumbnail()` method implemented
- [ ] Utility methods for watch and embed URLs exist
- [ ] Graceful fallback to default thumbnail on API failure
- [ ] API key loaded from environment
- [ ] No `any` types used
- [ ] TypeScript compilation passes

**Testing Instructions:**

```typescript
import { YouTubeService } from './src/api/youtube';

// Test with a known video key (example: dQw4w9WgXcQ)
const thumbnailUrl = await YouTubeService.getVideoThumbnail('dQw4w9WgXcQ');
console.log(thumbnailUrl); // Should be a valid image URL

// Test URL utilities
const watchUrl = YouTubeService.getWatchUrl('dQw4w9WgXcQ');
console.log(watchUrl); // https://www.youtube.com/watch?v=dQw4w9WgXcQ

const embedUrl = YouTubeService.getEmbedUrl('dQw4w9WgXcQ');
console.log(embedUrl); // https://www.youtube.com/embed/dQw4w9WgXcQ
```

**Commit Message Template:**
```
feat(api): implement YouTube API service

- Add YouTubeService with getVideoThumbnail method
- Add getWatchUrl and getEmbedUrl utilities
- Implement graceful fallback to default thumbnails
- Use environment variables for API key
```

**Estimated Tokens:** ~3,000

---

### Task 8: Create API Service Tests

**Goal:** Write unit tests for TMDb and YouTube API services to ensure they work correctly.

**Files to Create:**
- `Migration/expo-project/__tests__/api/tmdb.test.ts`
- `Migration/expo-project/__tests__/api/youtube.test.ts`

**Prerequisites:**
- Task 7 completed (both API services implemented)
- Jest installed (comes with Expo by default)

**Implementation Steps:**

1. Set up test utilities:
   - Create mock responses for TMDb API
   - Create mock responses for YouTube API
   - Mock global `fetch` function for controlled testing

2. Create `__tests__/api/tmdb.test.ts`:
   - Test `getPopularMovies()` success case
   - Test `getTopRatedTV()` success case
   - Test `getMovieVideos()` success case
   - Test `getMovieReviews()` success case
   - Test error handling (401, 404, network failure)
   - Test `getPosterUrl()` utility

3. Create `__tests__/api/youtube.test.ts`:
   - Test `getVideoThumbnail()` success case
   - Test fallback to default thumbnail on API failure
   - Test `getWatchUrl()` utility
   - Test `getEmbedUrl()` utility

4. Follow AAA pattern (Arrange, Act, Assert):
   - Arrange: Set up mocks and test data
   - Act: Call the API method
   - Assert: Verify the result

5. Ensure tests are isolated:
   - Each test should mock fetch independently
   - Clean up mocks after each test
   - No shared state between tests

**Verification Checklist:**
- [ ] Test files exist in `__tests__/api/`
- [ ] All API methods have at least one success test
- [ ] Error cases are tested (401, 404, network failure)
- [ ] Tests use proper mocking (don't call real APIs)
- [ ] All tests pass: `npm test`
- [ ] Code coverage >80% for API services

**Testing Instructions:**

```bash
cd Migration/expo-project

# Run all tests
npm test

# Run only API tests
npm test -- api

# Run with coverage
npm test -- --coverage

# Coverage should show >80% for src/api/tmdb.ts and src/api/youtube.ts
```

**Commit Message Template:**
```
test(api): add unit tests for TMDb and YouTube services

- Add tests for TMDb getPopularMovies, getTopRatedTV, getMovieVideos
- Add tests for YouTube getVideoThumbnail
- Mock fetch to avoid real API calls
- Test error handling for network failures
- Achieve >80% code coverage
```

**Estimated Tokens:** ~5,000

---

## Phase Verification

Before moving to Phase 2, verify all tasks are complete:

### Functional Verification
- [ ] Expo project runs without errors: `npx expo start`
- [ ] TypeScript compilation passes: `npm run type-check`
- [ ] ESLint passes: `npm run lint`
- [ ] All tests pass: `npm test`
- [ ] API services can fetch real data from TMDb and YouTube

### Code Quality Verification
- [ ] No `any` types in codebase
- [ ] All functions have return type annotations
- [ ] ESLint shows no errors (warnings acceptable)
- [ ] Prettier formatting applied to all files
- [ ] Git history shows clear, atomic commits

### Structure Verification
- [ ] Project follows structure defined in Phase-0
- [ ] All files are in correct directories (`src/api/`, `src/models/`, etc.)
- [ ] Import paths use TypeScript aliases if configured
- [ ] `.env` is gitignored, `.env.example` is committed

### Integration Points
- [ ] TMDb API can fetch popular movies
- [ ] TMDb API can fetch top-rated TV shows
- [ ] TMDb API can fetch movie videos
- [ ] TMDb API can fetch movie reviews
- [ ] YouTube API can fetch video thumbnails
- [ ] Environment variables are accessible

---

## Known Limitations & Technical Debt

**Introduced in this phase:**
- No caching implemented yet (every API call hits the network)
- No request deduplication (same request multiple times will make multiple calls)
- No rate limiting protection (could hit TMDb/YouTube limits)

**To be addressed in later phases:**
- Phase 2 will add database caching for API responses
- Phase 4 will implement loading states and error UI
- Phase 5 may add advanced caching strategies if needed

---

## Next Steps

Phase 1 is complete! You now have:
- ✅ Fully configured Expo + TypeScript project
- ✅ Type-safe API services for TMDb and YouTube
- ✅ Comprehensive test coverage for API layer
- ✅ Environment variable configuration
- ✅ Strong foundation for Phase 2

**Ready for Phase 2?**
Proceed to **[Phase 2: Database & State Management](./Phase-2.md)** to implement the local database (expo-sqlite) and Zustand stores.

---

**Estimated Total Tokens for Phase 1:** ~25,000
