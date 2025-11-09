# Android to React Native (Expo) Migration Plan

## Overview

This migration plan outlines the complete process of porting the **Movies Android (Java) application** to **React Native using Expo**. The original app is a TMDb (The Movie Database) client that showcases movies with local database persistence for favorites, reactive UI updates, and integration with YouTube for trailers.

The core architectural challenge is replacing Android-specific patterns (`Room` + `LiveData`) with React Native equivalents while maintaining the same functionality and user experience. This migration will create a cross-platform mobile application (iOS/Android) using modern React Native tooling and best practices.

The new React Native application will be built entirely within the `Migration/` directory, keeping the original Android codebase intact for reference. All development will follow a phased approach, with each phase representing a complete, testable milestone.

## Migration Strategy

**Tech Stack Decisions:**
- **Database:** `expo-sqlite` (direct SQL replacement for Room)
- **State Management:** Zustand (lightweight reactive state, replaces LiveData)
- **API Client:** Native `fetch` API (replaces OkHttp + Gson)
- **Image Loading:** `expo-image` (replaces Picasso)
- **Language:** TypeScript (type safety similar to Java)
- **UI Components:** React Native Paper (Material Design components)
- **Navigation:** Expo Router (file-based routing)
- **List Rendering:** FlatList (replaces RecyclerView)

## Prerequisites

Before starting implementation, ensure you have:

### Development Environment
- **Node.js:** v18.x or later
- **npm/yarn:** Latest stable version
- **Expo CLI:** `npm install -g expo-cli`
- **Git:** For version control
- **IDE/Editor:** VS Code with TypeScript and React Native extensions recommended

