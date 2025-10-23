package org.learning.retry;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.function.Supplier;

public class RetryImpl {

    private final Integer maxAttempt;
    private final Integer waitDurationInMillis;
    private RetryConfig retryConfig;
    private RetryRegistry retryRegistry;
    private Retry retry;

    public RetryImpl() {
        this.maxAttempt = 3;
        this.waitDurationInMillis = 500;
    }

    private RetryConfig getRetryConfig() {
        if (retryConfig == null) {
            retryConfig = RetryConfig.custom()
                    .retryExceptions(SocketTimeoutException.class)
                    .maxAttempts(maxAttempt)
                    .waitDuration(Duration.ofMillis(waitDurationInMillis))
                    .build();
        }
        return retryConfig;
    }

    private RetryRegistry getRetryRegistry() {
        var config = getRetryConfig();
        if (retryRegistry == null) {
            retryRegistry = RetryRegistry.of(config);
        }
        return retryRegistry;
    }

    private Retry getRetry() {
        var registry = getRetryRegistry();
        if (retry == null) {
            retry = registry.retry("WireMock");
        }
        return retry;
    }

    public <O> O executeWithRetry(Supplier<O> call) {
        var retry = getRetry();
        return retry.decorateSupplier(call).get();
    }
}
