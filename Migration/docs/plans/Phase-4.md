# Phase 4: Integration & Key Features

**Goal:** Connect the API layer, database layer, and UI layer into a complete working application. Implement key features like data synchronization, favorites management, and YouTube video playback.

**Success Criteria:**
- ✅ API data flows to database and UI automatically
- ✅ Initial app launch fetches movies from TMDb and stores locally
- ✅ Favorites toggle persists across app restarts
- ✅ Trailers play in WebView or external YouTube app
- ✅ Pull-to-refresh syncs with TMDb API
- ✅ Offline mode works (displays cached data)
- ✅ Error handling for network failures
- ✅ Integration tests for complete user flows

**Estimated Tokens:** ~25,000

---

## Prerequisites

Before starting this phase:

### Previous Phases
- [ ] Phase 1 completed (API services working)
- [ ] Phase 2 completed (database and stores working)
- [ ] Phase 3 completed (all screens and components built)
- [ ] All tests passing from previous phases

### Knowledge
- [ ] Understand data flow architecture from Phase-0
- [ ] Familiar with useEffect dependencies
- [ ] Understand React component lifecycle
- [ ] Reviewed Android's GetWebData.java data sync pattern

### Android Source Reference
- [ ] Access to `GetWebData.java` for API → Database flow
- [ ] Access to `MainActivity.java` for initial data loading pattern
- [ ] Understand how Android app handles favorites

---

## Tasks

### Task 1: Implement Initial Data Sync

**Goal:** On first app launch, fetch popular movies and top-rated TV shows from TMDb API and store in database.

**Files to Modify:**
- `Migration/expo-project/src/store/movieStore.ts` - Add syncWithAPI action
- `Migration/expo-project/app/index.tsx` - Call sync on mount

**Prerequisites:**
- API services working (Phase 1)
- Database queries working (Phase 2)
- Understand async/await and Promise.all patterns

**Implementation Steps:**

1. Create `syncMoviesWithAPI()` action in movieStore:
   - Fetch popular movies from TMDb: `TMDbService.getPopularMovies()`
   - Fetch top-rated TV: `TMDbService.getTopRatedTV()`
   - For each movie in response, create MovieDetails object
   - Set appropriate flags: `popular: true` for popular, `toprated: true` for top-rated
   - Insert into database using `insertMovie()` with REPLACE strategy
   - Update store state with combined results
   - Handle errors gracefully (network failures, API errors)

