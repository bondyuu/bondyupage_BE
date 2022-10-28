package com.bondyuu.bondyupage_BE.controller;

import com.bondyuu.bondyupage_BE.domain.UserDetailsImpl;
import com.bondyuu.bondyupage_BE.dto.request.*;
import com.bondyuu.bondyupage_BE.dto.response.ResponseDto;
import com.bondyuu.bondyupage_BE.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public ResponseDto<?> signup(@RequestBody SignupRequestDto requestDto) { return userService.signup(requestDto); }

    //로그인
    @PostMapping("/login")
    public ResponseDto<?> login(@RequestBody LoginRequestDto requestDto,
                                HttpServletResponse response) { return userService.login(requestDto, response); }

    //로그아웃
    @DeleteMapping ("/logout")
    public ResponseDto<?> logout(HttpServletRequest request) {
        return userService.logout(request);
    }


    @GetMapping("/refresh")
    public ResponseDto<?> refreshToken(HttpServletRequest request, HttpServletResponse response){
        return userService.refreshToken(request, response);
    }
}
