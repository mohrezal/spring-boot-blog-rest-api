package com.github.mohrezal.api.shared.interfaces;

public interface Command<P, R> {
    R execute(P params);

    default void validate(P params) {}
}
