package com.github.mohrezal.springbootblogrestapi.shared.abstracts;

import com.github.mohrezal.springbootblogrestapi.shared.interfaces.AuthenticatedParams;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Query;

public abstract class AuthenticatedQuery<P extends AuthenticatedParams, R>
        extends AuthenticatedBase<P> implements Query<P, R> {}
