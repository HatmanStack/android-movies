# Phase 3: Navigation & Core UI Components

**Goal:** Implement Expo Router navigation, build core UI screens (Home, Details, Filter), and create reusable React Native Paper components matching the Android app's Material Design.

**Success Criteria:**
- ✅ Expo Router file-based navigation configured
- ✅ Home screen with movie grid (RecyclerView equivalent)
- ✅ Movie details screen with trailers and reviews
- ✅ Filter screen for toggling Popular/TopRated/Favorites
- ✅ Reusable MovieCard, VideoCard, ReviewCard components
- ✅ Material Design UI matching Android app aesthetic
- ✅ Smooth navigation transitions
- ✅ Component tests for all UI elements

**Estimated Tokens:** ~35,000

---

## Prerequisites

Before starting this phase:

### Previous Phases
- [ ] Phase 1 completed (API services working)
- [ ] Phase 2 completed (database and stores working)
- [ ] All tests passing from previous phases

### Knowledge
- [ ] Read Phase-0.md ADR-006 (UI Components - React Native Paper)
- [ ] Read Phase-0.md ADR-007 (Navigation - Expo Router)
- [ ] Familiar with FlatList component
- [ ] Understand React Navigation patterns
- [ ] Reviewed Android UI files (MainActivity, DetailsActivity, SearchFragment)

### Android Source Reference
- [ ] Access to `app/src/main/res/layout/` for layout references
- [ ] Access to `MainActivity.java`, `DetailsActivity.java`
- [ ] Understand RecyclerView adapters

---

## Tasks

### Task 1: Configure Expo Router

**Goal:** Set up Expo Router file-based navigation with root layout and proper theming.

**Files to Create/Modify:**
- `Migration/expo-project/app/_layout.tsx` - Root layout
- `Migration/expo-project/app/index.tsx` - Home screen (placeholder)
- `Migration/expo-project/app/details/[id].tsx` - Details screen (placeholder)
- `Migration/expo-project/app/filter.tsx` - Filter screen (placeholder)

**Prerequisites:**
- Task completed: Phase 1, Task 1 (Expo project initialized)
- Expo Router already installed

**Implementation Steps:**

1. Configure root layout in `app/_layout.tsx`:
   - Import `PaperProvider` from react-native-paper for theming
   - Import `Stack` from expo-router for navigation
   - Wrap app with PaperProvider and Material Design 3 theme
   - Define Stack navigator with screen options
   - Set up global navigation bar styling

2. Define Material Design theme:
   - Use `MD3LightTheme` from react-native-paper
   - Customize primary and secondary colors to match Android app
   - Set up typography (font sizes, weights)
   - Configure dark mode support (optional, for future)

3. Create `app/index.tsx` (Home screen placeholder):
   - Simple component with "Home Screen" text
   - Will be implemented fully in Task 3
   - Export default functional component

4. Create `app/details/[id].tsx` (Details screen placeholder):
   - Use `useLocalSearchParams` to get movie ID from route
   - Simple component with "Details for movie {id}" text
   - Will be implemented fully in Task 4

5. Create `app/filter.tsx` (Filter screen placeholder):
   - Simple component with "Filter Screen" text
   - Will be implemented fully in Task 5

6. Test navigation:
   - Verify you can navigate between screens using `router.push()`
   - Verify route params work correctly
   - Verify back navigation works

**Verification Checklist:**
- [ ] `app/_layout.tsx` configures PaperProvider and Stack navigator
- [ ] Material Design theme applied globally
- [ ] All three screen files created (index, details/[id], filter)
- [ ] Navigation works between screens
- [ ] Route params accessible in details screen
- [ ] TypeScript compilation passes

**Testing Instructions:**

```bash
cd Migration/expo-project
npx expo start

# In app:
# 1. Should see Home screen
# 2. Navigate to /details/123 (programmatically or via link)
# 3. Should see Details screen with ID: 123
# 4. Navigate back to Home
# 5. Navigate to /filter
# 6. Should see Filter screen
```

**Commit Message Template:**
```
feat(navigation): configure Expo Router with Material Design theme

- Set up root layout with PaperProvider
- Configure Stack navigator
- Create placeholder screens (home, details, filter)
- Apply Material Design 3 theme
- Test navigation and route params
```

**Estimated Tokens:** ~3,000

---

### Task 2: Create Reusable UI Components

