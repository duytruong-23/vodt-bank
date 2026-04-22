package com.example.vodtbank.authentication.service;

import com.example.vodtbank.authentication.dto.ResetPasswordRequest;
import com.example.vodtbank.authentication.dto.SignInRequest;
import com.example.vodtbank.authentication.dto.SignInResponse;
import com.example.vodtbank.authentication.dto.SignUpRequest;
import com.example.vodtbank.authentication.dto.UserDto;

public interface AuthService {
	UserDto signUp(SignUpRequest signUpRequest);

	SignInResponse signIn(SignInRequest signInRequest);

	Object forgetPassword(String email);

	Object updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest);

}