### API Keys
- **TMDb API Key:** Obtain from [https://www.themoviedb.org/settings/api](https://www.themoviedb.org/settings/api)
- **YouTube Data API Key:** Obtain from [Google Cloud Console](https://console.cloud.google.com/)

### Knowledge Requirements
- TypeScript fundamentals
- React and React Hooks
- React Native basics
- SQLite database concepts
- REST API consumption

### Testing Tools
- **Jest:** JavaScript testing framework (included with Expo)
- **React Native Testing Library:** Component testing
- **Expo development client:** For device testing

## Phase Summary

| Phase | Goal | Focus | Est. Tokens |
|-------|------|-------|-------------|
| **Phase 0** | Foundation & Architecture | ADRs, tech stack decisions, design patterns, testing strategy | N/A (Reference) |
| **Phase 1** | Project Setup & API Layer | Initialize Expo project, implement TMDb/YouTube API services, TypeScript models | ~25,000 |
| **Phase 2** | Database & State Management | Implement expo-sqlite schema, DAO-equivalent queries, Zustand stores | ~30,000 |
| **Phase 3** | Navigation & Core UI | Expo Router setup, home screen with movie grid, details screen, filter screen | ~35,000 |
| **Phase 4** | Integration & Features | Connect API → Database → UI, favorites functionality, WebView for YouTube | ~25,000 |
| **Phase 5** | Testing, Polish & Deployment | Unit tests, integration tests, animations, error handling, build configuration | ~20,000 |
| **Total** | | | **~135,000** |

## Phase Navigation

### Foundation
- **[Phase 0: Architecture & Design Decisions](./Phase-0.md)** - Read this first! Contains critical architecture decisions (ADRs), shared patterns, and testing strategy that apply to all phases.

### Implementation Phases
1. **[Phase 1: Project Setup & API Service Layer](./Phase-1.md)**
   - Expo project initialization with TypeScript
   - TMDb and YouTube API services
   - TypeScript interfaces and models
   - Environment configuration

2. **[Phase 2: Database & State Management](./Phase-2.md)**
   - expo-sqlite schema implementation (Room migration)
   - DAO-equivalent query functions
   - Zustand stores (LiveData replacement)
   - Database initialization and migrations

3. **[Phase 3: Navigation & Core UI Components](./Phase-3.md)**
   - Expo Router file-based navigation
   - Home screen with staggered movie grid
   - Movie details screen
   - Filter/settings screen
   - Reusable UI components

4. **[Phase 4: Integration & Key Features](./Phase-4.md)**
   - Connect API → Database → Zustand → UI data flow
   - Favorites toggle functionality
   - WebView-based YouTube trailer playback
   - Image loading and caching with expo-image
   - Error handling and loading states

5. **[Phase 5: Testing, Polish & Deployment](./Phase-5.md)**
   - Unit tests for API services and database queries
   - Component tests with React Native Testing Library
   - Integration tests for complete user flows
   - Animations and transitions
   - App icon and splash screen
   - Build configuration for Android/iOS

## Development Workflow

### Branch Strategy
All work is performed on the feature branch: `claude/android-to-react-native-migration-011CUvsPAiUwZedKSfnDYrAr`

### Commit Convention
Follow conventional commits format:
```
type(scope): brief description

- Detail 1
- Detail 2
```

**Types:** `feat`, `fix`, `refactor`, `test`, `docs`, `chore`

**Example:**
```
feat(api): implement TMDb movie discovery service

- Add getPopularMovies() method
- Add getTopRatedTV() method
- Include TypeScript response types
```

### Testing Strategy
- **TDD Approach:** Write tests before implementation when possible
- **Coverage Target:** Aim for >80% coverage on critical paths (API, database, state)
- **Test Types:**
  - Unit tests for utilities, API services, database queries
  - Component tests for UI components
  - Integration tests for complete features

### Code Quality
- **ESLint:** Enforce TypeScript and React best practices
- **Prettier:** Consistent code formatting
- **TypeScript:** Strict mode enabled for maximum type safety
- **DRY Principle:** Extract reusable logic into utilities
- **YAGNI Principle:** Implement only what's needed, avoid over-engineering

## Repository Structure

```
android-movies/                    # Root (existing Android project)
├── app/                          # Android Java source code (READ ONLY)
│   └── src/main/java/...
├── Migration/                    # New React Native project (ALL WORK HERE)
│   ├── docs/
│   │   └── plans/               # This migration plan
│   │       ├── README.md        # This file
│   │       ├── Phase-0.md
│   │       ├── Phase-1.md
│   │       ├── Phase-2.md
│   │       ├── Phase-3.md
│   │       ├── Phase-4.md
│   │       └── Phase-5.md
│   └── expo-project/            # Created in Phase 1
│       ├── app/                 # Expo Router screens
│       ├── src/                 # Application source
│       │   ├── api/
│       │   ├── database/
│       │   ├── store/
│       │   ├── components/
│       │   ├── models/
│       │   └── utils/
│       ├── assets/
│       ├── __tests__/
│       └── package.json
└── README.md                     # Original Android README
```

## Success Criteria

The migration is considered complete when:

1. ✅ **Functional Parity:** All features from the Android app work in React Native
   - Browse popular movies and top-rated TV shows
   - View movie details with trailers and reviews
   - Add/remove favorites with local persistence
   - Filter movies by Popular/Top Rated/Favorites

2. ✅ **Data Integrity:** Local database correctly stores and retrieves data
   - All three tables (movies, videos, reviews) implemented
   - Queries match original DAO functionality
   - Favorites persist across app restarts

3. ✅ **API Integration:** TMDb and YouTube APIs work correctly
   - Movie discovery
   - Video/trailer fetching
   - Review fetching
   - Thumbnail loading

4. ✅ **UI/UX Quality:** User interface matches or exceeds Android version
   - Smooth scrolling in movie grid
   - Fast image loading with caching
   - Proper loading and error states
   - Material Design consistency

5. ✅ **Code Quality:** Codebase is maintainable and well-tested
   - TypeScript strict mode with no `any` types
   - >80% test coverage on critical paths
   - ESLint/Prettier passing
   - No console errors or warnings

6. ✅ **Build Success:** App builds and runs on both platforms
   - Android APK/AAB builds successfully
   - iOS IPA builds successfully (if Mac available)
   - No runtime crashes

## Risk Mitigation

### Known Challenges
1. **Database Migration Complexity:** Room uses compile-time verification; expo-sqlite is runtime. Mitigation: Comprehensive tests for all queries.
2. **State Management:** Zustand is different from LiveData. Mitigation: Clear patterns in Phase-0, thorough testing.
3. **Image Performance:** FlatList with many images can be sluggish. Mitigation: Use expo-image optimization, proper memoization.
4. **API Rate Limits:** TMDb has rate limits. Mitigation: Implement caching, avoid redundant requests.

### Rollback Strategy
- Each phase is independently verifiable and committable
- Git history allows rollback to any phase
- Original Android app remains untouched in parent directories

## Next Steps

1. **Read Phase-0.md** - Understand architecture decisions and patterns
2. **Begin Phase-1** - Set up the Expo project and API layer
3. **Follow each phase sequentially** - Each phase builds on the previous
4. **Test continuously** - Verify each task before moving to the next
5. **Commit frequently** - Small, atomic commits with clear messages

## Questions or Issues?

If you encounter ambiguities or need clarification while implementing:
- Refer back to Phase-0.md for architectural guidance
- Check the original Android source code in `../app/src/main/java/`
- Review TMDb API documentation: [https://developers.themoviedb.org/3](https://developers.themoviedb.org/3)
- Review Expo documentation: [https://docs.expo.dev/](https://docs.expo.dev/)

---

**Ready to begin?** Start with **[Phase 0: Architecture & Design Decisions](./Phase-0.md)**
