package com.github.mohrezal.api.domains.storage.controllers;

import com.github.mohrezal.api.config.Routes;
import com.github.mohrezal.api.domains.storage.commands.DeleteCommand;
import com.github.mohrezal.api.domains.storage.commands.UploadCommand;
import com.github.mohrezal.api.domains.storage.commands.UploadProfileCommand;
import com.github.mohrezal.api.domains.storage.commands.params.DeleteCommandParams;
import com.github.mohrezal.api.domains.storage.commands.params.UploadCommandParams;
import com.github.mohrezal.api.domains.storage.commands.params.UploadProfileCommandParams;
import com.github.mohrezal.api.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.api.domains.storage.dtos.UploadProfileRequest;
import com.github.mohrezal.api.domains.storage.dtos.UploadRequest;
import com.github.mohrezal.api.domains.storage.enums.StorageType;
import com.github.mohrezal.api.domains.storage.queries.GetStorageByFilenameQuery;
import com.github.mohrezal.api.domains.storage.queries.GetUserStorageListQuery;
import com.github.mohrezal.api.domains.storage.queries.params.GetStorageByFilenameQueryParams;
import com.github.mohrezal.api.domains.storage.queries.params.GetUserStorageListQueryParams;
import com.github.mohrezal.api.shared.annotations.IsAdminOrUser;
import com.github.mohrezal.api.shared.annotations.range.Range;
import com.github.mohrezal.api.shared.dtos.PageResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.Storage.BASE)
@RequiredArgsConstructor
@Tag(name = "Storage")
public class StorageController {

    private final ObjectProvider<@NonNull UploadCommand> uploadCommands;
    private final ObjectProvider<@NonNull DeleteCommand> deleteCommands;
    private final ObjectProvider<@NonNull UploadProfileCommand> uploadProfileCommands;

    private final ObjectProvider<@NonNull GetStorageByFilenameQuery> getStorageByFilenameQueries;
    private final ObjectProvider<@NonNull GetUserStorageListQuery> getUserStorageListQueries;

    @IsAdminOrUser
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<@NonNull StorageSummary> upload(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute UploadRequest uploadRequest) {
        var command = uploadCommands.getObject();
        var params = new UploadCommandParams(userDetails, uploadRequest, StorageType.MEDIA);

        command.validate(params);

        return ResponseEntity.status(HttpStatus.CREATED).body(command.execute(params));
    }

    @GetMapping(Routes.Storage.BY_FILENAME)
    public ResponseEntity<byte[]> download(@PathVariable String filename) {
        var query = getStorageByFilenameQueries.getObject();
        var params = new GetStorageByFilenameQueryParams(filename);

        var response = query.execute(params);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.mimeType()))
                .body(response.data());
    }

    @IsAdminOrUser
    @DeleteMapping(Routes.Storage.BY_FILENAME)
    public ResponseEntity<Void> deleteByFilename(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String filename) {
        var command = deleteCommands.getObject();
        var params = new DeleteCommandParams(userDetails, filename);
        command.execute(params);
        return ResponseEntity.noContent().build();
    }

    @IsAdminOrUser
    @GetMapping(Routes.Storage.LIST)
    public ResponseEntity<@NonNull PageResponse<StorageSummary>> list(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @Range(max = 1000) @RequestParam(defaultValue = "0") int page,
            @Valid @Range(min = 1, max = 20) @RequestParam(defaultValue = "10") int size) {
        var query = getUserStorageListQueries.getObject();
        var params = new GetUserStorageListQueryParams(userDetails, page, size);
        return ResponseEntity.ok().body(query.execute(params));
    }

    @IsAdminOrUser
    @PostMapping(value = Routes.Storage.PROFILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<@NonNull StorageSummary> profile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute UploadProfileRequest request) {
        var command = uploadProfileCommands.getObject();
        var params = new UploadProfileCommandParams(userDetails, request);

        command.validate(params);

        return ResponseEntity.ok().body(command.execute(params));
    }
}
