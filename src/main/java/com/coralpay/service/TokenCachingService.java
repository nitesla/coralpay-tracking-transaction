package com.coralpay.service;


import com.coralpay.model.ICacheManager;
import com.coralpay.utils.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service
public class TokenCachingService {

    private static final int KEY_SIZE = 40;
    private static Object apiAuthTokenCache;

    public final ICacheManager cacheManager;

    @Value("${token.time.to.leave}")
    long tokenTimeToLeave;

    public TokenCachingService(ICacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void init() {
        apiAuthTokenCache = cacheManager.createCache(Utility.APP_NAME, "TokenCache", tokenTimeToLeave);
    }

    public void put(String token, Object data) {

        this.cacheManager.put(apiAuthTokenCache, token.substring(0, KEY_SIZE), data);
    }

    public String get(String token) {

        return this.cacheManager.get(apiAuthTokenCache, token.substring(0, KEY_SIZE)).toString();
    }

    public boolean isTokenPresent(String token) {

        return this.cacheManager.isPresent(apiAuthTokenCache, token.substring(0, KEY_SIZE));
    }

    public void purgeExpiredElements() {

        this.cacheManager.purgeExpiredElements(apiAuthTokenCache);
    }

    public Authentication retrieve(String token) {
        return (Authentication) this.cacheManager.get(apiAuthTokenCache, token.substring(0, KEY_SIZE));
    }

    public boolean remove(String key) {

        return this.cacheManager.remove(apiAuthTokenCache, key.substring(0, KEY_SIZE));
    }
}

