# Phase 0: Architecture & Design Decisions

**Status:** Foundation (Reference Document)
**Applies To:** All implementation phases

---

## Purpose

This phase establishes the architectural foundation for the entire migration. Read this document **before** starting any implementation phase. It contains:
- Architecture Decision Records (ADRs)
- Shared patterns and conventions
- Testing strategy
- Common pitfalls and how to avoid them

**This is a reference document, not an implementation phase.** Refer back to it frequently during development.

---

## Architecture Decision Records (ADRs)

### ADR-001: Database Strategy - expo-sqlite

**Context:**
The Android app uses Room (an abstraction over SQLite) with compile-time query verification. React Native needs a similar local database solution.

**Decision:**
Use `expo-sqlite` for direct SQL database access.

**Rationale:**
- ✅ **Direct Room Equivalent:** expo-sqlite is SQLite, just like Room underneath
- ✅ **SQL Query Preservation:** Can migrate Room queries directly with minimal changes
- ✅ **Mature & Stable:** Well-supported by Expo team
- ✅ **No Learning Curve:** Standard SQL syntax
- ✅ **Relational Support:** Handles our 3-table schema with foreign keys

**Alternatives Considered:**
- **WatermelonDB:** More opinionated, built-in reactivity, but different schema definition and migration overhead
- **Async Storage:** Key-value only, unsuitable for relational data

**Implementation Patterns:**

```typescript
// Database initialization (one-time)
const db = SQLite.openDatabaseSync('movies.db');

// Execute schema creation
db.execSync(`
  CREATE TABLE IF NOT EXISTS movie_details (
    id INTEGER PRIMARY KEY,
    title TEXT,
    favorite INTEGER DEFAULT 0
    -- etc.
  );
`);

// Query execution (async)
const movies = await db.getAllAsync('SELECT * FROM movie_details WHERE favorite = 1');

// Prepared statements (safe from SQL injection)
const movie = await db.getFirstAsync(
  'SELECT * FROM movie_details WHERE id = ?',
  [movieId]
);
```

**Migration Strategy:**
1. Convert Room `@Entity` classes → TypeScript interfaces
2. Convert Room schema → SQLite `CREATE TABLE` statements
3. Convert `@Dao` methods → Async query functions
4. Map Java types → SQLite types (boolean → INTEGER, String → TEXT)

---

### ADR-002: State Management - Zustand

**Context:**
The Android app uses LiveData for reactive UI updates. When data changes in Room, observers automatically re-render UI components.

**Decision:**
Use Zustand for global state management, replacing LiveData.

**Rationale:**
- ✅ **Lightweight:** ~1KB, minimal API surface
- ✅ **No Boilerplate:** Simpler than Redux, MobX
- ✅ **TypeScript Support:** Full type safety
- ✅ **Reactivity:** Components auto-update when store changes
- ✅ **DevTools:** Works with Redux DevTools
- ✅ **No Context Providers:** Less React tree pollution

**Alternatives Considered:**
- **React Context + useState:** Boilerplate-heavy, performance issues with frequent updates
- **Redux:** Overkill for this app size, too much boilerplate
- **MobX:** More magic, steeper learning curve

**Implementation Pattern:**

```typescript
// Define store with TypeScript
interface MovieStore {
  movies: MovieDetails[];
  loading: boolean;
  error: string | null;

  // Actions
  setMovies: (movies: MovieDetails[]) => void;
  addMovie: (movie: MovieDetails) => void;
  toggleFavorite: (movieId: number) => void;
  loadPopularMovies: () => Promise<void>;
}

// Create store
const useMovieStore = create<MovieStore>((set, get) => ({
  movies: [],
  loading: false,
  error: null,

  setMovies: (movies) => set({ movies }),

  addMovie: (movie) => set((state) => ({
    movies: [...state.movies, movie]
  })),

  toggleFavorite: async (movieId) => {
    const movie = get().movies.find(m => m.id === movieId);
    if (!movie) return;

    // Update database
    await toggleFavoriteInDB(movieId, !movie.favorite);

    // Update store
    set((state) => ({
      movies: state.movies.map(m =>
        m.id === movieId ? { ...m, favorite: !m.favorite } : m
      )
    }));
  },

  loadPopularMovies: async () => {
    set({ loading: true, error: null });
    try {
      const movies = await queryPopularMovies(); // From database
      set({ movies, loading: false });
    } catch (error) {
      set({ error: error.message, loading: false });
    }
  }
}));

// Usage in components
function MovieList() {
  const { movies, loading, loadPopularMovies } = useMovieStore();

  useEffect(() => {
    loadPopularMovies();
  }, []);

  if (loading) return <ActivityIndicator />;

  return <FlatList data={movies} renderItem={...} />;
}
```

