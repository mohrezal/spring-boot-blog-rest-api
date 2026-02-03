package com.github.mohrezal.api.domains.users.commands.params;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenCommandParams {
    private String refreshToken;
    private String ipAddress;
    private String userAgent;
}