**Goal:** Build reusable MovieCard, VideoCard, and ReviewCard components using React Native Paper.

**Files to Create:**
- `Migration/expo-project/src/components/MovieCard.tsx`
- `Migration/expo-project/src/components/VideoCard.tsx`
- `Migration/expo-project/src/components/ReviewCard.tsx`
- `Migration/expo-project/src/components/LoadingSpinner.tsx`
- `Migration/expo-project/src/components/ErrorMessage.tsx`

**Prerequisites:**
- Task 1 completed (navigation configured)
- React Native Paper installed

**Implementation Steps:**

1. Create `MovieCard.tsx`:
   - Props: `movie: MovieDetails`, `onPress: (id: number) => void`
   - Use Paper's `Card` component with `mode="elevated"`
   - Use `expo-image` for poster display
   - Display: poster, title, vote_average (rating)
   - Add favorite indicator (star icon) if movie.favorite is true
   - Use `Pressable` or Card.onPress for tap handling
   - Memoize component with `React.memo` for FlatList performance
   - Style to match Android poster card design

2. Create `VideoCard.tsx`:
   - Props: `video: VideoDetails`, `onPress: (key: string) => void`
   - Use Paper's `Card` component
   - Display YouTube thumbnail using `image_url` field
   - Display video type (Trailer, Clip, etc.)
   - Horizontal layout for trailer carousel
   - Add play icon overlay on thumbnail

3. Create `ReviewCard.tsx`:
   - Props: `review: ReviewDetails`
   - Use Paper's `Card` component
   - Display author name prominently
   - Display review content (truncate if too long)
   - Add "Read more" expansion if content exceeds 3 lines
   - Vertical layout for scrolling list

4. Create `LoadingSpinner.tsx`:
   - Use Paper's `ActivityIndicator` component
   - Center on screen with overlay
   - Consistent styling across app
   - Optional message prop

5. Create `ErrorMessage.tsx`:
   - Props: `message: string`, `onRetry?: () => void`
   - Use Paper's `Banner` or `Card` with error styling
   - Display error icon and message
   - Optional retry button if `onRetry` provided

6. Add TypeScript prop types:
   - Define interfaces for all component props
   - Use strict typing (no `any`)
   - Export prop types for testing

7. Add styling:
   - Use StyleSheet.create for performance
   - Match Android app's card dimensions and spacing
   - Ensure responsive design (works on different screen sizes)

**Verification Checklist:**
- [ ] All five component files created
- [ ] Props are TypeScript typed
- [ ] Components use React Native Paper
- [ ] MovieCard uses expo-image
- [ ] Components are memoized where appropriate
- [ ] Styling matches Android app
- [ ] No PropTypes (use TypeScript instead)
- [ ] TypeScript compilation passes

**Testing Instructions:**

Create a test screen to render each component:

```typescript
// Test in app/index.tsx temporarily
const testMovie: MovieDetails = { /* ... */ };
const testVideo: VideoDetails = { /* ... */ };
const testReview: ReviewDetails = { /* ... */ };

<MovieCard movie={testMovie} onPress={(id) => console.log(id)} />
<VideoCard video={testVideo} onPress={(key) => console.log(key)} />
<ReviewCard review={testReview} />
<LoadingSpinner />
<ErrorMessage message="Test error" onRetry={() => {}} />
```

**Commit Message Template:**
```
feat(components): create reusable UI components

- Add MovieCard with poster, title, rating, favorite indicator
- Add VideoCard with YouTube thumbnail and play icon
- Add ReviewCard with author and expandable content
- Add LoadingSpinner and ErrorMessage utilities
- Use React Native Paper for Material Design
- Memoize components for performance
```

**Estimated Tokens:** ~6,000

---

### Task 3: Implement Home Screen (Movie Grid)

**Goal:** Build the main movie browsing screen with a staggered grid layout.

**Files to Modify:**
- `Migration/expo-project/app/index.tsx` - Home screen implementation

**Prerequisites:**
- Task 2 completed (MovieCard component created)
- Zustand movie store working (Phase 2)
- Understand Android's MainActivity.java and PosterRecycler.java

**Implementation Steps:**

1. Import required dependencies:
   - Import `useMovieStore` from src/store/movieStore
   - Import `MovieCard` component
   - Import `FlatList`, `RefreshControl` from react-native
   - Import `FAB` (Floating Action Button) from react-native-paper
   - Import `router` from expo-router

