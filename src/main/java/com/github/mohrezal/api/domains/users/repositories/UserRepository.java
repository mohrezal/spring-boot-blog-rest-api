package com.github.mohrezal.api.domains.users.repositories;

import com.github.mohrezal.api.domains.users.models.User;
import java.util.Optional;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<@NonNull User, @NonNull UUID> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsByHandle(String handle);

    Optional<User> findByHandle(String handle);
}
