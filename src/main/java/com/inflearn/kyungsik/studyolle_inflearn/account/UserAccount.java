package com.inflearn.kyungsik.studyolle_inflearn.account;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.inflearn.kyungsik.studyolle_inflearn.domain.Account;

public class UserAccount extends User {
	private final Account account;

	public UserAccount(Account account) {
		super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
		this.account = account;
	}

	public Account getAccount() {
		return account;
	}
}
