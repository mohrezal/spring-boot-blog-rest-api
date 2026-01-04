package com.github.mohrezal.springbootblogrestapi.domains.users.controllers;

import com.github.mohrezal.springbootblogrestapi.config.Routes;
import com.github.mohrezal.springbootblogrestapi.domains.users.commands.UpdateUserProfileCommand;
import com.github.mohrezal.springbootblogrestapi.domains.users.commands.params.UpdateUserProfileCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.UpdateUserProfileRequest;
import com.github.mohrezal.springbootblogrestapi.domains.users.dtos.UserSummary;
import com.github.mohrezal.springbootblogrestapi.domains.users.queries.CurrentUserQuery;
import com.github.mohrezal.springbootblogrestapi.domains.users.queries.params.CurrentUserQueryParams;
import com.github.mohrezal.springbootblogrestapi.shared.annotations.IsAdminOrUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.User.BASE)
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final ObjectProvider<@NonNull CurrentUserQuery> currentUserQueries;
    private final ObjectProvider<@NonNull UpdateUserProfileCommand> updateUserProfileCommands;

    @IsAdminOrUser
    @GetMapping(Routes.User.ME)
    public ResponseEntity<@NonNull UserSummary> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        var params = CurrentUserQueryParams.builder().userDetails(userDetails).build();
        var response = currentUserQueries.getObject().execute(params);
        return ResponseEntity.ok(response);
    }

    @IsAdminOrUser
    @PatchMapping(Routes.User.ME)
    public ResponseEntity<@NonNull UserSummary> updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateUserProfileRequest request) {
        var params =
                UpdateUserProfileCommandParams.builder()
                        .userDetails(userDetails)
                        .request(request)
                        .build();

        var command = updateUserProfileCommands.getObject();
        command.validate(params);
        var response = command.execute(params);
        return ResponseEntity.ok(response);
    }
}
