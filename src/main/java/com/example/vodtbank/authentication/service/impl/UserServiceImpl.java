package com.example.vodtbank.authentication.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.StringJoiner;
import java.util.UUID;

import com.example.vodtbank.authentication.dto.UpdatePasswordRequest;
import com.example.vodtbank.authentication.dto.UserDto;
import com.example.vodtbank.authentication.entity.User;
import com.example.vodtbank.authentication.repository.UserRepository;
import com.example.vodtbank.authentication.service.UserService;
import com.example.vodtbank.exception.BadRequestException;
import com.example.vodtbank.exception.NotFoundException;
import com.example.vodtbank.notification.dto.EmailDto;
import com.example.vodtbank.notification.dto.NotificationDto;
import com.example.vodtbank.notification.helper.NotificationHelper;
import com.example.vodtbank.notification.service.UserActionService;
import com.example.vodtbank.role.dto.SystemRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(rollbackFor = Throwable.class)
public class UserServiceImpl implements UserService {
	private static final String FILE_NAME_SEPARATOR = "_";

	private final Log log = LogFactory.getLog(this.getClass());

	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final PasswordEncoder passwordEncoder;
	private final UserActionService userActionService;

	@Value("${upload.profile-picture.directory}")
	private String uploadDir;

	public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder,
			UserActionService userActionService) {
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
		this.passwordEncoder = passwordEncoder;
		this.userActionService = userActionService;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getCurrentUser() {
		String email = getCurrentUserEmail();

		return modelMapper.map(
				userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found")),
				UserDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<UserDto> getUsers(int page, int size) {
		// page - 1 because Spring Data JPA pages are 0-indexed
		Page<User> users = userRepository.findAllExcludeRole(SystemRole.ADMIN.name(), PageRequest.of(page - 1, size));

		return users.map(user -> modelMapper.map(user, UserDto.class));
	}

	@Override
	public void updatePassword(UpdatePasswordRequest request) {
		if(!request.newPassword().equals(request.confirmPassword())) {
			throw new BadRequestException("New password and confirm password do not match");
		}

		if(request.newPassword().equals(request.oldPassword())) {
			throw new BadRequestException("New password cannot be the same as old password");
		}

		User user = userRepository.findByEmail(getCurrentUserEmail())
				.orElseThrow(() -> new NotFoundException("User not found"));

		if(!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
			throw new BadRequestException("Old password is incorrect");
		}

		user.setPassword(passwordEncoder.encode(request.newPassword()));
		userRepository.save(user);

		EmailDto passwordUpdateEmail = NotificationHelper.createPasswordUpdateEmail(user.getEmail(),
				user.getFirstName());
		NotificationDto passwordUpdateNotification = NotificationHelper.createPasswordUpdateNotification(
				user.getEmail());
		userActionService.sendAndCreateNotification(passwordUpdateEmail, passwordUpdateNotification, user.getId());
	}

	@Override
	public String uploadProfilePicture(MultipartFile file) {
		User user = userRepository.findByEmail(getCurrentUserEmail())
				.orElseThrow(() -> new NotFoundException("User not found"));

		try {
			Path uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();

			if(!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			if(StringUtils.hasText(user.getProfilePictureUrl())) {
				Path oldFilePath = uploadPath.resolve(Path.of(user.getProfilePictureUrl()).getFileName());
				Files.deleteIfExists(oldFilePath);
			}

			String originalFilename = file.getOriginalFilename();

			if(!StringUtils.hasText(originalFilename)) {
				throw new BadRequestException("Invalid file name");
			}

			originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
			String fileExtension = StringUtils.getFilenameExtension(originalFilename);
			fileExtension = StringUtils.hasLength(fileExtension) ? "." + fileExtension : "";
			String newFilename = new StringJoiner(FILE_NAME_SEPARATOR, "", fileExtension)
					.add("profile")
					.add(String.valueOf(user.getId()))
					.add(UUID.randomUUID().toString())
					.toString();

			Path newFilePath = uploadPath.resolve(newFilename);
			Files.copy(file.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);

			String newProfilePictureUrl = uploadDir + newFilename;
			user.setProfilePictureUrl(newProfilePictureUrl);

			userRepository.save(user);

		} catch(IOException e) {
			log.error(e);
			throw new RuntimeException("Failed to upload profile picture", e);
		}

		return user.getProfilePictureUrl();
	}

	private String getCurrentUserEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if(authentication == null) {
			throw new BadRequestException("No authenticated user found");
		}

		return authentication.getName();
	}
}