**Key Pattern: Store as LiveData Replacement**
- LiveData observers → Zustand selectors
- LiveData.setValue() → Zustand set()
- ViewModel + LiveData → Zustand store

---

### ADR-003: API Client - Native fetch

**Context:**
The Android app uses OkHttp + Gson for HTTP requests and JSON parsing.

**Decision:**
Use native `fetch` API with TypeScript for type-safe responses.

**Rationale:**
- ✅ **Zero Dependencies:** Built into React Native
- ✅ **Promise-based:** Works naturally with async/await
- ✅ **Sufficient for Use Case:** Simple GET requests to TMDb and YouTube
- ✅ **TypeScript Integration:** Easy to type responses

**Alternatives Considered:**
- **axios:** More features (interceptors, better errors), but adds dependency. Our use case is simple enough for fetch.

**Implementation Patterns:**

```typescript
// API service base pattern
class TMDbService {
  private static readonly BASE_URL = 'https://api.themoviedb.org/3';
  private static readonly API_KEY = process.env.EXPO_PUBLIC_TMDB_API_KEY;

  static async get<T>(endpoint: string, params: Record<string, string> = {}): Promise<T> {
    const url = new URL(`${this.BASE_URL}${endpoint}`);
    url.searchParams.append('api_key', this.API_KEY!);

    Object.entries(params).forEach(([key, value]) => {
      url.searchParams.append(key, value);
    });

    const response = await fetch(url.toString());

    if (!response.ok) {
      throw new APIError(
        `API request failed: ${response.statusText}`,
        response.status
      );
    }

    return response.json();
  }

  static async getPopularMovies(): Promise<TMDbDiscoverResponse> {
    return this.get<TMDbDiscoverResponse>('/discover/movie', {
      sort_by: 'popularity.desc'
    });
  }
}
```

**Error Handling:**
```typescript
// Custom API error class
class APIError extends Error {
  constructor(
    message: string,
    public statusCode?: number,
    public endpoint?: string
  ) {
    super(message);
    this.name = 'APIError';
  }
}

// Usage with try-catch
try {
  const movies = await TMDbService.getPopularMovies();
} catch (error) {
  if (error instanceof APIError) {
    console.error(`API Error ${error.statusCode}: ${error.message}`);
  } else {
    console.error('Network error:', error);
  }
}
```

---

### ADR-004: Image Loading - expo-image

**Context:**
The Android app uses Picasso for efficient image loading with automatic caching. React Native needs similar performance for grid layouts with many images.

**Decision:**
Use `expo-image` for all image loading.

**Rationale:**
- ✅ **Performance:** Better caching and memory management than `<Image>`
- ✅ **Built-in Placeholders:** Loading and error states
- ✅ **Optimized for Lists:** Works well with FlatList
- ✅ **Expo Integration:** First-class Expo support
- ✅ **Picasso-level Caching:** Comparable performance

**Implementation Pattern:**

