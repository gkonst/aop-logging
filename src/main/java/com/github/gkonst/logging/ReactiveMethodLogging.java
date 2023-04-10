package com.github.gkonst.logging;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

public class ReactiveMethodLogging {
    public static Object tryToAddReactiveLogging(Object result, Consumer<Object> logAfter) {
        if (result instanceof Mono<?>) {
            return ((Mono<?>) result).doOnSuccess(logAfter);
        } else if (result instanceof Flux<?>) {
            return ((Flux<?>) result).doOnComplete(() -> logAfter.accept(null));
        } else {
            logAfter.accept(result);
            return result;
        }
    }
}
