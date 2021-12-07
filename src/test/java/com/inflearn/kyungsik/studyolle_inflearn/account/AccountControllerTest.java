package com.inflearn.kyungsik.studyolle_inflearn.account;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AccountRepository accountRepository;

	@MockBean
	JavaMailSender javaMailSender;

	@DisplayName("회원 가입 화면 보이는지 테스트")
	@Test
	void signUpForm() throws Exception {
		mockMvc.perform(get("/sign-up"))
			.andExpect(status().isOk())
			.andExpect(view().name("account/sign-up"))
			.andExpect(model().attributeExists("signUpForm"));
	}

	@DisplayName("회원 가입 - 입력값 정상")
	@Test
	void signUpSubmit_with_right_input() throws Exception {
		mockMvc.perform(
			post("/sign-up")
				.param("nickname", "kyungsik")
				.param("email", "kyungsik@naver.com")
				.param("password", "12345678")
				.with(csrf())
		).andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/"));

		assertThat(accountRepository.existsByEmail("kyungsik@naver.com")).isTrue();
		then(javaMailSender).should().send(any(SimpleMailMessage.class));
	}

	@DisplayName("회원 가입 - 입력값 오류")
	@Test
	void signUpSubmit_with_wrong_input() throws Exception {
		mockMvc.perform(
			post("/sign-up")
				.param("nickname", "kyungsik")
				.param("email", "kyungcom")
				.param("password", "1234")
				.with(csrf())
		).andExpect(status().isOk())
			.andExpect(view().name("account/sign-up"));

	}
}