```typescript
import { Image } from 'expo-image';

// Movie poster with placeholder
<Image
  source={{ uri: TMDbService.getPosterUrl(movie.poster_path) }}
  placeholder={require('../../assets/placeholder.png')}
  contentFit="cover"
  transition={200}
  style={styles.poster}
/>

// Optimized for FlatList
const MovieCard = memo(({ movie }: { movie: MovieDetails }) => (
  <Image
    source={{ uri: TMDbService.getPosterUrl(movie.poster_path) }}
    cachePolicy="memory-disk" // Cache aggressively
    recyclingKey={movie.id.toString()} // Reuse image views
    contentFit="cover"
    style={styles.poster}
  />
));
```

---

### ADR-005: Language - TypeScript (Strict Mode)

**Context:**
The Android app is written in Java (strongly typed). React Native supports both JavaScript and TypeScript.

**Decision:**
Use TypeScript with strict mode enabled.

**Rationale:**
- ✅ **Type Safety:** Catch errors at compile time, similar to Java
- ✅ **Better IDE Support:** Autocomplete, refactoring, inline docs
- ✅ **Self-Documenting:** Types show data structures clearly
- ✅ **Easier Migration:** Java developers familiar with types
- ✅ **Industry Standard:** Modern React Native best practice

**TypeScript Configuration:**

```json
// tsconfig.json
{
  "compilerOptions": {
    "strict": true,
    "noImplicitAny": true,
    "strictNullChecks": true,
    "strictFunctionTypes": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noImplicitReturns": true
  }
}
```

**Type Mapping from Java:**

| Java Type | TypeScript Type | Notes |
|-----------|----------------|-------|
| `int` | `number` | JavaScript has one number type |
| `float` | `number` | Same as int |
| `boolean` | `boolean` | Direct mapping |
| `String` | `string` | Lowercase in TS |
| `List<T>` | `T[]` or `Array<T>` | Array syntax |
| `void` | `void` | Direct mapping |
| `null` | `null` | Direct mapping |
| Custom class | `interface` or `type` | TS uses structural typing |

---

### ADR-006: UI Components - React Native Paper

**Context:**
The Android app uses Material Design components (Cards, Buttons, FABs). React Native needs a UI component library.

**Decision:**
Use React Native Paper for Material Design 3 components.

**Rationale:**
- ✅ **Material Design:** Matches Android app's look and feel
- ✅ **Comprehensive:** Cards, Buttons, Dialogs, Snackbars, etc.
- ✅ **Theming:** Easy dark mode support
- ✅ **Expo Compatible:** Works out of the box
- ✅ **Active Maintenance:** Well-supported library

**Implementation Patterns:**

```typescript
import { Card, Button, FAB } from 'react-native-paper';

// Movie card component
<Card mode="elevated">
  <Card.Cover source={{ uri: posterUrl }} />
  <Card.Content>
    <Text variant="titleLarge">{movie.title}</Text>
    <Text variant="bodyMedium">{movie.overview}</Text>
  </Card.Content>
  <Card.Actions>
    <Button onPress={handleFavorite}>Favorite</Button>
  </Card.Actions>
</Card>

// Theme configuration
const theme = {
  ...MD3LightTheme,
  colors: {
    ...MD3LightTheme.colors,
    primary: '#6200EE',
    secondary: '#03DAC6',
  },
};

<PaperProvider theme={theme}>
  <App />
</PaperProvider>
```

---

### ADR-007: Navigation - Expo Router

**Context:**
The Android app uses Activities with Intent-based navigation. React Native needs a navigation solution.

**Decision:**
Use Expo Router for file-based routing.

**Rationale:**
- ✅ **Type-Safe:** TypeScript navigation with autocompletion
- ✅ **File-Based:** Routes defined by file structure (like Next.js)
- ✅ **Deep Linking:** Automatic URL support
- ✅ **Modern:** Latest Expo recommended approach
- ✅ **Built on React Navigation:** Mature foundation

**File Structure → Routes Mapping:**

```
app/
├── _layout.tsx           → Root layout
├── index.tsx             → / (Home - Movie Grid)
├── details/
│   └── [id].tsx          → /details/:id (Movie Details)
└── filter.tsx            → /filter (Filter Screen)
```

**Navigation Patterns:**

