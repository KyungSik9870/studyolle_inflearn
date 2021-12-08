package com.inflearn.kyungsik.studyolle_inflearn.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.inflearn.kyungsik.studyolle_inflearn.domain.Account;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

	Account findByEmail(String email);
}
