package com.github.mohrezal.api.domains.users.repositories;

import com.github.mohrezal.api.domains.users.models.UserCredentials;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCredentialsRepository
        extends JpaRepository<@NonNull UserCredentials, @NonNull UUID> {}