```typescript
// Navigate to details (Activity Intent equivalent)
import { router } from 'expo-router';

router.push(`/details/${movieId}`);

// Navigate back
router.back();

// Get route params
import { useLocalSearchParams } from 'expo-router';

function DetailsScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  // ...
}
```

---

## Shared Patterns & Conventions

### 1. Data Flow Architecture

```
┌─────────────────┐
│   TMDb/YouTube  │  (External APIs)
│      APIs       │
└────────┬────────┘
         │
         ↓
┌─────────────────┐
│  API Services   │  (src/api/tmdb.ts, youtube.ts)
│   (TypeScript)  │  - Typed responses
└────────┬────────┘  - Error handling
         │
         ↓
┌─────────────────┐
│  expo-sqlite    │  (src/database/queries.ts)
│   (Local DB)    │  - Persistence layer
└────────┬────────┘  - CRUD operations
         │
         ↓
┌─────────────────┐
│ Zustand Stores  │  (src/store/movieStore.ts)
│ (State Mgmt)    │  - Global state
└────────┬────────┘  - Business logic
         │
         ↓
┌─────────────────┐
│ React Components│  (app/*.tsx, src/components/*.tsx)
│   (UI Layer)    │  - Presentation
└─────────────────┘  - User interaction
```

**Key Principles:**
- **Separation of Concerns:** API, database, state, and UI are independent layers
- **Unidirectional Data Flow:** Data flows down, events flow up
- **Single Source of Truth:** Zustand stores are the source of truth for UI state
- **Database as Cache:** Local SQLite caches API responses

### 2. Error Handling Strategy

**Layered Error Handling:**

```typescript
// Layer 1: API Service (throw specific errors)
class TMDbService {
  static async getPopularMovies(): Promise<TMDbDiscoverResponse> {
    try {
      const response = await fetch(url);
      if (!response.ok) throw new APIError('Failed to fetch', response.status);
      return response.json();
    } catch (error) {
      if (error instanceof APIError) throw error;
      throw new NetworkError('Network request failed');
    }
  }
}

// Layer 2: Store (catch and set error state)
const useMovieStore = create<MovieStore>((set) => ({
  error: null,

  loadMovies: async () => {
    try {
      const data = await TMDbService.getPopularMovies();
      set({ movies: data.results, error: null });
    } catch (error) {
      set({ error: error.message });
    }
  }
}));

// Layer 3: Component (display error to user)
function MovieList() {
  const { movies, error } = useMovieStore();

  if (error) {
    return (
      <Snackbar visible onDismiss={...}>
        {error}
      </Snackbar>
    );
  }

  return <FlatList data={movies} />;
}
```

**Custom Error Types:**
```typescript
class APIError extends Error {
  constructor(message: string, public statusCode?: number) {
    super(message);
    this.name = 'APIError';
  }
}

class DatabaseError extends Error {
  constructor(message: string, public query?: string) {
    super(message);
    this.name = 'DatabaseError';
  }
}

class NetworkError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'NetworkError';
  }
}
```

### 3. File Organization

```
Migration/expo-project/
├── app/                          # Expo Router screens
│   ├── _layout.tsx              # Root layout (theme, providers)
│   ├── index.tsx                # Home screen
│   ├── details/
│   │   └── [id].tsx             # Dynamic route for movie details
│   └── filter.tsx               # Filter/settings screen
│
├── src/
│   ├── api/                      # API service layer
│   │   ├── tmdb.ts              # TMDb API client
│   │   ├── youtube.ts           # YouTube API client
│   │   └── types.ts             # API response types
│   │
│   ├── database/                 # Database layer
│   │   ├── init.ts              # Database initialization & schema
│   │   ├── queries.ts           # DAO-equivalent query functions
│   │   └── migrations.ts        # Schema version management
│   │
│   ├── store/                    # Zustand state stores
│   │   ├── movieStore.ts        # Movie state management
│   │   ├── filterStore.ts       # Filter preferences
│   │   └── types.ts             # Store types
│   │
│   ├── components/               # Reusable UI components
│   │   ├── MovieCard.tsx
│   │   ├── VideoCard.tsx
│   │   ├── ReviewCard.tsx
│   │   └── LoadingSpinner.tsx
│   │
│   ├── models/                   # TypeScript interfaces
│   │   └── types.ts             # Domain models (MovieDetails, etc.)
│   │
│   └── utils/                    # Utility functions
│       ├── dateFormatter.ts
│       └── constants.ts
│
├── assets/                       # Static assets
│   ├── images/
│   └── fonts/
│
├── __tests__/                    # Test files (mirrors src structure)
│   ├── api/
│   ├── database/
│   ├── store/
│   └── components/
│
└── app.json                      # Expo configuration
```

