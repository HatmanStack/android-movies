/**
 * Unit tests for errorHandler utility
 */

import {
  formatError,
  logError,
  isRetryableError,
  isNetworkError,
  ErrorSeverity,
} from '../../src/utils/errorHandler';
import { APIError, NetworkError, DatabaseError } from '../../src/api/errors';

describe('errorHandler - formatError', () => {
  it('should format APIError with status code 400', () => {
    const error = new APIError('Bad request', 400, '/api/movies');
    const formatted = formatError(error);

    expect(formatted.title).toBe('API Error');
    expect(formatted.message).toBe('Invalid request. Please try again.');
    expect(formatted.severity).toBe(ErrorSeverity.WARNING);
    expect(formatted.retryable).toBe(true);
  });

  it('should format APIError with status code 401', () => {
    const error = new APIError('Unauthorized', 401);
    const formatted = formatError(error);

    expect(formatted.message).toBe('Authentication required. Please check your API key.');
    expect(formatted.severity).toBe(ErrorSeverity.WARNING);
  });

  it('should format APIError with status code 403', () => {
    const error = new APIError('Forbidden', 403);
    const formatted = formatError(error);

    expect(formatted.message).toBe('Access denied. Please check your permissions.');
  });

  it('should format APIError with status code 404', () => {
    const error = new APIError('Not found', 404);
    const formatted = formatError(error);

    expect(formatted.message).toBe('The requested content was not found.');
    expect(formatted.retryable).toBe(false);
  });

  it('should format APIError with status code 429', () => {
    const error = new APIError('Too many requests', 429);
    const formatted = formatError(error);

    expect(formatted.message).toBe('Too many requests. Please wait a moment and try again.');
  });

  it('should format APIError with status code 500', () => {
    const error = new APIError('Internal server error', 500);
    const formatted = formatError(error);

    expect(formatted.message).toBe('Server error. Please try again later.');
    expect(formatted.severity).toBe(ErrorSeverity.ERROR);
  });

  it('should format APIError with status code 502', () => {
    const error = new APIError('Bad gateway', 502);
    const formatted = formatError(error);

    expect(formatted.message).toBe('Server error. Please try again later.');
  });

  it('should format APIError with status code 503', () => {
    const error = new APIError('Service unavailable', 503);
    const formatted = formatError(error);

    expect(formatted.message).toBe('Server error. Please try again later.');
  });

  it('should format APIError with unknown status code', () => {
    const error = new APIError('Some error', 418);
    const formatted = formatError(error);

    expect(formatted.message).toBe('Some error');
  });

  it('should format APIError without status code', () => {
    const error = new APIError('Custom error message');
    const formatted = formatError(error);

    expect(formatted.message).toBe('Custom error message');
  });

  it('should format NetworkError', () => {
    const error = new NetworkError('Network timeout');
    const formatted = formatError(error);

    expect(formatted.title).toBe('Network Error');
    expect(formatted.message).toBe('Unable to connect to the server. Please check your internet connection and try again.');
    expect(formatted.severity).toBe(ErrorSeverity.ERROR);
    expect(formatted.retryable).toBe(true);
  });

  it('should format DatabaseError', () => {
    const error = new DatabaseError('Query failed', 'SELECT * FROM movies');
    const formatted = formatError(error);

    expect(formatted.title).toBe('Database Error');
    expect(formatted.message).toBe('Failed to access local data. Please try restarting the app.');
    expect(formatted.severity).toBe(ErrorSeverity.CRITICAL);
    expect(formatted.retryable).toBe(true);
  });

  it('should format standard Error', () => {
    const error = new Error('Something broke');
    const formatted = formatError(error);

    expect(formatted.title).toBe('Error');
    expect(formatted.message).toBe('Something broke');
    expect(formatted.severity).toBe(ErrorSeverity.ERROR);
    expect(formatted.retryable).toBe(true);
  });

  it('should format unknown error types', () => {
    const error = 'string error';
    const formatted = formatError(error);

    expect(formatted.title).toBe('Unknown Error');
    expect(formatted.message).toBe('An unexpected error occurred. Please try again.');
    expect(formatted.severity).toBe(ErrorSeverity.ERROR);
    expect(formatted.retryable).toBe(true);
  });
});

describe('errorHandler - logError', () => {
  const originalEnv = process.env.NODE_ENV;
  const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

  afterEach(() => {
    consoleSpy.mockClear();
  });

  afterAll(() => {
    consoleSpy.mockRestore();
    process.env.NODE_ENV = originalEnv;
  });

  it('should log error in development mode', () => {
    const error = new Error('Test error');
    logError(error, 'test context');

    expect(consoleSpy).toHaveBeenCalled();
    const loggedData = consoleSpy.mock.calls[0][1];
    expect(loggedData).toHaveProperty('context', 'test context');
    expect(loggedData).toHaveProperty('severity');
    expect(loggedData).toHaveProperty('originalError', error);
  });

  it('should log error without context', () => {
    const error = new APIError('API failed', 500);
    logError(error);

    expect(consoleSpy).toHaveBeenCalled();
  });
});

describe('errorHandler - isRetryableError', () => {
  it('should return true for retryable errors', () => {
    expect(isRetryableError(new NetworkError('Timeout'))).toBe(true);
    expect(isRetryableError(new APIError('Server error', 500))).toBe(true);
    expect(isRetryableError(new Error('Generic error'))).toBe(true);
  });

  it('should return false for non-retryable errors', () => {
    expect(isRetryableError(new APIError('Not found', 404))).toBe(false);
  });
});

describe('errorHandler - isNetworkError', () => {
  it('should return true for NetworkError', () => {
    expect(isNetworkError(new NetworkError('Connection failed'))).toBe(true);
  });

  it('should return true for errors with network keywords', () => {
    expect(isNetworkError(new Error('Network timeout'))).toBe(true);
    expect(isNetworkError(new Error('Connection refused'))).toBe(true);
    expect(isNetworkError(new Error('Request timeout'))).toBe(true);
    expect(isNetworkError(new Error('Fetch failed'))).toBe(true);
  });

  it('should return false for non-network errors', () => {
    expect(isNetworkError(new Error('Database error'))).toBe(false);
    expect(isNetworkError(new APIError('Not found', 404))).toBe(false);
    expect(isNetworkError('string error')).toBe(false);
  });
});
