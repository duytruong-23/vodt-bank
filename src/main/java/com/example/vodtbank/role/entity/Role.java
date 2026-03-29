package com.example.vodtbank.role.entity;

import com.example.vodtbank.authentication.entity.User;
import com.example.vodtbank.common.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Column(unique = true)
    @NotBlank(message = "Role name is required")
    private String name;

    @ManyToMany(mappedBy = "roles")
    @JsonBackReference
    private List<User> users;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
