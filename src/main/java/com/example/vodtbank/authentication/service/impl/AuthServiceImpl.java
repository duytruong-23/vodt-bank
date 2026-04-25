package com.example.vodtbank.authentication.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.example.vodtbank.account.entity.Account;
import com.example.vodtbank.authentication.dto.ResetPasswordRequest;
import com.example.vodtbank.authentication.dto.SignInRequest;
import com.example.vodtbank.authentication.dto.SignInResponse;
import com.example.vodtbank.authentication.dto.SignUpRequest;
import com.example.vodtbank.authentication.dto.UserDto;
import com.example.vodtbank.authentication.entity.User;
import com.example.vodtbank.authentication.repository.UserRepository;
import com.example.vodtbank.authentication.service.AuthService;
import com.example.vodtbank.authentication.service.BloomFilterService;
import com.example.vodtbank.exception.BadRequestException;
import com.example.vodtbank.exception.NotFoundException;
import com.example.vodtbank.notification.dto.EmailDto;
import com.example.vodtbank.notification.dto.NotificationDto;
import com.example.vodtbank.notification.service.UserActionService;
import com.example.vodtbank.notification.util.NotificationHelper;
import com.example.vodtbank.role.dto.RoleDto;
import com.example.vodtbank.role.entity.Role;
import com.example.vodtbank.role.repository.RoleRepository;
import com.example.vodtbank.role.service.RoleService;
import com.example.vodtbank.security.token.TokenService;
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
	private final ModelMapper modelMapper;
	private final UserActionService userActionService;
	private final BloomFilterService bloomFilterService;

	public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder, TokenService tokenService,
			RoleService roleService, ModelMapper modelMapper,
			UserActionService userActionService, BloomFilterService bloomFilterService) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.tokenService = tokenService;
		this.userActionService = userActionService;
		this.roleService = roleService;
		this.modelMapper = modelMapper;
		this.bloomFilterService = bloomFilterService;
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

		if(bloomFilterService.isEmailExisting(signUpRequest.email())) {
			throw new BadRequestException("Email already exists");
		}

		User user = modelMapper.map(signUpRequest, User.class);
		user.setPassword(passwordEncoder.encode(signUpRequest.password()));
		user.setRoles(roles);

		User savedUser = userRepository.save(user);

		//TODO: Create default account for user and send welcome email and notification
		Account savedAccount = new Account();

		EmailDto emailDto = NotificationHelper.createWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName());
		NotificationDto notificationDto = NotificationHelper.createWelcomeNotification(savedUser.getEmail());
		userActionService.sendAndCreateNotification(emailDto, notificationDto, savedUser.getId());

		EmailDto newAccountEmail = NotificationHelper.createNewAccountEmail(savedUser.getEmail(),
				savedUser.getFirstName(), savedAccount.getAccountNumber());
		NotificationDto newAccountNotification = NotificationHelper.createNewAccountNotification(savedUser.getEmail());
		userActionService.sendAndCreateNotification(newAccountEmail, newAccountNotification, savedUser.getId());

		return modelMapper.map(savedUser, UserDto.class);
	}

	@Override
	public SignInResponse signIn(SignInRequest signInRequest) {
		final String email = signInRequest.email();
		final String password = signInRequest.password();

		if(!bloomFilterService.isEmailExisting(signInRequest.email())) {
			throw new NotFoundException("Email not found");
		}

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new NotFoundException("Email not found"));

		if(!passwordEncoder.matches(password, user.getPassword())) {
			throw new BadRequestException("Invalid password");
		}

		String token = tokenService.generateToken(email);
		List<RoleDto> roles = user.getRoles().stream()
				.map(role -> modelMapper.map(role, RoleDto.class))
				.toList();

		return new SignInResponse(token, roles);
	}

	@Override
	public Object forgetPassword(String email) {
		return null;
	}

	@Override
	public Object updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest) {
		return null;
	}
}
