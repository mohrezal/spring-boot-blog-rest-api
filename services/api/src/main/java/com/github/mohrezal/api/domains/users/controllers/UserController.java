package com.github.mohrezal.api.domains.users.controllers;

import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.domains.users.commands.FollowUserCommand;
import com.github.mohrezal.api.domains.users.commands.UnFollowUserCommand;
import com.github.mohrezal.api.domains.users.commands.UpdateUserProfileCommand;
import com.github.mohrezal.api.domains.users.commands.params.FollowUserCommandParams;
import com.github.mohrezal.api.domains.users.commands.params.UnFollowUserCommandParams;
import com.github.mohrezal.api.domains.users.dtos.FollowerSummary;
import com.github.mohrezal.api.domains.users.dtos.UserSummary;
import com.github.mohrezal.api.domains.users.queries.CurrentUserQuery;
import com.github.mohrezal.api.domains.users.queries.GetUserFollowersQuery;
import com.github.mohrezal.api.domains.users.queries.GetUserFollowingQuery;
import com.github.mohrezal.api.domains.users.queries.params.CurrentUserQueryParams;
import com.github.mohrezal.api.domains.users.queries.params.GetUserFollowersQueryParams;
import com.github.mohrezal.api.domains.users.queries.params.GetUserFollowingQueryParams;
import com.github.mohrezal.api.shared.annotations.IsAdminOrUser;
import com.github.mohrezal.api.shared.annotations.range.Range;
import com.github.mohrezal.api.shared.dtos.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
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

    private final CurrentUserQuery currentUserQueries;
    private final UpdateUserProfileCommand updateUserProfileCommands;
    private final FollowUserCommand followUserCommands;
    private final UnFollowUserCommand unFollowUserCommands;

    private final GetUserFollowersQuery getUserFollowersQueries;
    private final GetUserFollowingQuery getUserFollowingQueries;

    @IsAdminOrUser
    @GetMapping(Routes.User.ME)
    public ResponseEntity<@NonNull UserSummary> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        var params = new CurrentUserQueryParams(userDetails);
        var response = currentUserQueries.execute(params);
        return ResponseEntity.ok(response);
    }

    @IsAdminOrUser
    @PostMapping(Routes.User.FOLLOW_USER)
    public ResponseEntity<Void> followUser(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String handle) {
        var params = new FollowUserCommandParams(userDetails, handle);

        followUserCommands.execute(params);
        return ResponseEntity.ok().build();
    }

    @IsAdminOrUser
    @PostMapping(Routes.User.UNFOLLOW_USER)
    public ResponseEntity<Void> unFollowUser(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String handle) {
        var params = new UnFollowUserCommandParams(userDetails, handle);
        unFollowUserCommands.execute(params);
        return ResponseEntity.ok().build();
    }

    @IsAdminOrUser
    @GetMapping(Routes.User.FOLLOWERS)
    public ResponseEntity<@NonNull PageResponse<FollowerSummary>> getFollowers(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String handle,
            @Valid @Range(max = 1000) @RequestParam(defaultValue = "0") int page,
            @Valid @Range(min = 1, max = 20) @RequestParam(defaultValue = "20") int size) {

        var params = new GetUserFollowersQueryParams(userDetails, handle, page, size);

        var response = getUserFollowersQueries.execute(params);
        return ResponseEntity.ok(response);
    }

    @IsAdminOrUser
    @GetMapping(Routes.User.FOLLOWING)
    public ResponseEntity<@NonNull PageResponse<FollowerSummary>> getFollowing(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String handle,
            @Valid @Range(max = 1000) @RequestParam(defaultValue = "0") int page,
            @Valid @Range(min = 1, max = 20) @RequestParam(defaultValue = "20") int size) {

        var params = new GetUserFollowingQueryParams(userDetails, handle, page, size);
        var response = getUserFollowingQueries.execute(params);
        return ResponseEntity.ok(response);
    }
}
