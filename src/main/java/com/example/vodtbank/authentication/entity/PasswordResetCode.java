package com.example.vodtbank.authentication.entity;

import java.time.LocalDateTime;

import com.example.vodtbank.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "password_reset_codes")
public class PasswordResetCode extends BaseEntity {
	@Column(unique = true)
	private String code;

	@Column
	private LocalDateTime expirationDate;

	@Column
	private boolean used = false;

	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;
}
