package com.bondyuu.bondyupage_BE.service;

import com.bondyuu.bondyupage_BE.domain.User;
import com.bondyuu.bondyupage_BE.domain.UserDetailsImpl;
import com.bondyuu.bondyupage_BE.error.ErrorCode;
import com.bondyuu.bondyupage_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Optional<User> member = userRepository.findByEmail(email);
    return member
        .map(UserDetailsImpl::new)
        .orElseThrow(() -> new UsernameNotFoundException(String.valueOf(ErrorCode.USER_NOT_FOUND)));
  }
}