**Naming Conventions:**
- **Files:** PascalCase for components (`MovieCard.tsx`), camelCase for utilities (`dateFormatter.ts`)
- **Components:** PascalCase (`MovieCard`)
- **Functions:** camelCase (`getPopularMovies`)
- **Constants:** UPPER_SNAKE_CASE (`TMDB_BASE_URL`)
- **Interfaces/Types:** PascalCase (`MovieDetails`)

### 4. Component Patterns

**Functional Components with Hooks (Always):**
```typescript
// ✅ Correct: Functional component with TypeScript
interface MovieCardProps {
  movie: MovieDetails;
  onPress: (id: number) => void;
}

export const MovieCard: FC<MovieCardProps> = ({ movie, onPress }) => {
  const handlePress = useCallback(() => {
    onPress(movie.id);
  }, [movie.id, onPress]);

  return (
    <Pressable onPress={handlePress}>
      <Card>
        <Image source={{ uri: movie.poster_path }} />
        <Text>{movie.title}</Text>
      </Card>
    </Pressable>
  );
};

// ❌ Wrong: Class components (deprecated pattern)
class MovieCard extends React.Component { ... }
```

**Performance Optimization:**
```typescript
// Memoize expensive components
export const MovieCard = memo<MovieCardProps>(({ movie, onPress }) => {
  // Component implementation
}, (prevProps, nextProps) => {
  // Custom comparison: only re-render if movie.id changes
  return prevProps.movie.id === nextProps.movie.id;
});

// Memoize callbacks
const handlePress = useCallback(() => {
  navigation.navigate('Details', { id: movie.id });
}, [movie.id, navigation]);

// Memoize computed values
const formattedDate = useMemo(() => {
  return formatDate(movie.release_date);
}, [movie.release_date]);
```

### 5. Testing Patterns

**Unit Test Structure:**
```typescript
// __tests__/api/tmdb.test.ts
describe('TMDbService', () => {
  describe('getPopularMovies', () => {
    it('should fetch popular movies successfully', async () => {
      // Arrange
      global.fetch = jest.fn().mockResolvedValue({
        ok: true,
        json: async () => mockTMDbResponse
      });

      // Act
      const result = await TMDbService.getPopularMovies();

      // Assert
      expect(result.results).toHaveLength(20);
      expect(result.results[0]).toHaveProperty('title');
    });

    it('should throw APIError on failed request', async () => {
      // Arrange
      global.fetch = jest.fn().mockResolvedValue({
        ok: false,
        status: 401
      });

      // Act & Assert
      await expect(TMDbService.getPopularMovies()).rejects.toThrow(APIError);
    });
  });
});
```

**Component Test Structure:**
```typescript
// __tests__/components/MovieCard.test.tsx
import { render, fireEvent } from '@testing-library/react-native';

describe('MovieCard', () => {
  const mockMovie: MovieDetails = {
    id: 1,
    title: 'Test Movie',
    // ...
  };

  it('should render movie title', () => {
    const { getByText } = render(<MovieCard movie={mockMovie} onPress={jest.fn()} />);
    expect(getByText('Test Movie')).toBeTruthy();
  });

  it('should call onPress with movie ID when pressed', () => {
    const onPress = jest.fn();
    const { getByTestId } = render(<MovieCard movie={mockMovie} onPress={onPress} />);

    fireEvent.press(getByTestId('movie-card'));
    expect(onPress).toHaveBeenCalledWith(1);
  });
});
```

