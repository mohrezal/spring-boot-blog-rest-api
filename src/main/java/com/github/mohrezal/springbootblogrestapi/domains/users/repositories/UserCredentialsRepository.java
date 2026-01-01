package com.github.mohrezal.springbootblogrestapi.domains.users.repositories;

import com.github.mohrezal.springbootblogrestapi.domains.users.models.UserCredentials;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, UUID> {}