2. Set up state subscriptions:
   - Subscribe to `movies`, `loading`, `error` from movie store
   - Use Zustand selectors for performance (only re-render when needed)

3. Implement component lifecycle:
   - On mount (useEffect), load movies based on current filter
   - Call `movieStore.loadPopularMovies()` by default

4. Implement movie grid with FlatList:
   - Use `numColumns={2}` for 2-column grid (matches Android's StaggeredGridLayoutManager)
   - Set `keyExtractor={(item) => item.id.toString()}`
   - Implement `renderItem` with memoized MovieCard
   - Add performance optimizations:
     - `removeClippedSubviews={true}`
     - `maxToRenderPerBatch={10}`
     - `windowSize={5}`
     - `initialNumToRender={10}`

5. Implement pull-to-refresh:
   - Add `RefreshControl` to FlatList
   - On refresh, reload movies from API and database
   - Set refreshing state from store

6. Implement navigation to details:
   - MovieCard onPress navigates to `/details/${movieId}`
   - Use `router.push()` from expo-router

7. Add FAB for filter navigation:
   - Floating Action Button in bottom-right corner
   - Icon: filter/settings icon
   - On press, navigate to `/filter` screen
   - Matches Android's search/filter button

8. Handle loading and error states:
   - If loading, show `<LoadingSpinner />`
   - If error, show `<ErrorMessage />` with retry button
   - If no movies, show empty state message

9. Add header/title:
   - Use Stack.Screen options for title
   - Title: "Movies" or "Popular Movies" based on filter

**Verification Checklist:**
- [ ] Movie grid displays with 2 columns
- [ ] Movies load from store on mount
- [ ] Pull-to-refresh works
- [ ] Tap movie navigates to details screen
- [ ] FAB navigates to filter screen
- [ ] Loading state shows spinner
- [ ] Error state shows error message
- [ ] Empty state handled gracefully
- [ ] Performance is smooth (60 FPS scrolling)

**Testing Instructions:**

```bash
cd Migration/expo-project
npx expo start

# In app:
# 1. Should see movie grid with 2 columns
# 2. Scroll smoothly through movies
# 3. Pull down to refresh (should reload)
# 4. Tap a movie (should navigate to details)
# 5. Tap FAB (should navigate to filter)
# 6. Test with empty database (should show empty state)
# 7. Test with network error (should show error message)
```

**Commit Message Template:**
```
feat(screens): implement home screen with movie grid

- Add 2-column movie grid using FlatList
- Implement pull-to-refresh
- Add navigation to details screen
- Add FAB for filter navigation
- Handle loading, error, and empty states
- Optimize FlatList performance
- Match Android MainActivity functionality
```

**Estimated Tokens:** ~7,000

---

### Task 4: Implement Details Screen

**Goal:** Build the movie details screen showing full information, trailers, and reviews.

**Files to Modify:**
- `Migration/expo-project/app/details/[id].tsx` - Details screen implementation

**Prerequisites:**
- Task 2 completed (VideoCard and ReviewCard components)
- Task 3 completed (Home screen for navigation source)
- Understand Android's DetailsActivity.java

**Implementation Steps:**

1. Extract movie ID from route params:
   - Use `useLocalSearchParams<{ id: string }>()` from expo-router
   - Parse ID to number: `const movieId = parseInt(params.id)`

2. Set up local state and store subscriptions:
   - Local state: `movie`, `videos`, `reviews`, `loading`, `error`
   - Option A: Fetch directly from database in component
   - Option B: Create dedicated details store (more complex)
   - Recommend Option A for simplicity

3. Load movie data on mount:
   - useEffect(() => { loadMovieDetails(movieId) }, [movieId])
   - Query database for movie: `getMovieById(movieId)`
   - Query database for videos: `getVideosForMovie(movieId)`
   - Query database for reviews: `getReviewsForMovie(movieId)`
   - If not in database, fetch from API and save

4. Implement scrollable layout with ScrollView:
   - Use `ScrollView` as root container
   - Vertical layout matching Android DetailsActivity

5. Display movie header section:
   - Large poster image (expo-image)
   - Title (variant="headlineMedium")
   - Release date, vote average (rating), original language
   - Overview/plot text
   - Favorite button (icon toggle) in top-right

6. Implement favorite toggle:
   - Use Paper's `IconButton` with star icon
   - Gold star if favorite, white/gray if not
   - On press, call `movieStore.toggleFavorite(movieId)`
   - Optimistic UI update

7. Display trailers section:
   - Horizontal FlatList of VideoCards
   - Filter videos for type === 'Trailer'
   - Section title: "Trailers"
   - On VideoCard press, open YouTube (WebView or external)
   - Matches Android's horizontal RecyclerView for trailers

8. Display reviews section:
   - Vertical list of ReviewCards (can use map, not many reviews)
   - Section title: "Reviews"
   - If no reviews, show "No reviews yet" message
   - Matches Android's vertical RecyclerView for reviews

9. Handle loading and error states:
   - Show LoadingSpinner while fetching
   - Show ErrorMessage if movie not found or network error

10. Add back navigation:
    - Use Stack.Screen options for back button
    - Or use router.back() programmatically

**Verification Checklist:**
- [ ] Movie details load from database
- [ ] Poster, title, overview, rating displayed
- [ ] Favorite toggle works and updates database
- [ ] Trailers displayed in horizontal scrolling list
- [ ] Reviews displayed in vertical list
- [ ] Tap trailer opens YouTube (implement in Phase 4)
- [ ] Back navigation works
- [ ] Loading state shows spinner
- [ ] Error state handled (movie not found)

**Testing Instructions:**

```bash
cd Migration/expo-project
npx expo start

# In app:
# 1. Navigate to /details/550 (Fight Club, a known movie)
# 2. Should see movie details, poster, overview
# 3. Should see trailers (if in database)
# 4. Should see reviews (if in database)
# 5. Tap favorite button (should toggle)
# 6. Navigate back to home (favorite should persist)
# 7. Test with invalid ID (should show error)
```

**Commit Message Template:**
```
feat(screens): implement movie details screen

- Add movie header with poster, title, overview, rating
- Add favorite toggle button with database update
- Add horizontal trailers section with VideoCards
- Add vertical reviews section with ReviewCards
- Handle loading and error states
- Implement back navigation
- Match Android DetailsActivity functionality
```

**Estimated Tokens:** ~8,000

---

### Task 5: Implement Filter Screen

**Goal:** Build the filter/settings screen for toggling Popular, Top Rated, and Favorites.

**Files to Modify:**
- `Migration/expo-project/app/filter.tsx` - Filter screen implementation

**Prerequisites:**
- Task 4 completed (Details screen)
- Zustand filter store working (Phase 2)
- Understand Android's SearchFragment.java and SharedPreferences

**Implementation Steps:**

1. Import dependencies:
   - Import `useFilterStore` from src/store/filterStore
   - Import `useMovieStore` from src/store/movieStore
   - Import `Switch`, `List` from react-native-paper
   - Import `router` from expo-router

2. Subscribe to filter store state:
   - Get `showPopular`, `showTopRated`, `showFavorites` from store
   - Get toggle actions: `togglePopular`, `toggleTopRated`, `toggleFavorites`

3. Implement filter UI with Paper's List components:
   - Use `List.Section` for grouping
   - Three `List.Item` components with `Switch` controls
   - Item 1: "Show Popular Movies" with `showPopular` switch
   - Item 2: "Show Top Rated TV Shows" with `showTopRated` switch
   - Item 3: "Show Favorites" with `showFavorites` switch

4. Wire up toggle handlers:
   - Popular switch onValueChange → `togglePopular()`
   - Top Rated switch onValueChange → `toggleTopRated()`
   - Favorites switch onValueChange → `toggleFavorites()`

5. Trigger movie reload on filter change:
   - When any toggle changes, call appropriate load function:
     - If showPopular → `movieStore.loadPopularMovies()`
     - If showTopRated → `movieStore.loadTopRatedMovies()`
     - If showFavorites → `movieStore.loadFavoriteMovies()`
   - Use useEffect to watch filter changes

6. Add screen title and description:
   - Title: "Filter Movies"
   - Subtitle: "Choose which movies to display"
   - Use Stack.Screen options for title

7. Add back/close navigation:
   - Use Stack.Screen header with back button
   - When user navigates back, home screen should show updated results

8. Match Android SearchFragment design:
   - Similar checkbox/switch layout
   - Material Design styling
   - Clear and simple UI

**Verification Checklist:**
- [ ] Three filter switches displayed
- [ ] Switches reflect current filter state from store
- [ ] Toggle switches update store state
- [ ] Toggling filter triggers movie reload
- [ ] Navigate back to home shows filtered results
- [ ] UI matches Android SearchFragment aesthetic
- [ ] TypeScript compilation passes

**Testing Instructions:**

```bash
cd Migration/expo-project
npx expo start

# In app:
# 1. Start on home screen (shows popular movies)
# 2. Tap FAB to open filter screen
# 3. Toggle "Show Popular" off (should update store)
# 4. Toggle "Show Favorites" on (should update store)
# 5. Navigate back to home
# 6. Should see favorites instead of popular movies
# 7. Repeat with different filter combinations
```

**Commit Message Template:**
```
feat(screens): implement filter screen

- Add three filter switches (Popular, Top Rated, Favorites)
- Wire up switches to filter store
- Trigger movie reload on filter change
- Implement back navigation
- Match Android SearchFragment design
- Use React Native Paper List and Switch components
```

**Estimated Tokens:** ~5,000

---

### Task 6: Add Animations and Transitions

**Goal:** Implement smooth transitions and animations matching the Android app's scene transitions.

**Files to Modify:**
- `Migration/expo-project/app/_layout.tsx` - Add screen transition animations
- `Migration/expo-project/src/components/MovieCard.tsx` - Add press animation

**Prerequisites:**
- Task 5 completed (all screens implemented)
- Understand React Navigation animations
- Review Android's Explode transition and shared element transitions

**Implementation Steps:**

1. Add screen transition animations in `_layout.tsx`:
   - Configure Stack.Screen options for transitions
   - Use slide-from-right animation for details screen
   - Use slide-from-bottom animation for filter screen (modal-like)
   - Set animation duration to match Android (250-300ms)

2. Configure shared element transition for poster:
   - When tapping MovieCard, animate poster to details screen
   - Use react-native-shared-element library if needed
   - Or use simple scale/fade animation as fallback

3. Add press animation to MovieCard:
   - Use `Animated` API or `Pressable` with scale feedback
   - On press down: scale(0.95)
   - On press up: scale(1.0)
   - Duration: 150ms
   - Matches Android's ripple effect feel

4. Add fade-in animation for images:
   - Use expo-image's built-in transition prop
   - Set transition={200} for smooth fade-in when poster loads
   - Prevents harsh image popping

5. Add layout animation for filter changes:
   - When movies reload after filter change, animate FlatList update
   - Use LayoutAnimation or Reanimated
   - Fade out old movies, fade in new movies

6. Test animations on both platforms:
   - Verify animations work on iOS and Android
   - Ensure animations don't block UI (60 FPS maintained)
   - Check for jank or stuttering

**Verification Checklist:**
- [ ] Screen transitions animate smoothly
- [ ] MovieCard has press feedback animation
- [ ] Poster images fade in when loaded
- [ ] Filter changes animate movie grid updates
- [ ] Animations maintain 60 FPS
- [ ] No animation-related errors or warnings

**Testing Instructions:**

```bash
cd Migration/expo-project
npx expo start

# In app:
# 1. Navigate from home to details (should slide/fade smoothly)
# 2. Press back (should animate back)
# 3. Tap MovieCard (should scale down then up)
# 4. Open filter, change filter, go back (list should animate update)
# 5. Scroll quickly (images should fade in smoothly)
# 6. Check FPS with performance monitor (should be ~60 FPS)
```

**Commit Message Template:**
```
feat(animations): add screen transitions and press feedback

- Add slide transitions for screen navigation
- Add scale press feedback to MovieCard
- Add fade-in animation for poster images
- Add layout animation for filter changes
- Ensure 60 FPS performance
- Match Android scene transition feel
```

**Estimated Tokens:** ~4,000

---

### Task 7: Write Component Tests

**Goal:** Create component tests for all UI components using React Native Testing Library.

**Files to Create:**
- `Migration/expo-project/__tests__/components/MovieCard.test.tsx`
- `Migration/expo-project/__tests__/components/VideoCard.test.tsx`
- `Migration/expo-project/__tests__/components/ReviewCard.test.tsx`
- `Migration/expo-project/__tests__/screens/HomeScreen.test.tsx`

**Prerequisites:**
- Task 6 completed (all UI implemented)
- React Native Testing Library installed

**Implementation Steps:**

1. Set up test utilities:
   - Mock Zustand stores
   - Mock expo-router navigation
   - Mock expo-image component
   - Create test data fixtures (mock MovieDetails, VideoDetails, etc.)

2. Test `MovieCard.test.tsx`:
   - Test renders movie title, poster, rating
   - Test favorite indicator shows when favorite is true
   - Test onPress callback called with correct movie ID
   - Test accessibility labels
   - Test loading state for image

3. Test `VideoCard.test.tsx`:
   - Test renders video thumbnail
   - Test displays video type (Trailer, Clip)
   - Test onPress callback called with video key
   - Test play icon overlay visible

4. Test `ReviewCard.test.tsx`:
   - Test renders author and content
   - Test long content is truncated
   - Test "Read more" expansion works

5. Test `HomeScreen.test.tsx`:
   - Test renders movie grid
   - Test loading state shows spinner
   - Test error state shows error message
   - Test empty state shows message
   - Test pull-to-refresh triggers reload
   - Test navigation to details on movie press
   - Test FAB navigation to filter

6. Follow component testing best practices:
   - Test user-visible behavior, not implementation
   - Use `getByText`, `getByTestId` for queries
   - Use `fireEvent` for interactions
   - Assert on rendered output, not internal state

**Verification Checklist:**
- [ ] All component test files created
- [ ] Tests cover main component functionality
- [ ] Tests use React Native Testing Library
- [ ] Stores and navigation mocked properly
- [ ] All tests pass: `npm test`
- [ ] Coverage >70% for components

**Testing Instructions:**

```bash
cd Migration/expo-project

# Run component tests
npm test -- components

# Run screen tests
npm test -- screens

# Run with coverage
npm test -- --coverage components screens

# Coverage should be >70%
```

**Commit Message Template:**
```
test(components): add component and screen tests

- Add tests for MovieCard, VideoCard, ReviewCard
- Add tests for Home screen
- Mock Zustand stores and navigation
- Test user interactions and rendering
- Achieve >70% component coverage
```

**Estimated Tokens:** ~5,000

---

## Phase Verification

Before moving to Phase 4, verify all tasks are complete:

### Functional Verification
- [ ] All three screens accessible via navigation
- [ ] Home screen displays movie grid
- [ ] Details screen shows movie information
- [ ] Filter screen toggles work and update movie list
- [ ] All components render correctly
- [ ] Animations smooth and performant
- [ ] All tests pass

### UI/UX Verification
- [ ] Material Design aesthetic matches Android app
- [ ] 2-column movie grid looks good on different screen sizes
- [ ] Poster images load and display correctly
- [ ] Text is readable and properly sized
- [ ] Touch targets are appropriately sized
- [ ] Scrolling is smooth (60 FPS)

### Navigation Verification
- [ ] Can navigate from Home → Details
- [ ] Can navigate from Home → Filter → Home
- [ ] Back navigation works correctly
- [ ] Route params passed correctly to Details screen
- [ ] Deep links work (e.g., opening /details/123 directly)

### Code Quality Verification
- [ ] TypeScript strict mode passes
- [ ] ESLint shows no errors
- [ ] All components are properly typed
- [ ] No `any` types used
- [ ] Code follows Phase-0 patterns (memoization, etc.)

---

## Known Limitations & Technical Debt

**Introduced in this phase:**
- YouTube trailers don't open yet (clicking VideoCard does nothing)
- No actual data in database (components show empty states)
- Filter changes don't persist across app restarts
- No dark mode support yet

**To be addressed in later phases:**
- Phase 4 will connect API → Database → UI flow (populate data)
- Phase 4 will implement WebView for YouTube playback
- Phase 5 may add filter persistence with AsyncStorage
- Phase 5 may add dark mode support

---

## Next Steps

Phase 3 is complete! You now have:
- ✅ Full navigation with Expo Router
- ✅ All core screens implemented (Home, Details, Filter)
- ✅ Reusable Material Design components
- ✅ Smooth animations and transitions
- ✅ Comprehensive component tests
- ✅ UI foundation ready for data integration

**Ready for Phase 4?**
Proceed to **[Phase 4: Integration & Key Features](./Phase-4.md)** to connect the API, database, and UI layers into a complete working app.

---

**Estimated Total Tokens for Phase 4:** ~35,000
