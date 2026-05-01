package com.example.vodtbank.account.controller;

import java.util.List;

import com.example.vodtbank.account.dto.AccountOverview;
import com.example.vodtbank.account.service.AccountService;
import com.example.vodtbank.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {
	private final AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@GetMapping("/me")
	public ResponseEntity<Response<List<AccountOverview>>> getCurrentUserAccounts() {
		List<AccountOverview> accounts = accountService.getCurrentUserAccounts();
		return ResponseEntity.ok(Response.success("Accounts retrieved successfully", accounts));
	}

	@PutMapping("/close/{accountIdToken}")
	public ResponseEntity<Response<Void>> closeAccount(@PathVariable String accountIdToken) {
		accountService.closeAccount(accountIdToken);
		return ResponseEntity.ok(Response.noContent("Account closed successfully"));
	}
}
