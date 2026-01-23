package com.github.mohrezal.springbootblogrestapi.domains.storage.repositories;

import com.github.mohrezal.springbootblogrestapi.domains.storage.models.Storage;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRepository extends JpaRepository<Storage, UUID> {

    Optional<Storage> findByFilename(String filename);
}
