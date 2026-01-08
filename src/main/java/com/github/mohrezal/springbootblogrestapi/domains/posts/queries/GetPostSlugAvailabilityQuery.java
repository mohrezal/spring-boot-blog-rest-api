package com.github.mohrezal.springbootblogrestapi.domains.posts.queries;

import com.github.mohrezal.springbootblogrestapi.domains.posts.dtos.SlugAvailability;
import com.github.mohrezal.springbootblogrestapi.domains.posts.exceptions.types.PostSlugFormatException;
import com.github.mohrezal.springbootblogrestapi.domains.posts.queries.params.GetPostSlugAvailabilityQueryParams;
import com.github.mohrezal.springbootblogrestapi.domains.posts.repositories.PostRepository;
import com.github.mohrezal.springbootblogrestapi.shared.constants.RegexUtils;
import com.github.mohrezal.springbootblogrestapi.shared.interfaces.Query;
import com.github.mohrezal.springbootblogrestapi.shared.services.sluggenerator.SlugGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Slf4j
public class GetPostSlugAvailabilityQuery
        implements Query<GetPostSlugAvailabilityQueryParams, SlugAvailability> {
    private final PostRepository postRepository;
    private final SlugGeneratorService slugGeneratorService;

    @Override
    public void validate(GetPostSlugAvailabilityQueryParams params) {
        if (!params.getSlug().matches(RegexUtils.SLUG_PATTERN)) {
            throw new PostSlugFormatException();
        }
    }

    @Override
    public SlugAvailability execute(GetPostSlugAvailabilityQueryParams params) {
        validate(params);
        SlugAvailability response = new SlugAvailability();
        response.setAvailable(true);
        if (postRepository.existsBySlug(params.getSlug())) {
            response.setAvailable(false);
            response.setSuggestion(
                    slugGeneratorService.getSlug(params.getSlug(), postRepository::existsBySlug));
        }

        return response;
    }
}
