package com.inflearn.kyungsik.studyolle_inflearn.account;

import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.inflearn.kyungsik.studyolle_inflearn.domain.Account;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccountController {

	private final SignUpFormValidator signUpFormValidator;
	private final AccountService accountService;
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

		Account account = accountService.processNewAccount(signUpForm);
		accountService.login(account);
		return "redirect:/";
	}

	@GetMapping("/check-email-token")
	public String checkEmailToken(String token, String email, Model model) {
		Account account = accountRepository.findByEmail(email);
		String view = "account/checked-email";
		if (account == null) {
			model.addAttribute("error", "wrong.email");
			return view;
		}

		if (!account.isValidToken(token)) {
			model.addAttribute("error", "wrong.token");
			return view;
		}

		accountService.completeSignUp(account);
		model.addAttribute("numberOfUser", accountRepository.count());
		model.addAttribute("nickname", account.getNickname());
		return view;
	}

	@GetMapping("/check-email")
	public String checkEmail(@AuthenticationPrincipal UserAccount account, Model model) {
		model.addAttribute("email", account.getAccount().getEmail());
		return "account/check-email";
	}

	@GetMapping("/resend-confirm-email")
	public String resendConfirmEmail(@AuthenticationPrincipal UserAccount account, Model model) {
		if (!account.getAccount().canSendConfirmEmail()) {
			model.addAttribute("error", "?????? ????????? 1????????? ????????? ????????? ??? ????????????.");
			model.addAttribute("email",account.getAccount().getEmail());
			return "account/check-email";
		}

		accountService.sendSignUpConfirmEmail(account.getAccount());
		return "redirect:/";
	}

	@GetMapping("/profile/{nickname}")
	public String profile(@PathVariable String nickname, Model model, @AuthenticationPrincipal UserAccount account) {
		Account accountToView = accountService.getAccount(nickname);
		model.addAttribute(accountToView);
		model.addAttribute("isOwner", accountToView.equals(account.getAccount()));
		return "account/profile";
	}
}
