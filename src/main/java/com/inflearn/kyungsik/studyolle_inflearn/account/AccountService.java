package com.inflearn.kyungsik.studyolle_inflearn.account;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.inflearn.kyungsik.studyolle_inflearn.domain.Account;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final JavaMailSender javaMailSender;
	private final AccountRepository accountRepository;

	public void processNewAccount(SignUpForm signUpForm) {
		Account newAccount = saveNewAccount(signUpForm);
		newAccount.generateEmailCheckToken();
		sendSignUpConfirmEmail(newAccount);
	}

	private Account saveNewAccount(SignUpForm signUpForm) {
		Account account = Account.builder()
			.email(signUpForm.getEmail())
			.nickname(signUpForm.getNickname())
			.password(signUpForm.getPassword()) // TODO encoding 해야함
			.studyCreatedByWeb(true)
			.studyEnrollmentResultByWeb(true)
			.studyUpdatedByWeb(true)
			.build();
		return accountRepository.save(account);
	}

	private void sendSignUpConfirmEmail(Account newAccount) {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setSubject("스터디올래, 회원 가입 인증");
		simpleMailMessage.setText(
			"/check-email-token?token=" + newAccount.getEmailCheckToken()
				+ "&email=" + newAccount.getEmail()
		);

		javaMailSender.send(simpleMailMessage);
	}
}