2. Mark movies as popular or top-rated:
   - Map TMDb API response to MovieDetails interface
   - Set `popular: true` for movies from /discover/movie
   - Set `toprated: true` for shows from /discover/tv
   - Set `favorite: false` initially (user hasn't favorited yet)

3. Use Promise.all for parallel API calls:
   - Fetch popular and top-rated simultaneously
   - Wait for both to complete before updating database
   - Improves performance vs sequential fetches

4. Add loading state management:
   - Set `loading: true` before API calls
   - Set `loading: false` after database updates
   - Set `error` state if any operation fails

5. Call `syncMoviesWithAPI()` on app mount:
   - In `app/index.tsx`, add useEffect hook
   - Check if database is empty (no movies)
   - If empty, call syncMoviesWithAPI()
   - Only run once on mount (empty dependency array)

6. Implement sync debouncing:
   - Prevent multiple syncs if already syncing
   - Use a `syncing` flag in store
   - Ignore sync requests if `syncing === true`

7. Handle pagination (optional but recommended):
   - TMDb API returns 20 results per page
   - Optionally fetch multiple pages for more movies
   - Store page 1 initially, add "Load more" later if needed

**Verification Checklist:**
- [ ] syncMoviesWithAPI action implemented
- [ ] API responses mapped to MovieDetails correctly
- [ ] Movies inserted into database with correct flags
- [ ] Loading state managed properly
- [ ] Errors handled and displayed to user
- [ ] Sync called once on first app launch
- [ ] Duplicate syncs prevented (debouncing)

**Testing Instructions:**

```bash
cd Migration/expo-project

# Clear database (or use fresh install)
# Start app
npx expo start

# In app:
# 1. On first launch, should show loading spinner
# 2. After 2-3 seconds, movies should appear in grid
# 3. Check database has movies stored
# 4. Close and reopen app (should load from database, no API call)
# 5. Verify movies have correct popular/toprated flags
```

**Commit Message Template:**
```
feat(integration): implement initial data sync from TMDb API

- Add syncMoviesWithAPI action to movie store
- Fetch popular movies and top-rated TV shows
- Insert API data into database with appropriate flags
- Call sync on first app launch
- Handle loading and error states
- Prevent duplicate syncs with debouncing
```

**Estimated Tokens:** ~5,000

---

### Task 2: Implement Pull-to-Refresh Sync

**Goal:** Allow users to manually refresh movie data from TMDb API by pulling down on the home screen.

**Files to Modify:**
- `Migration/expo-project/app/index.tsx` - Wire up RefreshControl
- `Migration/expo-project/src/store/movieStore.ts` - Add refresh action

**Prerequisites:**
- Task 1 completed (syncMoviesWithAPI implemented)
- RefreshControl already added to FlatList in Phase 3

**Implementation Steps:**

1. Create `refreshMovies()` action in movieStore:
   - Similar to syncMoviesWithAPI, but always syncs (doesn't check if DB is empty)
   - Fetches fresh data from TMDb API
   - Updates database with new data
   - Preserves user's favorite flags (don't overwrite)
   - Updates store state with refreshed movies

2. Preserve favorite status during refresh:
   - Before syncing, query existing favorites: `getFavoriteMovies()`
   - Store favorite IDs in a Set for fast lookup
   - After fetching new data, restore favorite flag if ID in Set
   - This prevents refresh from losing user's favorites

3. Wire up RefreshControl in Home screen:
   - `refreshing` state comes from movieStore
   - `onRefresh` calls `movieStore.refreshMovies()`
   - Show loading indicator while refreshing
   - Complete when store updates

4. Add last sync timestamp (optional but recommended):
   - Track when last sync occurred
   - Display "Last updated: X minutes ago" in UI
   - Prevent excessive syncing (e.g., max once per 5 minutes)

5. Handle refresh errors:
   - If API call fails, show error message
   - Keep existing data in UI (don't clear)
   - Allow user to retry

**Verification Checklist:**
- [ ] refreshMovies action implemented
- [ ] Pull-to-refresh triggers API sync
- [ ] Favorites preserved during refresh
- [ ] New movies appear in grid after refresh
- [ ] Error handling works (test with airplane mode)
- [ ] Last sync timestamp tracked (optional)

**Testing Instructions:**

```bash
cd Migration/expo-project
npx expo start

# In app:
# 1. Load app with movies displayed
# 2. Favorite a few movies
# 3. Pull down on home screen to refresh
# 4. Should show refresh spinner
# 5. After refresh, movies updated but favorites preserved
# 6. Test with airplane mode (should show error, keep existing data)
```

**Commit Message Template:**
```
feat(integration): implement pull-to-refresh sync

- Add refreshMovies action to movie store
- Wire up RefreshControl in Home screen
- Preserve favorite status during refresh
- Handle refresh errors gracefully
- Add last sync timestamp tracking
```

**Estimated Tokens:** ~3,000

---

### Task 3: Implement Movie Details Data Loading

**Goal:** Load movie details, trailers, and reviews from database (and API if missing) when details screen opens.

**Files to Modify:**
- `Migration/expo-project/app/details/[id].tsx` - Add data loading logic
- `Migration/expo-project/src/store/movieStore.ts` - Add details loading action (optional)

**Prerequisites:**
- Task 1 completed (initial sync working)
- Details screen UI implemented (Phase 3)
- Database query functions for videos and reviews

**Implementation Steps:**

1. Create `loadMovieDetails()` function in Details screen:
   - Extract movieId from route params
   - Query database for movie: `getMovieById(movieId)`
   - Query database for videos: `getVideosForMovie(movieId)`
   - Query database for reviews: `getReviewsForMovie(movieId)`
   - Set local component state with results

2. Fetch from API if not in database:
   - If `getMovieById()` returns null, movie not in DB
   - Fetch from TMDb API: `TMDbService.getMovieVideos(movieId)`
   - Fetch reviews: `TMDbService.getMovieReviews(movieId)`
   - Insert into database for future offline access
   - Populate local state

3. Fetch video thumbnails from YouTube:
   - For each video, check if `image_url` is populated
   - If empty, fetch from YouTube API: `YouTubeService.getVideoThumbnail(video.key)`
   - Update database with thumbnail URL
   - Display in VideoCards

4. Handle loading states:
   - Show LoadingSpinner while fetching
   - Show skeleton screens for better UX (optional)
   - Handle errors (movie not found, network failure)

5. Use useEffect for data loading:
   - Run on mount and when movieId changes
   - Cleanup: cancel requests if component unmounts
   - Dependency array: [movieId]

6. Optimize with caching:
   - Don't refetch if data already loaded
   - Use stale-while-revalidate pattern (show cached, update in background)

**Verification Checklist:**
- [ ] Movie details load from database
- [ ] Videos and reviews load correctly
- [ ] Falls back to API if not in database
- [ ] YouTube thumbnails fetched and displayed
- [ ] Loading states handled
- [ ] Errors handled (movie not found, network failure)
- [ ] Details screen navigable from home screen

**Testing Instructions:**

```bash
cd Migration/expo-project
npx expo start

# In app:
# 1. Tap a movie from home screen
# 2. Should navigate to details and show loading
# 3. After loading, should display movie info, trailers, reviews
# 4. Verify trailers have thumbnails
# 5. Navigate to a movie not in database (use arbitrary ID)
# 6. Should fetch from API and display
# 7. Navigate back and open again (should load from cache)
```

**Commit Message Template:**
```
feat(integration): implement movie details data loading

- Load movie, videos, and reviews from database
- Fetch from API if not in database
- Fetch YouTube thumbnails for videos
- Handle loading and error states
- Optimize with caching
```

**Estimated Tokens:** ~4,000

---

### Task 4: Implement Favorites Toggle Integration

**Goal:** Make favorites toggle functional, updating both database and UI reactively.

**Files to Modify:**
- `Migration/expo-project/src/store/movieStore.ts` - Enhance toggleFavorite action
- `Migration/expo-project/app/details/[id].tsx` - Wire up favorite button

**Prerequisites:**
- Task 3 completed (details screen loading data)
- toggleFavorite action exists from Phase 2

**Implementation Steps:**

1. Enhance `toggleFavorite()` action in movieStore:
   - Get current movie from store or database
   - Toggle `favorite` boolean
   - Update database: `insertMovie({ ...movie, favorite: !movie.favorite })`
   - Update store state optimistically (before database confirms)
   - If database update fails, rollback store state

2. Implement optimistic updates:
   - Update UI immediately when user taps favorite
   - Don't wait for database operation to complete
   - Provides instant feedback
   - Rollback if database fails

3. Wire up favorite button in Details screen:
   - IconButton with star icon
   - Filled star if favorite, outline star if not
   - onPress calls `movieStore.toggleFavorite(movieId)`
   - Subscribe to movie store to reflect state changes

4. Sync favorite state between Home and Details:
   - When user favorites in Details, Home screen updates
   - Use Zustand's reactive updates for automatic sync
   - No manual prop drilling needed

5. Update Home screen to reflect favorites:
   - MovieCards show favorite indicator if movie.favorite === true
   - When filter set to "Favorites", only favorited movies show
   - Real-time updates when favorites change

6. Handle edge cases:
   - Toggling favorite of movie not in store
   - Database update failure (show error, rollback)
   - Multiple rapid toggles (debounce)

**Verification Checklist:**
- [ ] Favorite button in Details screen toggles state
- [ ] Database updated when favorite toggled
- [ ] Store state updates optimistically
- [ ] Home screen reflects favorite changes
- [ ] Filter by Favorites shows only favorited movies
- [ ] Favorites persist across app restarts
- [ ] Errors handled gracefully

**Testing Instructions:**

```bash
cd Migration/expo-project
npx expo start

# In app:
# 1. Open a movie in Details screen
# 2. Tap favorite button (star should fill immediately)
# 3. Go back to Home screen (movie should have favorite indicator)
# 4. Open Filter screen, toggle "Show Favorites"
# 5. Should see only favorited movies
# 6. Close and reopen app (favorites should persist)
# 7. Unfavorite a movie in Details (should update everywhere)
```

**Commit Message Template:**
```
feat(integration): implement favorites toggle with optimistic updates

- Enhance toggleFavorite to update database and store
- Wire up favorite button in Details screen
- Sync favorite state between Home and Details screens
- Implement optimistic UI updates with rollback
- Persist favorites across app restarts
```

**Estimated Tokens:** ~3,500

---

### Task 5: Implement YouTube Video Playback

**Goal:** Allow users to watch trailers by tapping VideoCards, opening YouTube in WebView or external app.

**Files to Create:**
- `Migration/expo-project/src/components/YouTubePlayer.tsx` - WebView player component
- `Migration/expo-project/app/video/[key].tsx` - Full-screen video player screen (optional)

**Files to Modify:**
- `Migration/expo-project/app/details/[id].tsx` - Handle VideoCard press

**Prerequisites:**
- Task 3 completed (videos loaded in Details screen)
- Understand React Native WebView
- Reviewed Android's VideoActivity.java (deprecated) and WebView usage

**Implementation Steps:**

1. Decide on playback method:
   - **Option A:** Open YouTube in external app (simpler)
   - **Option B:** Embed YouTube in WebView (better UX)
   - Recommend **Option B** for matching Android app behavior

2. Install WebView dependency (if Option B):
   - Install `react-native-webview`
   - Ensure compatibility with Expo

3. Create `YouTubePlayer.tsx` component (Option B):
   - Props: `videoKey: string`, `onClose: () => void`
   - Use WebView to embed YouTube
   - Source: `YouTubeService.getEmbedUrl(videoKey)`
   - Full-screen modal overlay
   - Close button in top-right corner

4. Handle VideoCard press in Details screen:
   - On VideoCard press, get video.key
   - **Option A:** Use Linking API to open YouTube app
     ```typescript
     Linking.openURL(YouTubeService.getWatchUrl(video.key));
     ```
   - **Option B:** Open YouTubePlayer modal/screen
     ```typescript
     setSelectedVideoKey(video.key); // Show modal
     ```

5. Implement full-screen video player screen (Option B):
   - Create `app/video/[key].tsx` route
   - Render WebView with YouTube embed
   - Allow fullscreen controls
   - Back button to return to Details

6. Handle playback errors:
   - If YouTube fails to load, show error message
   - Provide fallback to open in external browser
   - Handle restricted/unavailable videos

7. Add loading state for video player:
   - Show loading spinner while WebView loads
   - onLoadEnd to hide spinner

**Verification Checklist:**
- [ ] Tapping VideoCard opens YouTube player
- [ ] Video plays in WebView or external app
- [ ] Close button works (Option B)
- [ ] Loading state while video loads
- [ ] Errors handled (video unavailable, network failure)
- [ ] Back navigation works
- [ ] Works on both iOS and Android

**Testing Instructions:**

```bash
cd Migration/expo-project
npx expo start

# In app:
# 1. Navigate to Details screen for a movie with trailers
# 2. Tap a VideoCard
# 3. Should open YouTube player (WebView or external)
# 4. Video should play
# 5. Close player (if WebView) or use back button
# 6. Should return to Details screen
# 7. Test with restricted video (should handle error)
```

**Commit Message Template:**
```
feat(integration): implement YouTube video playback

- Add YouTubePlayer component with WebView
- Wire up VideoCard press to open player
- Implement full-screen video player screen
- Handle loading and error states
- Support both embedded and external playback
```

**Estimated Tokens:** ~4,000

---

### Task 6: Implement Offline Mode Support

**Goal:** Ensure app works offline by displaying cached data when network is unavailable.

**Files to Modify:**
- `Migration/expo-project/src/store/movieStore.ts` - Add offline detection
- `Migration/expo-project/app/index.tsx` - Show offline indicator

**Prerequisites:**
- All previous tasks completed
- Understand NetInfo API from Expo

**Implementation Steps:**

1. Install NetInfo dependency:
   - Install `@react-native-community/netinfo`
   - Expo-compatible package for network status

2. Add network state to movieStore:
   - State: `isOffline: boolean`
   - Subscribe to NetInfo events
   - Update state when network status changes

3. Modify API sync logic to respect offline state:
   - In `syncMoviesWithAPI()` and `refreshMovies()`, check `isOffline`
   - If offline, skip API calls and load from database only
   - Show user-friendly message: "You're offline. Showing cached movies."

4. Add offline indicator in Home screen:
   - Banner at top of screen when offline
   - Use Paper's Banner component
   - Message: "No internet connection. Showing cached data."
   - Dismissible or persistent (recommend persistent)

5. Handle graceful degradation:
   - Movie grid still works (shows cached movies)
   - Details screen shows cached details/videos/reviews
   - Favorites toggle still works (updates database, syncs later)
   - Pull-to-refresh shows error message when offline

6. Queue operations for when online returns (optional):
   - Track favorite toggles made while offline
   - When network returns, sync changes to server (if you add backend)
   - For now, just ensure database updates persist

**Verification Checklist:**
- [ ] NetInfo installed and working
- [ ] Offline state tracked in store
- [ ] API calls skipped when offline
- [ ] Offline banner displayed
- [ ] App fully functional with cached data
- [ ] Favorites toggle works offline
- [ ] Graceful error messages for network operations

**Testing Instructions:**

```bash
cd Migration/expo-project
npx expo start

# In app:
# 1. Load app with internet connection
# 2. Enable airplane mode on device/simulator
# 3. Pull to refresh (should show "offline" error)
# 4. Should still see cached movies
# 5. Tap movie to view details (should load from cache)
# 6. Toggle favorite (should work, save to database)
# 7. Disable airplane mode
# 8. Offline banner should disappear
# 9. Pull to refresh (should sync successfully)
```

**Commit Message Template:**
```
feat(integration): implement offline mode support

- Add NetInfo for network status detection
- Track offline state in movie store
- Skip API calls when offline, use cached data
- Display offline banner in Home screen
- Ensure favorites toggle works offline
- Graceful degradation for all features
```

**Estimated Tokens:** ~3,500

---

### Task 7: Write Integration Tests

**Goal:** Create integration tests for complete user flows (browse → view → favorite → filter).

**Files to Create:**
- `Migration/expo-project/__tests__/integration/movieFlow.test.tsx`
- `Migration/expo-project/__tests__/integration/favoritesFlow.test.tsx`

**Prerequisites:**
- All previous tasks completed
- React Native Testing Library installed
- Understand integration testing patterns

**Implementation Steps:**

1. Create `movieFlow.test.tsx` for browse flow:
   - Test: User opens app → sees movies → taps movie → sees details → goes back
   - Mock API responses
   - Mock database operations
   - Assert on rendered components
   - Verify navigation calls

2. Create `favoritesFlow.test.tsx` for favorites flow:
   - Test: User opens app → favorites movie → filters by favorites → sees only favorited movie
   - Test: User unfavorites → movie disappears from favorites filter
   - Mock store and database
   - Assert on UI updates

3. Test data sync flow:
   - Test: App launches → calls API → saves to database → displays in UI
   - Mock fetch API
   - Verify database insert calls
   - Assert on UI rendering

4. Test pull-to-refresh flow:
   - Test: User pulls to refresh → API called → database updated → UI refreshed
   - Mock RefreshControl
   - Verify API and database calls
   - Assert on loading states

5. Test offline flow:
   - Test: User offline → app shows cached data → offline banner visible
   - Mock NetInfo as offline
   - Verify no API calls made
   - Assert on offline banner

6. Use end-to-end style assertions:
   - Test complete user journeys, not isolated functions
   - Verify integration between layers (API → DB → Store → UI)
   - Focus on user-visible outcomes

**Verification Checklist:**
- [ ] Integration test files created
- [ ] Main user flows tested (browse, details, favorites, filter)
- [ ] Data sync flow tested
- [ ] Offline flow tested
- [ ] Tests use proper mocking
- [ ] All tests pass
- [ ] Tests catch integration bugs

**Testing Instructions:**

```bash
cd Migration/expo-project

# Run integration tests
npm test -- integration

# Run all tests
npm test

# All tests should pass
```

**Commit Message Template:**
```
test(integration): add integration tests for user flows

- Add movieFlow test (browse → details → back)
- Add favoritesFlow test (favorite → filter → verify)
- Test data sync flow (API → DB → UI)
- Test pull-to-refresh flow
- Test offline mode flow
- Ensure all integration tests pass
```

**Estimated Tokens:** ~4,000

---

## Phase Verification

Before moving to Phase 5, verify all tasks are complete:

### Functional Verification
- [ ] App fetches and displays movies from TMDb on first launch
- [ ] Pull-to-refresh syncs new data
- [ ] Movie details, trailers, and reviews load correctly
- [ ] Favorites toggle works and persists
- [ ] YouTube trailers play in WebView or external app
- [ ] App works offline with cached data
- [ ] All integration tests pass

### Data Flow Verification
- [ ] API → Database → Store → UI flow works end-to-end
- [ ] Optimistic updates provide instant feedback
- [ ] Store state updates trigger UI re-renders
- [ ] Database updates persist across app restarts
- [ ] Network errors handled gracefully

### User Experience Verification
- [ ] Initial load is fast (shows cached data if available)
- [ ] Pull-to-refresh provides feedback
- [ ] Favorites toggle is instant
- [ ] Video playback works smoothly
- [ ] Offline mode is transparent (app still usable)
- [ ] Error messages are user-friendly

### Code Quality Verification
- [ ] TypeScript strict mode passes
- [ ] ESLint shows no errors
- [ ] All tests pass (unit + integration)
- [ ] No console errors or warnings
- [ ] Code follows Phase-0 patterns

---

## Known Limitations & Technical Debt

**Introduced in this phase:**
- No pagination (only loads first page of movies from API)
- No search functionality
- Favorites not synced to cloud (local only)
- No user accounts or authentication

**To be addressed in later phases:**
- Phase 5 will add polish and optimization
- Future: Add pagination for loading more movies
- Future: Add search bar for filtering movies
- Future: Add backend for cloud sync of favorites

---

## Next Steps

Phase 4 is complete! You now have:
- ✅ Fully integrated app with API → Database → UI data flow
- ✅ Working favorites feature with persistence
- ✅ YouTube video playback
- ✅ Offline mode support
- ✅ Comprehensive integration tests
- ✅ Complete feature parity with Android app

**Ready for Phase 5?**
Proceed to **[Phase 5: Testing, Polish & Deployment](./Phase-5.md)** to add final touches, comprehensive testing, and prepare for release.

---

**Estimated Total Tokens for Phase 4:** ~25,000
