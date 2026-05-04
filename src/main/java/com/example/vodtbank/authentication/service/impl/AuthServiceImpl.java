package com.example.vodtbank.authentication.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.example.vodtbank.account.dto.AccountDto;
import com.example.vodtbank.account.service.AccountService;
import com.example.vodtbank.authentication.dto.PasswordResetCodeDto;
import com.example.vodtbank.authentication.dto.ResetPasswordRequest;
import com.example.vodtbank.authentication.dto.SignInRequest;
import com.example.vodtbank.authentication.dto.SignInResponse;
import com.example.vodtbank.authentication.dto.SignUpRequest;
import com.example.vodtbank.authentication.dto.UserDto;
import com.example.vodtbank.authentication.entity.PasswordResetCode;
import com.example.vodtbank.authentication.entity.User;
import com.example.vodtbank.authentication.repository.PasswordResetCodeRepository;
import com.example.vodtbank.authentication.repository.UserRepository;
import com.example.vodtbank.authentication.service.AuthService;
import com.example.vodtbank.authentication.service.BloomFilterService;
import com.example.vodtbank.authentication.service.PasswordResetCodeService;
import com.example.vodtbank.common.enums.AccountType;
import com.example.vodtbank.exception.BadRequestException;
import com.example.vodtbank.exception.NotFoundException;
import com.example.vodtbank.notification.dto.NotificationDto;
import com.example.vodtbank.notification.helper.NotificationHelper;
import com.example.vodtbank.notification.service.UserActionService;
import com.example.vodtbank.role.dto.RoleDto;
import com.example.vodtbank.role.dto.SystemRole;
import com.example.vodtbank.role.entity.Role;
import com.example.vodtbank.role.repository.RoleRepository;
import com.example.vodtbank.role.service.RoleService;
import com.example.vodtbank.security.token.TokenService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
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
	private final PasswordResetCodeService passwordResetCodeService;
	private final PasswordResetCodeRepository passwordResetCodeRepository;
	private final AccountService accountService;

	@Value("${password.base-url}")
	private String passwordResetBaseUrl;

	public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder, TokenService tokenService,
			RoleService roleService, ModelMapper modelMapper,
			UserActionService userActionService, BloomFilterService bloomFilterService,
			PasswordResetCodeService passwordResetCodeService,
			PasswordResetCodeRepository passwordResetCodeRepository, AccountService accountService) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.tokenService = tokenService;
		this.userActionService = userActionService;
		this.roleService = roleService;
		this.modelMapper = modelMapper;
		this.bloomFilterService = bloomFilterService;
		this.passwordResetCodeService = passwordResetCodeService;
		this.passwordResetCodeRepository = passwordResetCodeRepository;
		this.accountService = accountService;
	}

	@Override
	public UserDto signUp(SignUpRequest signUpRequest) {
		List<String> roleNames = signUpRequest.roles();
		final List<Role> roles = new ArrayList<>();

		if(roleNames == null || roleNames.isEmpty()) {
			Long defaultRoleId = roleService.getDefaultRoleId();
			Role defaultRole = roleRepository.findById(defaultRoleId)
					.orElseThrow(() -> new NotFoundException("Default role with id " + defaultRoleId + " not found"));
			roles.add(defaultRole);
		} else {
			if(roleNames.stream().anyMatch(SystemRole::isAdminRole)) {
				throw new BadRequestException("Cannot assign role " + SystemRole.ADMIN.name() + " to user");
			}

			roles.addAll(roleNames.stream()
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

		bloomFilterService.addEmail(user.getEmail());
		User savedUser = userRepository.save(user);

		AccountDto savedAccount = accountService.createAccount(AccountType.SAVINGS, savedUser.getEmail());

		NotificationDto notificationDto = NotificationHelper.createWelcomeNotification(savedUser.getEmail(),
				savedUser.getFirstName());
		userActionService.sendAndCreateNotification(notificationDto, savedUser.getId());

		NotificationDto newAccountNotification = NotificationHelper.createNewAccountNotification(savedUser.getEmail(),
				savedUser.getFirstName(), savedAccount.getAccountNumber(), savedAccount.getAccountType(),
				savedAccount.getCurrency());
		userActionService.sendAndCreateNotification(newAccountNotification, savedUser.getId());

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
	public void forgotPassword(String email) {
		if(!bloomFilterService.isEmailExisting(email)) {
			throw new NotFoundException("Email not found");
		}

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new NotFoundException("Email not found"));

		PasswordResetCodeDto passwordResetCodeDto = passwordResetCodeService.replaceCodeForUser(user.getId());

		NotificationDto passwordResetNotification = NotificationHelper.createPasswordResetNotification(user.getEmail(),
				user.getFirstName(),
				passwordResetBaseUrl, passwordResetCodeDto.getCode());
		userActionService.sendAndCreateNotification(passwordResetNotification, user.getId());
	}

	@Override
	public void updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest) {
		String code = resetPasswordRequest.code();
		String email = resetPasswordRequest.email();
		String newPassword = resetPasswordRequest.newPassword();

		PasswordResetCode passwordResetCode = passwordResetCodeRepository.findByCode(code)
				.orElseThrow(() -> new NotFoundException("Invalid password reset code"));

		if(!passwordResetCodeService.validateCode(code)) {
			// Invalidate the code if it's expired or already used
			passwordResetCodeRepository.deleteById(passwordResetCode.getId());
			throw new BadRequestException("Invalid or expired password reset code");
		}

		User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Email not found"));

		if(passwordResetCode.getUser().getId().equals(user.getId()) && passwordEncoder.matches(newPassword,
				user.getPassword())) {
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);
		}

		// Invalidate the code after successful password reset
		passwordResetCodeRepository.deleteById(passwordResetCode.getId());

		NotificationDto passwordUpdateNotification = NotificationHelper.createPasswordUpdateNotification(
				user.getEmail(), user.getFirstName());
		userActionService.sendAndCreateNotification(passwordUpdateNotification, user.getId());
	}
}