---

## Common Pitfalls & Solutions

### Pitfall 1: Async Database Operations Not Awaited

**Problem:**
```typescript
// ❌ Wrong: Not awaiting database operation
function loadMovies() {
  db.getAllAsync('SELECT * FROM movie_details'); // Promise not awaited!
  setMovies(movies); // movies is undefined
}
```

**Solution:**
```typescript
// ✅ Correct: Always await async operations
async function loadMovies() {
  const movies = await db.getAllAsync('SELECT * FROM movie_details');
  setMovies(movies);
}
```

### Pitfall 2: SQL Injection Vulnerability

**Problem:**
```typescript
// ❌ Wrong: String interpolation in SQL query
const movies = await db.getAllAsync(
  `SELECT * FROM movie_details WHERE id = ${movieId}` // VULNERABLE!
);
```

**Solution:**
```typescript
// ✅ Correct: Use prepared statements
const movies = await db.getAllAsync(
  'SELECT * FROM movie_details WHERE id = ?',
  [movieId]
);
```

### Pitfall 3: FlatList Performance Issues

**Problem:**
```typescript
// ❌ Wrong: No optimization
<FlatList
  data={movies}
  renderItem={({ item }) => <MovieCard movie={item} />}
/>
```

**Solution:**
```typescript
// ✅ Correct: Optimize with keyExtractor and memoization
const renderItem = useCallback(({ item }: { item: MovieDetails }) => (
  <MovieCard movie={item} onPress={handlePress} />
), [handlePress]);

<FlatList
  data={movies}
  renderItem={renderItem}
  keyExtractor={(item) => item.id.toString()}
  removeClippedSubviews={true}
  maxToRenderPerBatch={10}
  windowSize={5}
/>
```

### Pitfall 4: Not Handling Loading and Error States

**Problem:**
```typescript
// ❌ Wrong: No loading or error handling
function MovieList() {
  const { movies } = useMovieStore();
  return <FlatList data={movies} />;
}
```

**Solution:**
```typescript
// ✅ Correct: Handle all states
function MovieList() {
  const { movies, loading, error } = useMovieStore();

  if (loading) return <ActivityIndicator />;
  if (error) return <ErrorMessage message={error} />;
  if (movies.length === 0) return <EmptyState />;

  return <FlatList data={movies} />;
}
```

### Pitfall 5: Zustand Store Mutations

**Problem:**
```typescript
// ❌ Wrong: Mutating state directly
const useMovieStore = create<MovieStore>((set) => ({
  movies: [],
  addMovie: (movie) => {
    movies.push(movie); // MUTATING STATE!
    set({ movies });
  }
}));
```

**Solution:**
```typescript
// ✅ Correct: Immutable updates
const useMovieStore = create<MovieStore>((set) => ({
  movies: [],
  addMovie: (movie) => set((state) => ({
    movies: [...state.movies, movie] // New array
  }))
}));
```

---

## Testing Strategy

### Coverage Targets

| Layer | Target Coverage | Priority |
|-------|----------------|----------|
| API Services | >90% | High |
| Database Queries | >90% | High |
| Zustand Stores | >80% | High |
| UI Components | >70% | Medium |
| Utilities | >90% | High |
| Screens/Routes | >50% | Low |

### Test Types

**1. Unit Tests**
- **Scope:** Individual functions, API services, database queries
- **Tools:** Jest
- **Run Frequency:** On every commit
- **Example:** Testing `TMDbService.getPopularMovies()` with mocked fetch

**2. Component Tests**
- **Scope:** UI components in isolation
- **Tools:** React Native Testing Library
- **Run Frequency:** On every commit
- **Example:** Testing MovieCard renders correctly and handles press events

