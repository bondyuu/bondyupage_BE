package com.bondyuu.bondyupage_BE.service;

import com.bondyuu.bondyupage_BE.domain.RefreshToken;
import com.bondyuu.bondyupage_BE.domain.User;
import com.bondyuu.bondyupage_BE.domain.UserDetailsImpl;
import com.bondyuu.bondyupage_BE.dto.TokenDto;
import com.bondyuu.bondyupage_BE.dto.request.*;
import com.bondyuu.bondyupage_BE.dto.response.LoginResponseDto;
import com.bondyuu.bondyupage_BE.dto.response.MessageResponseDto;
import com.bondyuu.bondyupage_BE.dto.response.ResponseDto;
import com.bondyuu.bondyupage_BE.error.ErrorCode;
import com.bondyuu.bondyupage_BE.jwt.TokenProvider;
import com.bondyuu.bondyupage_BE.repository.RefreshTokenRepository;
import com.bondyuu.bondyupage_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> signup(SignupRequestDto requestDto) {

        if (isPresentNickname(requestDto.getNickname()) != null) {
            return ResponseDto.fail(ErrorCode.DUPLICATED_NICKNAME);
        }

        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            return ResponseDto.fail(ErrorCode.PASSWORDS_NOT_MATCHED);
        }


        User user = User.builder()
                        .email(requestDto.getEmail())
                        .nickname(requestDto.getNickname())
                        .password(passwordEncoder.encode(requestDto.getPassword()))
                        .location(requestDto.getLocation())
                        .profileUrl("https://bondyu.s3.ap-northeast-2.amazonaws.com/static/user/%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84.png")
                        .build();

        userRepository.save(user);

        return ResponseDto.success(MessageResponseDto.builder()
                .msg("회원가입 성공")
                .build());
    }

    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
        User user = isPresentEmail(requestDto.getEmail());
        if (!user.validatePassword(passwordEncoder, requestDto.getPassword())) {
            return ResponseDto.fail(ErrorCode.INVALID_USER);
        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(user);
        tokenToHeaders(tokenDto, response);

        return ResponseDto.success(
                LoginResponseDto.builder()
                                .id(user.getId())
                                .nickname(user.getNickname())
                                .token(tokenDto)
                                .build());
    }

    public ResponseDto<?> logout(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseDto.fail(ErrorCode.INVALID_TOKEN);
        }

        User user = tokenProvider.getUserFromAuthentication();
        if (user == null) {
            return ResponseDto.fail(ErrorCode.NOT_LOGIN_STATE);
        }

        return tokenProvider.deleteRefreshToken(user);
    }

    @Transactional
    public ResponseDto<?> refreshToken(HttpServletRequest request, HttpServletResponse response){
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseDto.fail(ErrorCode.INVALID_TOKEN);
        }

        String refreshTokenValue = request.getHeader("Refresh-Token");
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshTokenValue(refreshTokenValue).orElseThrow(
                () -> new IllegalArgumentException("리프레쉬 토큰을 찾을 수 없습니다.")
        );
        User user = refreshToken.getUser();

        tokenProvider.deleteRefreshToken(user);
        TokenDto tokenDto = tokenProvider.generateTokenDto(user);
        tokenToHeaders(tokenDto, response);
        return ResponseDto.success(tokenDto);
    }
    @Transactional(readOnly = true)
    public User isPresentEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.orElse(null);
    }

    @Transactional(readOnly = true)
    public User isPresentNickname(String nickname) {
        Optional<User> optionalUser = userRepository.findByNickname(nickname);
        return optionalUser.orElse(null);
    }

    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }

}
