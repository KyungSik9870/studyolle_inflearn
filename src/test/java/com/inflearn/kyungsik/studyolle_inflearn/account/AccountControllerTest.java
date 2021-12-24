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
import org.springframework.transaction.annotation.Transactional;

import com.inflearn.kyungsik.studyolle_inflearn.domain.Account;

@Transactional
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

		Account account = accountRepository.findByEmail("kyungsik@naver.com");
		assertThat(account.getPassword()).isNotEqualTo("12345678");
		assertThat(account.getEmailCheckToken()).isNotBlank();

		assertThat(accountRepository.existsByEmail("kyungsik@naver.com")).isTrue();
		then(javaMailSender).should().send(any(SimpleMailMessage.class));
	}

	@DisplayName("인증 메일 확인 - 입력값 오류")
	@Test
	void checkEmailToken_with_wrong_input() throws Exception {
		mockMvc.perform(get("/check-email-token")
			.param("token", "sdfadsfasdf")
			.param("email", "email@email.com"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("error"))
			.andExpect(view().name("account/checked-email"));
	}

	@DisplayName("인증 메일 확인 - 입력값 정상")
	@Test
	void checkEmailToken_with_right_input() throws Exception {
		Account account = Account.builder()
			.email("test@email.com")
			.password("12345678")
			.nickname("kyungsik")
			.build();

		Account newAccount = this.accountRepository.save(account);
		newAccount.generateEmailCheckToken();

		mockMvc.perform(get("/check-email-token")
			.param("token", newAccount.getEmailCheckToken())
			.param("email", newAccount.getEmail()))
			.andExpect(status().isOk())
			.andExpect(model().attributeDoesNotExist("error"))
			.andExpect(model().attributeExists("nickname"))
			.andExpect(model().attributeExists("numberOfUser"))
			.andExpect(view().name("account/checked-email"));
	}
}