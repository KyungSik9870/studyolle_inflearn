package com.inflearn.kyungsik.studyolle_inflearn.account;

import java.util.List;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inflearn.kyungsik.studyolle_inflearn.domain.Account;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
	private final JavaMailSender javaMailSender;
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public Account processNewAccount(SignUpForm signUpForm) {
		Account newAccount = saveNewAccount(signUpForm);
		newAccount.generateEmailCheckToken();
		sendSignUpConfirmEmail(newAccount);
		return newAccount;
	}

	public void sendSignUpConfirmEmail(Account newAccount) {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setSubject("스터디올래, 회원 가입 인증");
		simpleMailMessage.setText(
			"/check-email-token?token=" + newAccount.getEmailCheckToken()
				+ "&email=" + newAccount.getEmail()
		);

		javaMailSender.send(simpleMailMessage);
	}

	public void login(Account account) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
			new UserAccount(account),
			account.getPassword(),
			List.of(new SimpleGrantedAuthority("ROLE_USER")));

		SecurityContextHolder.getContext().setAuthentication(token);
	}

	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
		Account account = accountRepository.findByEmail(emailOrNickname);
		if (account == null) {
			account = accountRepository.findByNickname(emailOrNickname);
		}
		if(account == null) {
			throw new UsernameNotFoundException(emailOrNickname);
		}
		return new UserAccount(account);
	}

	private Account saveNewAccount(SignUpForm signUpForm) {
		Account account = Account.builder()
			.email(signUpForm.getEmail())
			.nickname(signUpForm.getNickname())
			.password(passwordEncoder.encode(signUpForm.getPassword()))
			.studyCreatedByWeb(true)
			.studyEnrollmentResultByWeb(true)
			.studyUpdatedByWeb(true)
			.build();
		return accountRepository.save(account);
	}

	public Account getAccount(String nickname) {
		Account account = accountRepository.findByEmail(nickname);
		if (account == null) {
			account = accountRepository.findByNickname(nickname);
		}
		if (account == null) {
			throw new UsernameNotFoundException(nickname);
		}
		return account;
	}

	public void completeSignUp(Account account) {
		account.completeSignUp();
		login(account);
	}
}
