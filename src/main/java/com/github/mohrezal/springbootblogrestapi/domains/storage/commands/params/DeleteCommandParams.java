package com.github.mohrezal.springbootblogrestapi.domains.storage.commands.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteCommandParams {
    private UserDetails userDetails;
    private String fileName;
}
