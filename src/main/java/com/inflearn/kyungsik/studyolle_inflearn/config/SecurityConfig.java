package com.inflearn.kyungsik.studyolle_inflearn.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final DataSource dataSource;

	public SecurityConfig(UserDetailsService userDetailsService, DataSource dataSource) {
		this.userDetailsService = userDetailsService;
		this.dataSource = dataSource;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.mvcMatchers("/", "/login", "/sign-up", "/check-email-token", "/email-login",
				"/check-email-login", "/login-link").permitAll()
			.mvcMatchers(HttpMethod.GET, "/profile/*").permitAll()
			.anyRequest().authenticated();

		http.formLogin()
			.loginPage("/login").permitAll();

		http.logout()
			.logoutSuccessUrl("/");

		http.rememberMe()
			.userDetailsService(userDetailsService)
			.tokenRepository(tokenRepository());
	}

	@Bean
	public PersistentTokenRepository tokenRepository() {
		JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
		jdbcTokenRepository.setDataSource(dataSource);
		return jdbcTokenRepository;
	}

	@Override
	public void configure(WebSecurity web) {
		web.ignoring()
			.mvcMatchers("/node_modules/**", "/images/**")
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}
}
