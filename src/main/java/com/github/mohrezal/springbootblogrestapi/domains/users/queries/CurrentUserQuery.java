package com.github.mohrezal.springbootblogrestapi.domains.users.queries;

import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.UserSummary;
import com.github.mohrezal.springbootblogrestapi.domains.users.mappers.UserMapper;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import com.github.mohrezal.springbootblogrestapi.domains.users.queries.params.CurrentUserQueryParams;
import com.github.mohrezal.springbootblogrestapi.shared.exceptions.types.ForbiddenException;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class CurrentUserQuery implements Query<CurrentUserQueryParams, UserSummary> {

    private final UserMapper userMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserSummary execute(CurrentUserQueryParams params) {
        if (!(params.getUserDetails() instanceof User currentUser)) {
            throw new ForbiddenException();
        }

        return userMapper.toUserSummary(currentUser);
    }
}
