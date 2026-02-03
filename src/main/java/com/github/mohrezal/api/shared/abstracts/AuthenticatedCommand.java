package com.github.mohrezal.api.shared.abstracts;

import com.github.mohrezal.api.shared.interfaces.AuthenticatedParams;
import com.github.mohrezal.api.shared.interfaces.Command;

public abstract class AuthenticatedCommand<P extends AuthenticatedParams, R>
        extends AuthenticatedBase<P> implements Command<P, R> {}
