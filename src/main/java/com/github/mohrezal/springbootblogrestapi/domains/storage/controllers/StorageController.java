package com.github.mohrezal.springbootblogrestapi.domains.storage.controllers;

import com.github.mohrezal.springbootblogrestapi.config.Routes;
import com.github.mohrezal.springbootblogrestapi.domains.storage.commands.DeleteCommand;
import com.github.mohrezal.springbootblogrestapi.domains.storage.commands.UploadCommand;
import com.github.mohrezal.springbootblogrestapi.domains.storage.commands.UploadProfileCommand;
import com.github.mohrezal.springbootblogrestapi.domains.storage.commands.params.DeleteCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.storage.commands.params.UploadCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.storage.commands.params.UploadProfileCommandParams;
import com.github.mohrezal.springbootblogrestapi.domains.storage.dtos.StorageFileResponse;
import com.github.mohrezal.springbootblogrestapi.domains.storage.dtos.StorageSummary;
import com.github.mohrezal.springbootblogrestapi.domains.storage.dtos.UploadProfileRequest;
import com.github.mohrezal.springbootblogrestapi.domains.storage.dtos.UploadRequest;
import com.github.mohrezal.springbootblogrestapi.domains.storage.enums.StorageType;
import com.github.mohrezal.springbootblogrestapi.domains.storage.queries.GetStorageByFilenameQuery;
import com.github.mohrezal.springbootblogrestapi.domains.storage.queries.GetUserStorageListQuery;
import com.github.mohrezal.springbootblogrestapi.domains.storage.queries.params.GetStorageByFilenameQueryParams;
import com.github.mohrezal.springbootblogrestapi.domains.storage.queries.params.GetUserStorageListQueryParams;
import com.github.mohrezal.springbootblogrestapi.shared.annotations.IsAdminOrUser;
import com.github.mohrezal.springbootblogrestapi.shared.dtos.PageResponse;
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
        var params =
                UploadCommandParams.builder()
                        .uploadRequest(uploadRequest)
                        .userDetails(userDetails)
                        .type(StorageType.MEDIA)
                        .build();

        command.validate(params);

        return ResponseEntity.status(HttpStatus.CREATED).body(command.execute(params));
    }

    @GetMapping(Routes.Storage.BY_FILENAME)
    public ResponseEntity<byte[]> download(@PathVariable String filename) {
        var query = getStorageByFilenameQueries.getObject();
        var params = GetStorageByFilenameQueryParams.builder().filename(filename).build();

        StorageFileResponse response = query.execute(params);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.getMimeType()))
                .body(response.getData());
    }

    @IsAdminOrUser
    @DeleteMapping(Routes.Storage.BY_FILENAME)
    public ResponseEntity<Void> deleteByFilename(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String filename) {
        var command = deleteCommands.getObject();
        var params =
                DeleteCommandParams.builder().fileName(filename).userDetails(userDetails).build();
        command.execute(params);
        return ResponseEntity.noContent().build();
    }

    @IsAdminOrUser
    @GetMapping(Routes.Storage.LIST)
    public ResponseEntity<@NonNull PageResponse<StorageSummary>> list(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var query = getUserStorageListQueries.getObject();
        var params =
                GetUserStorageListQueryParams.builder()
                        .userDetails(userDetails)
                        .page(page)
                        .size(size)
                        .build();
        return ResponseEntity.ok().body(query.execute(params));
    }

    @IsAdminOrUser
    @PostMapping(value = Routes.Storage.PROFILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<@NonNull StorageSummary> profile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute UploadProfileRequest request) {
        var command = uploadProfileCommands.getObject();
        var params =
                UploadProfileCommandParams.builder()
                        .userDetails(userDetails)
                        .uploadProfileRequest(request)
                        .build();

        command.validate(params);

        return ResponseEntity.ok().body(command.execute(params));
    }
}
