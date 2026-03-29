package com.example.vodtbank.authentication.entity;

import com.example.vodtbank.common.entity.BaseEntity;
import com.example.vodtbank.role.entity.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String phoneNumber;

    @Column(unique = true, nullable = false)
    @Email
    @NotBlank(message = "Email is required")
    private String email;

    @Column
    private String password;

    @Column
    private String profilePictureUrl;

    @Column
    private Boolean active = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonManagedReference
    private List<Role> roles;
}
