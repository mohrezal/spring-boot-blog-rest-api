package com.github.mohrezal.springbootblogrestapi.domains.storage.queries.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetUserStorageListQueryParams {
    private UserDetails userDetails;
    private int size;
    private int page;
}
