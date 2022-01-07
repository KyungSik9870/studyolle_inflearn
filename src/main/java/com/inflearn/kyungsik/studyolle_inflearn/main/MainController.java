package com.inflearn.kyungsik.studyolle_inflearn.main;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.inflearn.kyungsik.studyolle_inflearn.account.UserAccount;

@Controller
public class MainController {

	@GetMapping("/")
	public String home(@AuthenticationPrincipal UserAccount account, Model model) {
		if (account != null) {
			model.addAttribute(account.getAccount());
		}
		return "index";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}
}
