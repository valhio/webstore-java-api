package com.github.valhio.storeapi.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.Map;

/*
    The LoginAttemptService class is a service class that is used to keep track of the number of failed login attempts
    for a user and lock the user out if the number of failed login attempts exceeds a threshold.
    This class uses the Guava cache to store the number of failed login attempts.
    Guava cache is a caching library that provides an in-memory cache implementation that supports
    automatic loading of entries and expiration of entries based on various criteria.

    The LoginAttemptService class has a constructor that initializes the attemptsCache cache with some default values.
    The cache is created using the CacheBuilder class, which allows
    the maximum size of the cache and the expiration time of entries in the cache to be configured.
    The cache is initialized with a CacheLoader that loads entries into the cache when they are requested.

    The LoginAttemptService class has the following methods: loginSucceeded, loginFailed, isBlocked, getAttemptsCache and clearCache.
    • The loginSucceeded method is used to reset the number of failed login attempts for a user when the user successfully logs in.
    • The loginFailed method is used to increment the number of failed login attempts for a user when the user fails to log in.
    • The isBlocked method is used to check if a user is locked out. If the number of failed login attempts
    exceeds the maximum number of attempts, the user is locked out.
    • The getAttemptsCache method is used to get the number of failed login attempts for all users.
    • The clearCache method is used to clear the cache.

    In summary:
    This class is used to keep track of the number of failed login attempts for a user
    and lock the user out if the number of failed login attempts exceeds a threshold.
*/

/*
   The LoadingCache interface is a subtype of the Cache interface that supports automatic loading of new values into the cache.
   The LoadingCache interface is used to create a cache that automatically loads new values into the cache when requested.
   This is useful for caches that do not have an obvious or easy way to pre-populate, such as caches that load values from a database or web service.
 */

@Service
public class LoginAttemptService {
    private static final int MAX_ATTEMPT = 5;
    private static final int ATTEMPT_INCREMENT = 1;
    // LoadingCache is a Guava cache that automatically loads new values into the cache when requested.
    // This is useful for caches that do not have an obvious or easy way to pre-populate, such as caches that load values from a database or web service.
    private final LoadingCache<String, Integer> attemptsCache;

    // The CacheBuilder class is used to create instances of LoadingCache.
    // The maximum size of the cache is set to 100.
    // The cache will evict entries that are least recently used.
    // The cache will expire after 15 minutes.
    // The CacheLoader is used to load new values into the cache when requested.
    public LoginAttemptService() {
        super();
        this.attemptsCache = CacheBuilder.newBuilder()
                .maximumSize(100) // In case the cache reaches its maximum size, the cache will evict entries that are least recently used.
                .expireAfterWrite(15, java.util.concurrent.TimeUnit.MINUTES)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String username) throws Exception {
                        return 0;
                    }
                });
    }

    // This method is used to reset the number of failed login attempts for a user.
    public void loginSucceeded(String username) {
        attemptsCache.invalidate(username);
    }

    // This method is used to increment the number of failed login attempts for a user.
    public void loginFailed(String username) {
        int attempts = 0;
        try {
            attempts = ATTEMPT_INCREMENT + attemptsCache.get(username);
        } catch (Exception e) {
            attempts = ATTEMPT_INCREMENT;
        }
        attemptsCache.put(username, attempts);
    }

    // This method is used to check if a user is locked out.
    public boolean isBlocked(String username) {
        try {
            return attemptsCache.get(username) >= MAX_ATTEMPT;
        } catch (Exception e) {
            return false;
        }
    }

    // This method is used to get the number of failed login attempts for a user.
    public Map<String, Integer> getAttemptsCache() {
        return attemptsCache.asMap();
    }

    // This method is used to clear the cache.
    public void clearCache() {
        attemptsCache.invalidateAll();
    }

}
