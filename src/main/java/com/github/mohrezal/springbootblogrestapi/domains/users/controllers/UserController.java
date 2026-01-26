package com.github.mohrezal.springbootblogrestapi.domains.users.controllers;

import com.github.mohrezal.springbootblogrestapi.config.Routes;
import com.github.mohrezal.springbootblogrestapi.domains.users.commands.FollowUserCommand;
import com.github.mohrezal.springbootblogrestapi.domains.users.commands.UnFollowUserCommand;
import com.github.mohrezal.springbootblogrestapi.domains.users.commands.UpdateUserProfileCommand;
import com.github.mohrezal.springbootblogrestapi.domains.users.commands.params.FollowUserCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.users.commands.params.UnFollowUserCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.FollowerSummary;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.UserSummary;
import com.github.mohrezal.springbootblogrestapi.domains.users.queries.CurrentUserQuery;
import com.github.mohrezal.springbootblogrestapi.domains.users.queries.GetUserFollowersQuery;
import com.github.mohrezal.springbootblogrestapi.domains.users.queries.GetUserFollowingQuery;
import com.github.mohrezal.springbootblogrestapi.domains.users.queries.params.CurrentUserQueryParams;
import com.github.mohrezal.springbootblogrestapi.domains.users.queries.params.GetUserFollowersQueryParams;
import com.github.mohrezal.springbootblogrestapi.domains.users.queries.params.GetUserFollowingQueryParams;
import com.github.mohrezal.springbootblogrestapi.shared.annotations.IsAdminOrUser;
import com.github.mohrezal.springbootblogrestapi.shared.dtos.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.User.BASE)
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final ObjectProvider<@NonNull CurrentUserQuery> currentUserQueries;
    private final ObjectProvider<@NonNull UpdateUserProfileCommand> updateUserProfileCommands;
    private final ObjectProvider<@NonNull FollowUserCommand> followUserCommands;
    private final ObjectProvider<@NonNull UnFollowUserCommand> unFollowUserCommands;

    private final ObjectProvider<@NonNull GetUserFollowersQuery> getUserFollowersQueries;
    private final ObjectProvider<@NonNull GetUserFollowingQuery> getUserFollowingQueries;

    @IsAdminOrUser
    @GetMapping(Routes.User.ME)
    public ResponseEntity<@NonNull UserSummary> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        var params = CurrentUserQueryParams.builder().userDetails(userDetails).build();
        var response = currentUserQueries.getObject().execute(params);
        return ResponseEntity.ok(response);
    }

    @IsAdminOrUser
    @PostMapping(Routes.User.FOLLOW_USER)
    public ResponseEntity<Void> followUser(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String handle) {
        var params =
                FollowUserCommandParams.builder().userDetails(userDetails).handle(handle).build();

        var command = followUserCommands.getObject();
        command.validate(params);
        command.execute(params);
        return ResponseEntity.ok().build();
    }

    @IsAdminOrUser
    @PostMapping(Routes.User.UNFOLLOW_USER)
    public ResponseEntity<Void> unFollowUser(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String handle) {
        var command = unFollowUserCommands.getObject();
        var params =
                UnFollowUserCommandParams.builder().userDetails(userDetails).handle(handle).build();

        command.execute(params);
        return ResponseEntity.ok().build();
    }

    @IsAdminOrUser
    @GetMapping(Routes.User.FOLLOWERS)
    public ResponseEntity<@NonNull PageResponse<FollowerSummary>> getFollowers(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String handle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var params =
                GetUserFollowersQueryParams.builder()
                        .userDetails(userDetails)
                        .handle(handle)
                        .page(page)
                        .size(size)
                        .build();

        var response = getUserFollowersQueries.getObject().execute(params);
        return ResponseEntity.ok(response);
    }

    @IsAdminOrUser
    @GetMapping(Routes.User.FOLLOWING)
    public ResponseEntity<@NonNull PageResponse<FollowerSummary>> getFollowing(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String handle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var params =
                GetUserFollowingQueryParams.builder()
                        .userDetails(userDetails)
                        .handle(handle)
                        .page(page)
                        .size(size)
                        .build();

        var response = getUserFollowingQueries.getObject().execute(params);
        return ResponseEntity.ok(response);
    }
}
