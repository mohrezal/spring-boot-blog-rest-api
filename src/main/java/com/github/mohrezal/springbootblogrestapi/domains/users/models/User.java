package com.github.mohrezal.springbootblogrestapi.domains.users.models;

import com.github.mohrezal.springbootblogrestapi.domains.users.enums.UserRole;
import com.github.mohrezal.springbootblogrestapi.shared.models.BaseModel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(
        name = "users",
        indexes = {
            @Index(name = "idx_user_email", columnList = "email", unique = true),
        })
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class User extends BaseModel implements UserDetails {

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "bio", nullable = false)
    private String bio;

    @Column(name = "avatar_url", nullable = false)
    private String avatarUrl;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @OneToOne(
            mappedBy = "user",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private UserCredentials credentials;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public String getPassword() {
        return credentials.getHashedPassword();
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