**3. Integration Tests**
- **Scope:** Complete user flows (e.g., browse movies → view details → add favorite)
- **Tools:** React Native Testing Library + mocked APIs/DB
- **Run Frequency:** Before merging to main
- **Example:** Full flow from loading movies to displaying in UI

**4. E2E Tests (Optional)**
- **Scope:** Real app on device/simulator
- **Tools:** Detox or Maestro
- **Run Frequency:** Before releases
- **Example:** Complete app flow on real device

### Test Organization

```
__tests__/
├── api/
│   ├── tmdb.test.ts
│   └── youtube.test.ts
├── database/
│   ├── queries.test.ts
│   └── init.test.ts
├── store/
│   └── movieStore.test.ts
├── components/
│   ├── MovieCard.test.tsx
│   ├── VideoCard.test.tsx
│   └── ReviewCard.test.tsx
└── integration/
    └── movieFlow.test.tsx
```

### Mocking Strategies

**Mock fetch for API tests:**
```typescript
global.fetch = jest.fn().mockResolvedValue({
  ok: true,
  json: async () => mockResponse
});
```

**Mock database for store tests:**
```typescript
jest.mock('../database/queries', () => ({
  queryPopularMovies: jest.fn().mockResolvedValue(mockMovies)
}));
```

**Mock Zustand for component tests:**
```typescript
jest.mock('../store/movieStore', () => ({
  useMovieStore: () => ({
    movies: mockMovies,
    loading: false,
    error: null
  })
}));
```

---

## Code Quality Tools

### ESLint Configuration

```json
// .eslintrc.js
module.exports = {
  extends: [
    'expo',
    'plugin:@typescript-eslint/recommended',
    'plugin:react-hooks/recommended'
  ],
  rules: {
    '@typescript-eslint/no-explicit-any': 'error',
    '@typescript-eslint/explicit-function-return-type': 'warn',
    'react-hooks/exhaustive-deps': 'warn',
    'no-console': ['warn', { allow: ['warn', 'error'] }]
  }
};
```

### Prettier Configuration

```json
// .prettierrc
{
  "semi": true,
  "trailingComma": "es5",
  "singleQuote": true,
  "printWidth": 100,
  "tabWidth": 2
}
```

---

## Migration Checklist

Use this checklist to ensure architectural decisions are followed:

### Database Layer
- [ ] All queries use prepared statements (no SQL injection)
- [ ] All database operations are async/await
- [ ] Schema matches Room entities exactly
- [ ] Foreign key relationships preserved
- [ ] REPLACE conflict strategy used for upserts

### State Management
- [ ] Zustand stores use immutable updates
- [ ] Loading/error states included in all stores
- [ ] Store actions are async when needed
- [ ] No direct state mutations

### API Layer
- [ ] All API responses are TypeScript typed
- [ ] Error handling for network failures
- [ ] Error handling for non-200 responses
- [ ] API keys stored in environment variables
- [ ] No hardcoded URLs

### UI Components
- [ ] All components are functional (no class components)
- [ ] Props are TypeScript typed
- [ ] Callbacks are memoized with useCallback
- [ ] Expensive computations use useMemo
- [ ] List items are memoized with React.memo

### Testing
- [ ] Unit tests for all API services
- [ ] Unit tests for all database queries
- [ ] Component tests for key UI components
- [ ] Integration test for main user flow
- [ ] All tests passing before commit

### Code Quality
- [ ] No TypeScript `any` types
- [ ] ESLint passing with no warnings
- [ ] Prettier formatting applied
- [ ] No console.log in production code
- [ ] Meaningful variable/function names

---

## Next Steps

You've completed reading Phase 0! You now understand:
- ✅ Why expo-sqlite was chosen for database
- ✅ Why Zustand was chosen for state management
- ✅ How data flows through the application
- ✅ Common pitfalls and how to avoid them
- ✅ Testing strategy and coverage targets

**Ready to implement?**
Proceed to **[Phase 1: Project Setup & API Service Layer](./Phase-1.md)**

---

**Questions?** Refer back to this document whenever you need architectural guidance during implementation.
