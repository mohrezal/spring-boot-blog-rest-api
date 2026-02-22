package com.github.mohrezal.api.shared.services.seeder;

import com.github.mohrezal.api.domains.categories.models.Category;
import com.github.mohrezal.api.domains.categories.repositories.CategoryRepository;
import com.github.mohrezal.api.domains.posts.enums.PostStatus;
import com.github.mohrezal.api.domains.posts.models.Post;
import com.github.mohrezal.api.domains.posts.repositories.PostRepository;
import com.github.mohrezal.api.domains.redirects.enums.RedirectTargetType;
import com.github.mohrezal.api.domains.redirects.models.Redirect;
import com.github.mohrezal.api.domains.redirects.repositories.RedirectRepository;
import com.github.mohrezal.api.domains.users.enums.UserRole;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.models.UserCredentials;
import com.github.mohrezal.api.domains.users.repositories.UserCredentialsRepository;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.services.sluggenerator.SlugGeneratorService;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("seed")
public class Seeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final PostRepository postRepository;
    private final RedirectRepository redirectRepository;

    private final SlugGeneratorService slugGeneratorService;

    private final PlatformTransactionManager transactionManager;
    private final PasswordEncoder passwordEncoder;
    private final ConfigurableApplicationContext applicationContext;

    private final Faker faker = new Faker();

    private static final String AVATAR_API = "https://api.dicebear.com/7.x/";

    @Override
    public void run(String... args) throws Exception {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);

        tx.execute(
                status -> {
                    int categoryCount = getArgumentValue(args, "--category", 0);
                    int userCount = getArgumentValue(args, "--user", 0);
                    int postCount = getArgumentValue(args, "--post", 0);

                    cleanUp();

                    List<Category> categories = generateCategories(categoryCount);
                    List<User> users = generateUsers(userCount);
                    generatePosts(postCount, categories, users);
                    return null;
                });

        System.out.println("Seeding done.");
        SpringApplication.exit(applicationContext, () -> 0);
    }

    private void generatePosts(int postCount, List<Category> categories, List<User> users) {
        List<Post> postList = new ArrayList<>();
        for (int i = 0; i < postCount; i++) {
            String name = faker.book().title().concat(" ").concat(faker.random().hex(5));
            String slug = slugGeneratorService.getSlug(name, postRepository::existsBySlug);
            PostStatus postStatus = faker.options().option(PostStatus.class);
            Post post =
                    Post.builder()
                            .title(name)
                            .slug(slug)
                            .user(getRandomUser(users))
                            .status(postStatus)
                            .avatarUrl(
                                    AVATAR_API
                                            .concat("shapes/jpeg?size=500&seed={SEED}")
                                            .replace("{SEED}", name))
                            .publishedAt(
                                    !postStatus.equals(PostStatus.DRAFT)
                                            ? OffsetDateTime.now()
                                            : null)
                            .content(faker.lorem().paragraph(faker.random().nextInt(10, 100)))
                            .description(
                                    String.join(
                                            " ",
                                            faker.lorem().words(faker.random().nextInt(5, 15))))
                            .categories(getRandomCategories(categories))
                            .build();
            postList.add(post);
        }

        var savedPosts = postRepository.saveAll(postList);
        Set<String> usedCodes = new HashSet<>();
        List<Redirect> redirects = new ArrayList<>();
        for (Post post : savedPosts) {
            String code =
                    slugGeneratorService.getRandomSlug(
                            12,
                            candidate ->
                                    usedCodes.contains(candidate)
                                            || redirectRepository.existsByCode(candidate));
            usedCodes.add(code);
            redirects.add(
                    Redirect.builder()
                            .code(code)
                            .targetType(RedirectTargetType.POST)
                            .targetId(post.getId())
                            .build());
        }
        redirectRepository.saveAll(redirects);
    }

    private User getRandomUser(List<User> users) {
        Collections.shuffle(users);
        return users.get(faker.random().nextInt(0, users.size() - 1));
    }

    private Set<Category> getRandomCategories(List<Category> categories) {
        Collections.shuffle(categories);
        return new HashSet<>(categories.subList(0, faker.number().numberBetween(1, 4)));
    }

    private List<User> generateUsers(int userCount) {
        List<User> userList = new ArrayList<>();
        String password = "password";
        for (int i = 0; i < userCount; i++) {
            String handle = faker.internet().username().replaceAll("[^a-z0-9_]", "").toLowerCase();
            if (handle.length() < 5) {
                handle = handle + faker.random().hex(5);
            }
            if (handle.length() > 30) {
                handle = handle.substring(0, 30);
            }
            final String uniqueHandle = handle + "_" + faker.random().hex(4);

            User user =
                    User.builder()
                            .avatar(null)
                            .bio(faker.lorem().sentence(faker.random().nextInt(1, 299)))
                            .email(faker.internet().emailAddress())
                            .handle(
                                    uniqueHandle.length() > 30
                                            ? uniqueHandle.substring(0, 30)
                                            : uniqueHandle)
                            .role(UserRole.USER)
                            .isVerified(faker.options().option(Boolean.FALSE, Boolean.TRUE))
                            .lastName(faker.name().lastName())
                            .firstName(faker.name().firstName())
                            .build();
            User savedUser = userRepository.save(user);
            UserCredentials userCredentials =
                    UserCredentials.builder()
                            .user(savedUser)
                            .hashedPassword(passwordEncoder.encode(password))
                            .build();
            userCredentialsRepository.save(userCredentials);
            userList.add(savedUser);
        }
        return userList;
    }

    private List<Category> generateCategories(int categoryCount) {
        List<Category> categories = new ArrayList<>();

        for (int i = 0; i < categoryCount; i++) {
            String name = faker.commerce().department().concat(" ").concat(faker.random().hex(5));
            String slug = slugGeneratorService.getSlug(name, categoryRepository::existsBySlug);

            Category category =
                    Category.builder()
                            .name(name)
                            .slug(slug)
                            .description(faker.lorem().sentence(faker.random().nextInt(20, 100)))
                            .build();
            categories.add(category);
        }

        return categoryRepository.saveAll(categories);
    }

    private void cleanUp() {
        categoryRepository.deleteAllInBatch();
        userCredentialsRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        redirectRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
    }

    private static int getArgumentValue(String[] args, String key, int defaultValue) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(key)) {
                try {
                    return Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.out.println(
                            "Invalid value for " + key + ", using default: " + defaultValue);
                    return defaultValue;
                }
            }
        }
        return defaultValue;
    }
}
