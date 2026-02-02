package com.github.mohrezal.springbootblogrestapi.shared.abstracts;

import com.github.mohrezal.springbootblogrestapi.shared.interfaces.AuthenticatedParams;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Command;

public abstract class AuthenticatedCommand<P extends AuthenticatedParams, R>
        extends AuthenticatedBase<P> implements Command<P, R> {}
