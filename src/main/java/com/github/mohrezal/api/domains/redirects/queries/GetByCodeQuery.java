package com.github.mohrezal.api.domains.redirects.queries;

import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.domains.redirects.exceptions.types.RedirectNotFoundException;
import com.github.mohrezal.api.domains.redirects.queries.params.GetByCodeQueryParams;
import com.github.mohrezal.api.domains.redirects.services.store.RedirectStoreService;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.config.ApplicationProperties;
import com.github.mohrezal.api.shared.interfaces.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class GetByCodeQuery implements Query<GetByCodeQueryParams, String> {

    private final RedirectStoreService redirectStoreService;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final ApplicationProperties properties;

    @Transactional(readOnly = true)
    @Override
    public String execute(GetByCodeQueryParams params) {
        var redirect =
                redirectStoreService
                        .findByCode(params.code())
                        .orElseThrow(RedirectNotFoundException::new);

        switch (redirect.targetType()) {
            case POST -> {
                var post =
                        postRepository
                                .findById(redirect.targetId())
                                .orElseThrow(RedirectNotFoundException::new);
                return UriComponentsBuilder.fromUriString(properties.frontend().paths().base())
                        .path(properties.frontend().paths().posts())
                        .pathSegment(post.getSlug())
                        .build()
                        .toUriString();
            }
            case PROFILE -> {
                var user =
                        userRepository
                                .findById(redirect.targetId())
                                .orElseThrow(RedirectNotFoundException::new);
                return UriComponentsBuilder.fromUriString(properties.frontend().paths().base())
                        .path(properties.frontend().paths().users())
                        .pathSegment(user.getHandle())
                        .build()
                        .toUriString();
            }
            default -> throw new RedirectNotFoundException();
        }
    }
}
