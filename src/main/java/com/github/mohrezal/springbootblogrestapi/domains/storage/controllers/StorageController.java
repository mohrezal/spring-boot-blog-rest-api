package com.github.mohrezal.springbootblogrestapi.domains.storage.controllers;

import com.github.mohrezal.springbootblogrestapi.config.Routes;
import com.github.mohrezal.springbootblogrestapi.domains.storage.commands.UploadCommand;
import com.github.mohrezal.springbootblogrestapi.domains.storage.commands.params.UploadCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.storage.dtos.UploadRequest;
import com.github.mohrezal.springbootblogrestapi.domains.storage.dtos.UploadResponse;
import com.github.mohrezal.springbootblogrestapi.shared.annotations.IsAdminOrUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.Storage.BASE)
@RequiredArgsConstructor
@Tag(name = "Storage")
public class StorageController {

    private final ObjectProvider<@NonNull UploadCommand> uploadCommands;

    @IsAdminOrUser
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<@NonNull UploadResponse> upload(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute UploadRequest uploadRequest) {
        var command = uploadCommands.getObject();
        var params =
                UploadCommandParams.builder()
                        .uploadRequest(uploadRequest)
                        .userDetails(userDetails)
                        .build();

        command.validate(params);

        return ResponseEntity.status(HttpStatus.CREATED).body(command.execute(params));
    }
}
