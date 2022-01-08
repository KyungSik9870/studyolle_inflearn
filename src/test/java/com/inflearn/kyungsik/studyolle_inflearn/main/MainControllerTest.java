package com.inflearn.kyungsik.studyolle_inflearn.main;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.inflearn.kyungsik.studyolle_inflearn.account.AccountRepository;
import com.inflearn.kyungsik.studyolle_inflearn.account.AccountService;
import com.inflearn.kyungsik.studyolle_inflearn.account.SignUpForm;

@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	AccountService accountService;

	@Autowired
	AccountRepository accountRepository;

	@BeforeEach
	void setUp() {
		SignUpForm signUpForm = new SignUpForm();
		signUpForm.setNickname("fbrudtlr");
		signUpForm.setEmail("fbrudtlr9870@naver.com");
		signUpForm.setPassword("12345678");
		accountService.processNewAccount(signUpForm);
	}

	@AfterEach
	void afterAll() {
		accountRepository.deleteAll();
	}

	@DisplayName("이메일로 로그인 성공")
	@Test
	void login_with_email() throws Exception {
		mockMvc.perform(post("/login")
			.param("username", "fbrudtlr9870@naver.com")
			.param("password", "12345678")
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
		.andExpect(authenticated().withUsername("fbrudtlr"));
	}

	@DisplayName("닉네임으로 로그인 성공")
	@Test
	void login_with_nickname() throws Exception {
		mockMvc.perform(post("/login")
			.param("username", "fbrudtlr")
			.param("password", "12345678")
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
			.andExpect(authenticated().withUsername("fbrudtlr"));
	}

	@Test
	void login_fail() throws Exception {
		mockMvc.perform(post("/login")
			.param("username", "111111")
			.param("password", "000000000")
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login?error"))
			.andExpect(unauthenticated());
	}

	@WithMockUser
	@DisplayName("로그아웃")
	@Test
	void logout() throws Exception {
		mockMvc.perform(post("/logout")
			.with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"))
			.andExpect(unauthenticated());
	}
}
