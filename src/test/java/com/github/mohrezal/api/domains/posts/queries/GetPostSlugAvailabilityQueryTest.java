package com.github.mohrezal.api.domains.posts.queries;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.github.mohrezal.api.domains.posts.exceptions.types.PostSlugFormatException;
import com.github.mohrezal.api.domains.posts.queries.params.GetPostSlugAvailabilityQueryParams;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.shared.services.sluggenerator.SlugGeneratorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetPostSlugAvailabilityQueryTest {

    @Mock private PostRepository postRepository;

    @Mock private SlugGeneratorService slugGeneratorService;

    @InjectMocks private GetPostSlugAvailabilityQuery query;

    @Test
    void validate_whenSlugIsInvalid_shouldThrowException() {
        var params = new GetPostSlugAvailabilityQueryParams("invalid slug!!!");

        assertThrows(PostSlugFormatException.class, () -> query.validate(params));
    }

    @Test
    void validate_whenSlugIsValid_shouldNotThrow() {
        var params = new GetPostSlugAvailabilityQueryParams("new-post");
        assertDoesNotThrow(() -> query.validate(params));
    }

    @Test
    void execute_whenSlugIsAvailable_shouldReturnAvailableTrue() {
        var params = new GetPostSlugAvailabilityQueryParams("new-post");

        when(postRepository.existsBySlug(eq(params.slug()))).thenReturn(false);

        var result = query.execute(params);
        assertTrue(result.available());
        assertNull(result.suggestion());
    }

    @Test
    void execute_whenSlugAlreadyExists_shouldReturnUnavailableWithSuggestion() {
        var params = new GetPostSlugAvailabilityQueryParams("new-post");

        when(postRepository.existsBySlug(params.slug())).thenReturn(true);
        when(slugGeneratorService.getSlug(eq(params.slug()), any())).thenReturn("new-post-1");

        var result = query.execute(params);

        assertFalse(result.available());
        assertNotNull(result.suggestion());
        assertEquals("new-post-1", result.suggestion());
    }
}
