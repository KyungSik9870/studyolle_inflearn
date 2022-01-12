package com.inflearn.kyungsik.studyolle_inflearn.account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingsController {

	@GetMapping("/settings/progile")
	public String profileUpdateForm(@AuthenticationPrincipal UserAccount userAccount, Model model) {
		model.addAttribute("account", userAccount.getAccount());
		model.addAttribute(new Profile(userAccount.getAccount()));
		return "settings/profile";
	}
}
