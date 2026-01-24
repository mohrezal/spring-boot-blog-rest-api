package com.github.mohrezal.springbootblogrestapi.domains.storage.repositories;

import com.github.mohrezal.springbootblogrestapi.domains.storage.models.Storage;
import com.github.mohrezal.springbootblogrestapi.domains.users.models.User;
import java.util.Optional;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRepository extends JpaRepository<@NonNull Storage, @NonNull UUID> {

    Optional<Storage> findByFilename(String filename);

    Page<@NonNull Storage> findAllByUser(User user, Pageable pageable);
}
