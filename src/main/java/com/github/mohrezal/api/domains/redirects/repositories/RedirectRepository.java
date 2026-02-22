package com.github.mohrezal.api.domains.redirects.repositories;

import com.github.mohrezal.api.domains.redirects.enums.RedirectTargetType;
import com.github.mohrezal.api.domains.redirects.models.Redirect;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedirectRepository extends JpaRepository<Redirect, UUID> {
    Optional<Redirect> findByCode(String code);

    Optional<Redirect> findByTargetTypeAndTargetId(RedirectTargetType type, UUID targetId);

    boolean existsByCode(String code);

    void deleteByTargetTypeAndTargetId(RedirectTargetType targetType, UUID targetId);
}
