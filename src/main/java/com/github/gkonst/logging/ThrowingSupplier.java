package com.github.gkonst.logging;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Throwable;
}
