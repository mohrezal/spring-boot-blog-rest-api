package com.github.mohrezal.api.domains.users.queries;

import com.github.mohrezal.api.domains.users.dtos.UserSummary;
import com.github.mohrezal.api.domains.users.mappers.UserMapper;
import com.github.mohrezal.api.domains.users.queries.params.CurrentUserQueryParams;
import com.github.mohrezal.api.shared.abstracts.AuthenticatedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class CurrentUserQuery extends AuthenticatedQuery<CurrentUserQueryParams, UserSummary> {

    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public UserSummary execute(CurrentUserQueryParams params) {
        validate(params);
        return userMapper.toUserSummary(user);
    }
}
