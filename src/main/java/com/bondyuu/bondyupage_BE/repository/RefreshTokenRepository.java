package com.bondyuu.bondyupage_BE.repository;

import com.bondyuu.bondyupage_BE.domain.RefreshToken;
import com.bondyuu.bondyupage_BE.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByUser(User user);
  void deleteByRefreshTokenValue(String refreshTokenValue);
  Optional<RefreshToken> findByRefreshTokenValue(String refreshTokenValue);
}
