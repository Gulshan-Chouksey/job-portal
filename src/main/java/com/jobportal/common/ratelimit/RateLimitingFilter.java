package com.jobportal.common.ratelimit;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

    private final Map<String, RateLimitBucket> buckets = new ConcurrentHashMap<>();

    @Value("${rate-limit.max-requests:50}")
    private int maxRequests;

    @Value("${rate-limit.window-seconds:60}")
    private int windowSeconds;

    @Value("${rate-limit.enabled:true}")
    private boolean enabled;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientKey = getClientKey(request);
        RateLimitBucket bucket = buckets.computeIfAbsent(clientKey, k -> new RateLimitBucket(maxRequests));

        bucket.resetIfExpired(windowSeconds);

        if (bucket.tryConsume()) {
            response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(bucket.getRemaining()));
            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded for client: {}", clientKey);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"success\":false,\"message\":\"Too many requests. Please try again later.\",\"data\":null}");
        }
    }

    /** Clears all rate limit buckets. Used for testing. */
    public void resetBuckets() {
        buckets.clear();
    }

    private String getClientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    static class RateLimitBucket {
        private final int maxRequests;
        private final AtomicInteger remaining;
        private volatile long windowStart;

        RateLimitBucket(int maxRequests) {
            this.maxRequests = maxRequests;
            this.remaining = new AtomicInteger(maxRequests);
            this.windowStart = System.currentTimeMillis();
        }

        void resetIfExpired(int windowSeconds) {
            long now = System.currentTimeMillis();
            if (now - windowStart > windowSeconds * 1000L) {
                synchronized (this) {
                    if (now - windowStart > windowSeconds * 1000L) {
                        remaining.set(maxRequests);
                        windowStart = now;
                    }
                }
            }
        }

        boolean tryConsume() {
            return remaining.getAndDecrement() > 0;
        }

        int getRemaining() {
            int val = remaining.get();
            return Math.max(val, 0);
        }
    }
}
