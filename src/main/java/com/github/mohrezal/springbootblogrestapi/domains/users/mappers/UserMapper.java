package com.github.mohrezal.springbootblogrestapi.domains.users.mappers;

import com.github.mohrezal.springbootblogrestapi.domains.posts.dtos.AuthorSummary;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.RegisterUserRequest;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.UserSummary;
import com.github.mohrezal.springbootblogrestapi.domains.users.enums.UserRole;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "handle", source = "registerUser.handle")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "email", source = "registerUser.email")
    @Mapping(target = "firstName", source = "registerUser.firstName")
    @Mapping(target = "lastName", source = "registerUser.lastName")
    @Mapping(target = "bio", source = "registerUser.bio")
    @Mapping(target = "isVerified", constant = "false")
    @Mapping(target = "credentials", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    User toUser(RegisterUserRequest registerUser, UserRole role);

    UserSummary toUserSummary(User user);

    AuthorSummary toAuthorSummary(User user);
}
