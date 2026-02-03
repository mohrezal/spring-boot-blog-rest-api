package com.github.mohrezal.api.shared.interfaces;

public interface Query<P, R> {
    R execute(P params);

    default void validate(P params) {}
}
