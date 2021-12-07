package com.inflearn.kyungsik.studyolle_inflearn.account;

import javax.validation.Valid;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import com.inflearn.kyungsik.studyolle_inflearn.ConsoleMailSender;
import com.inflearn.kyungsik.studyolle_inflearn.domain.Account;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccountController {

	private final SignUpFormValidator signUpFormValidator;
	private final ConsoleMailSender consoleMailSender;
	private final AccountRepository accountRepository;

	@InitBinder("signUpForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(signUpFormValidator);
	}

	@GetMapping("/sign-up")
	public String signUpForm(Model model) {
		model.addAttribute(new SignUpForm());
		return "account/sign-up";
	}

	@PostMapping("/sign-up")
	public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {
		if (errors.hasErrors()) {
			return "account/sign-up";
		}

		Account newAccount = accountRepository.save(Account.builder()
			.email(signUpForm.getEmail())
			.nickname(signUpForm.getNickname())
			.password(signUpForm.getPassword()) // TODO encoding 해야함
			.studyCreatedByWeb(true)
			.studyEnrollmentResultByWeb(true)
			.studyUpdatedByWeb(true)
			.build());

		newAccount.generateEmailCheckToken();
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setSubject("스터디올래, 회원 가입 인증");
		simpleMailMessage.setText(
			"/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail());

		consoleMailSender.send(simpleMailMessage);

		return "redirect:/";
	}
}
