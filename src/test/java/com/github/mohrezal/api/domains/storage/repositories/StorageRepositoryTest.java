package com.github.mohrezal.api.domains.storage.repositories;

import static com.github.mohrezal.api.support.builders.StorageBuilder.aStorage;
import static com.github.mohrezal.api.support.builders.UserBuilder.aUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.mohrezal.api.config.JpaAuditingConfig;
import com.github.mohrezal.api.domains.storage.models.Storage;
import com.github.mohrezal.api.domains.users.models.User;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class StorageRepositoryTest {

    @Autowired private StorageRepository storageRepository;

    @Autowired private UserRepository userRepository;

    @Test
    void findByFilename_whenExists_shouldReturnStorage() {
        User user = this.userRepository.save(aUser().build());
        Storage storage =
                storageRepository.save(aStorage().withUser(user).withFilename("test.jpg").build());

        Optional<Storage> result = storageRepository.findByFilename("test.jpg");

        assertTrue(result.isPresent());
        assertEquals("test.jpg", result.get().getFilename());
    }
}
