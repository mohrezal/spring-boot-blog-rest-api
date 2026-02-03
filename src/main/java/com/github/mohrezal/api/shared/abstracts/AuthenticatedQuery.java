package com.github.mohrezal.api.shared.abstracts;

import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
import com.github.mohrezal.api.shared.interfaces.Query;

public abstract class AuthenticatedQuery<P extends AuthenticatedParams, R>
        extends AuthenticatedBase<P> implements Query<P, R> {}
