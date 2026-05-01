package com.github.mohrezal.api.domains.posts.queries;

import com.github.mohrezal.api.domains.posts.dtos.SlugAvailability;
import com.github.mohrezal.api.domains.posts.exceptions.types.PostSlugFormatException;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostSlugAvailabilityQueryParams;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.shared.constants.RegexUtils;
import com.github.mohrezal.api.shared.interfaces.Query;
import com.github.mohrezal.api.shared.services.sluggenerator.SlugGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetPostSlugAvailabilityQuery
        implements Query<GetPostSlugAvailabilityQueryParams, SlugAvailability> {
    private final PostRepository postRepository;
    private final SlugGeneratorService slugGeneratorService;

    @Override
    public void validate(GetPostSlugAvailabilityQueryParams params) {
        if (!params.slug().matches(RegexUtils.SLUG_PATTERN)) {
            throw new PostSlugFormatException();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public SlugAvailability execute(GetPostSlugAvailabilityQueryParams params) {
        validate(params);
        if (postRepository.existsBySlug(params.slug())) {
            return new SlugAvailability(
                    false,
                    slugGeneratorService.getSlug(params.slug(), postRepository::existsBySlug));
        }

        log.info("Get post slug availability query successful.");
        return new SlugAvailability(true, null);
    }
}
