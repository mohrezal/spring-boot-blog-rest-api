package com.github.mohrezal.api.shared.services.sluggenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.mohrezal.api.shared.exceptions.types.UnexpectedException;
import org.junit.jupiter.api.Test;

class SlugGeneratorServiceImplTest {

    private final SlugGeneratorServiceImpl slugGeneratorService = new SlugGeneratorServiceImpl();

    @Test
    void getSlug_whenTextIsNullOrBlank_shouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> slugGeneratorService.getSlug(null, slug -> false));
        assertThrows(
                IllegalArgumentException.class,
                () -> slugGeneratorService.getSlug("", slug -> false));
        assertThrows(
                IllegalArgumentException.class,
                () -> slugGeneratorService.getSlug("   ", slug -> false));
    }

    @Test
    void getSlug_whenSimpleText_shouldReturnLowercaseHyphenatedSlug() {
        String result = slugGeneratorService.getSlug("Hello World", slug -> false);

        assertEquals("hello-world", result);
    }

    @Test
    void getSlug_whenUppercaseText_shouldReturnLowercase() {
        String result = slugGeneratorService.getSlug("HELLO", slug -> false);

        assertEquals("hello", result);
    }

    @Test
    void getSlug_whenTextWithNumbers_shouldPreserveNumbers() {
        String result = slugGeneratorService.getSlug("Post 123", slug -> false);

        assertEquals("post-123", result);
    }

    @Test
    void getSlug_whenTextWithUnderscores_shouldReplaceWithHyphens() {
        String result = slugGeneratorService.getSlug("hello_world", slug -> false);

        assertEquals("hello-world", result);
    }

    @Test
    void getSlug_whenTextWithSpecialChars_shouldRemoveThem() {
        String result = slugGeneratorService.getSlug("Hello @World!", slug -> false);

        assertEquals("hello-world", result);
    }

    @Test
    void getSlug_whenTextWithOnlySpecialChars_shouldReturnRandomSlug() {
        String result = slugGeneratorService.getSlug("@#$%", slug -> false);

        assertEquals(20, result.length());
    }

    @Test
    void getSlug_whenTextWithAccentedChars_shouldNormalize() {
        String result = slugGeneratorService.getSlug("Café résumé", slug -> false);

        assertEquals("cafe-resume", result);
    }

    @Test
    void getSlug_whenTextWithSpaces_shouldNormalizeCorrectly() {
        assertEquals("hello-world", slugGeneratorService.getSlug("hello   world", slug -> false));
        assertEquals(
                "hello-world", slugGeneratorService.getSlug(" hello   world  ", slug -> false));
    }

    @Test
    void getSlug_whenTextWithMultipleHyphens_shouldCollapseToSingle() {
        String result = slugGeneratorService.getSlug("hello---world", slug -> false);

        assertEquals("hello-world", result);
    }

    @Test
    void getSlug_whenSlugDoesNotExist_shouldReturnBaseSlug() {
        String result = slugGeneratorService.getSlug("hello", slug -> false);

        assertEquals("hello", result);
    }

    @Test
    void getSlug_whenSlugAlwaysExists_shouldThrowUnexpectedException() {
        assertThrows(
                UnexpectedException.class,
                () -> slugGeneratorService.getSlug("hello", slug -> true));
    }
}
