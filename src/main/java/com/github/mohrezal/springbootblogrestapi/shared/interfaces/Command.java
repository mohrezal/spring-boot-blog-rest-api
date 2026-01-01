package com.github.mohrezal.springbootblogrestapi.shared.interfaces;

public interface Command<P, R> {
    R execute(P params);

    default void validate(P params) {}
}
