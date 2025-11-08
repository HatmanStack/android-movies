/**
 * Zustand Filter Store tests
 * Tests filter preference management
 */

import { useFilterStore } from '../../src/store/filterStore';

describe('FilterStore', () => {
  beforeEach(() => {
    // Reset store to initial state
    useFilterStore.setState({
      showPopular: true,
      showTopRated: true,
      showFavorites: false,
    });
  });

  describe('Initial State', () => {
    it('should have correct initial state', () => {
      const state = useFilterStore.getState();

      expect(state.showPopular).toBe(true);
      expect(state.showTopRated).toBe(true);
      expect(state.showFavorites).toBe(false);
    });
  });

  describe('togglePopular', () => {
    it('should toggle popular filter on', () => {
      useFilterStore.setState({ showPopular: false });

      const store = useFilterStore.getState();
      store.togglePopular();

      const state = useFilterStore.getState();
      expect(state.showPopular).toBe(true);
      expect(state.showTopRated).toBe(false); // Others turned off
      expect(state.showFavorites).toBe(false);
    });

    it('should toggle popular filter off', () => {
      useFilterStore.setState({ showPopular: true });

      const store = useFilterStore.getState();
      store.togglePopular();

      const state = useFilterStore.getState();
      expect(state.showPopular).toBe(false);
      expect(state.showTopRated).toBe(true); // Others turned on
      expect(state.showFavorites).toBe(true);
    });
  });

  describe('toggleTopRated', () => {
    it('should toggle top-rated filter on', () => {
      useFilterStore.setState({ showTopRated: false });

      const store = useFilterStore.getState();
      store.toggleTopRated();

      const state = useFilterStore.getState();
      expect(state.showTopRated).toBe(true);
      expect(state.showPopular).toBe(false); // Others turned off
      expect(state.showFavorites).toBe(false);
    });

    it('should toggle top-rated filter off', () => {
      useFilterStore.setState({ showTopRated: true });

      const store = useFilterStore.getState();
      store.toggleTopRated();

      const state = useFilterStore.getState();
      expect(state.showTopRated).toBe(false);
      expect(state.showPopular).toBe(true); // Others turned on
      expect(state.showFavorites).toBe(true);
    });
  });

  describe('toggleFavorites', () => {
    it('should toggle favorites filter on', () => {
      useFilterStore.setState({ showFavorites: false });

      const store = useFilterStore.getState();
      store.toggleFavorites();

      const state = useFilterStore.getState();
      expect(state.showFavorites).toBe(true);
      expect(state.showPopular).toBe(false); // Others turned off
      expect(state.showTopRated).toBe(false);
    });

    it('should toggle favorites filter off', () => {
      useFilterStore.setState({ showFavorites: true });

      const store = useFilterStore.getState();
      store.toggleFavorites();

      const state = useFilterStore.getState();
      expect(state.showFavorites).toBe(false);
      expect(state.showPopular).toBe(true); // Others turned on
      expect(state.showTopRated).toBe(true);
    });
  });

  describe('getActiveFilter', () => {
    it('should return "favorites" when showFavorites is true', () => {
      useFilterStore.setState({
        showPopular: false,
        showTopRated: false,
        showFavorites: true,
      });

      const store = useFilterStore.getState();
      const filter = store.getActiveFilter();

      expect(filter).toBe('favorites');
    });

    it('should return "toprated" when showTopRated is true', () => {
      useFilterStore.setState({
        showPopular: false,
        showTopRated: true,
        showFavorites: false,
      });

      const store = useFilterStore.getState();
      const filter = store.getActiveFilter();

      expect(filter).toBe('toprated');
    });

    it('should return "popular" when showPopular is true', () => {
      useFilterStore.setState({
        showPopular: true,
        showTopRated: false,
        showFavorites: false,
      });

      const store = useFilterStore.getState();
      const filter = store.getActiveFilter();

      expect(filter).toBe('popular');
    });

    it('should return "popular" as default when none selected', () => {
      useFilterStore.setState({
        showPopular: false,
        showTopRated: false,
        showFavorites: false,
      });

      const store = useFilterStore.getState();
      const filter = store.getActiveFilter();

      expect(filter).toBe('popular'); // Default fallback
    });

    it('should prioritize favorites when multiple are true', () => {
      useFilterStore.setState({
        showPopular: true,
        showTopRated: true,
        showFavorites: true,
      });

      const store = useFilterStore.getState();
      const filter = store.getActiveFilter();

      // Favorites has highest priority
      expect(filter).toBe('favorites');
    });
  });

  describe('setActiveFilter', () => {
    it('should set popular filter', () => {
      const store = useFilterStore.getState();
      store.setActiveFilter('popular');

      const state = useFilterStore.getState();
      expect(state.showPopular).toBe(true);
      expect(state.showTopRated).toBe(false);
      expect(state.showFavorites).toBe(false);
    });

    it('should set toprated filter', () => {
      const store = useFilterStore.getState();
      store.setActiveFilter('toprated');

      const state = useFilterStore.getState();
      expect(state.showPopular).toBe(false);
      expect(state.showTopRated).toBe(true);
      expect(state.showFavorites).toBe(false);
    });

    it('should set favorites filter', () => {
      const store = useFilterStore.getState();
      store.setActiveFilter('favorites');

      const state = useFilterStore.getState();
      expect(state.showPopular).toBe(false);
      expect(state.showTopRated).toBe(false);
      expect(state.showFavorites).toBe(true);
    });
  });

  describe('Exclusive Selection', () => {
    it('should only have one filter active at a time when toggling', () => {
      useFilterStore.setState({
        showPopular: true,
        showTopRated: false,
        showFavorites: false,
      });

      // Toggle to top-rated
      const store = useFilterStore.getState();
      store.setActiveFilter('toprated');

      const state = useFilterStore.getState();
      const activeCount =
        Number(state.showPopular) + Number(state.showTopRated) + Number(state.showFavorites);

      expect(activeCount).toBe(1); // Only one should be active
      expect(state.showTopRated).toBe(true);
    });
  });
});
