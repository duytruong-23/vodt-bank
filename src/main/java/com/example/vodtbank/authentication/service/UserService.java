package com.example.vodtbank.authentication.service;

import com.example.vodtbank.authentication.dto.UpdatePasswordRequest;
import com.example.vodtbank.authentication.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
	UserDto getCurrentUser();

	Page<UserDto> getUsers(int page, int size);

	void updatePassword(UpdatePasswordRequest request);

	String uploadProfilePicture(MultipartFile file);

	String getCurrentUserEmail();
}
