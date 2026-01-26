package com.github.mohrezal.springbootblogrestapi.domains.users.models;

import com.github.mohrezal.springbootblogrestapi.domains.storage.models.Storage;
import com.github.mohrezal.springbootblogrestapi.domains.users.enums.UserRole;
import com.github.mohrezal.springbootblogrestapi.shared.models.BaseModel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(
        name = "users",
        indexes = {
            @Index(name = "idx_user_email", columnList = "email", unique = true),
            @Index(name = "idx_user_handle", columnList = "handle", unique = true),
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class User extends BaseModel implements UserDetails {

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "handle", unique = true, nullable = false, length = 30)
    private String handle;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "bio")
    private String bio;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_id")
    private Storage avatar;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = true;

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
