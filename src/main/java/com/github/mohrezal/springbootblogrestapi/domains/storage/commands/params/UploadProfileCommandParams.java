package com.github.mohrezal.springbootblogrestapi.domains.storage.commands.params;

import com.github.mohrezal.springbootblogrestapi.domains.storage.dtos.UploadProfileRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UploadProfileCommandParams {
    private UserDetails userDetails;
    private UploadProfileRequest uploadProfileRequest;
}
