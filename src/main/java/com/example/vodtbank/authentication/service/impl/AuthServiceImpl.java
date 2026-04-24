package com.example.vodtbank.authentication.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.example.vodtbank.authentication.dto.ResetPasswordRequest;
import com.example.vodtbank.authentication.dto.SignInRequest;
import com.example.vodtbank.authentication.dto.SignInResponse;
import com.example.vodtbank.authentication.dto.SignUpRequest;
import com.example.vodtbank.authentication.dto.UserDto;
import com.example.vodtbank.authentication.entity.User;
import com.example.vodtbank.authentication.repository.UserRepository;
import com.example.vodtbank.authentication.service.AuthService;
import com.example.vodtbank.exception.BadRequestException;
import com.example.vodtbank.exception.NotFoundException;
import com.example.vodtbank.notification.dto.EmailDto;
import com.example.vodtbank.notification.dto.NotificationDto;
import com.example.vodtbank.notification.service.UserActionService;
import com.example.vodtbank.notification.util.NotificationHelper;
import com.example.vodtbank.role.entity.Role;
import com.example.vodtbank.role.repository.RoleRepository;
import com.example.vodtbank.role.service.RoleService;
import com.example.vodtbank.security.token.TokenService;
import com.google.common.hash.BloomFilter;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Throwable.class)
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;
	private final RoleService roleService;
	private final BloomFilter<String> emailBloomFilter;
	private final ModelMapper modelMapper;
	private final UserActionService userActionService;

	public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder, TokenService tokenService,
			RoleService roleService, BloomFilter<String> emailBloomFilter, ModelMapper modelMapper,
			UserActionService userActionService) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.tokenService = tokenService;
		this.userActionService = userActionService;
		this.roleService = roleService;
		this.emailBloomFilter = emailBloomFilter;
		this.modelMapper = modelMapper;
	}

	@Override
	public UserDto signUp(SignUpRequest signUpRequest) {
		final List<Role> roles = new ArrayList<>();

		if(signUpRequest.roles() == null || signUpRequest.roles().isEmpty()) {
			Long defaultRoleId = roleService.getDefaultRoleId();
			Role defaultRole = roleRepository.findById(defaultRoleId)
					.orElseThrow(() -> new NotFoundException("Default role with id " + defaultRoleId + " not found"));
			roles.add(defaultRole);
		} else {
			roles.addAll(signUpRequest.roles().stream()
					.map(roleName -> roleRepository.findByName(roleName)
							.orElseThrow(() -> new NotFoundException("Role with name " + roleName + " not found")))
					.toList()
			);
		}

		if(isEmailExists(signUpRequest.email())) {
			throw new BadRequestException("Email already exists");
		}

		User user = modelMapper.map(signUpRequest, User.class);
		user.setPassword(passwordEncoder.encode(signUpRequest.password()));
		user.setRoles(roles);

		User savedUser = userRepository.save(user);

		//TODO: Send welcome email to user
		EmailDto emailDto = NotificationHelper.createWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName());
		NotificationDto notificationDto = NotificationHelper.createWelcomeNotification(savedUser.getEmail());
		userActionService.sendAndCreateNotification(emailDto, notificationDto, savedUser.getId());

		return null;
	}

	@Override
	public SignInResponse signIn(SignInRequest signInRequest) {
		return null;
	}

	@Override
	public Object forgetPassword(String email) {
		return null;
	}

	@Override
	public Object updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest) {
		return null;
	}

	private boolean isEmailExists(String email) {
		if(emailBloomFilter.mightContain(email)) {
			return userRepository.findByEmail(email).isPresent();
		}
		return false;
	}
}
