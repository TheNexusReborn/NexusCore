package com.thenexusreborn.nexuscore.util;

/**
 * A callback that returns something
 * @param <T> The type for the check
 * @param <R> The value that returns
 */
public interface ReturnableCallback<T, R> {
    R callback(T t);
}
