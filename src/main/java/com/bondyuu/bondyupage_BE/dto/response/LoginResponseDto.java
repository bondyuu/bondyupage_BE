package com.bondyuu.bondyupage_BE.dto.response;

import com.bondyuu.bondyupage_BE.dto.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private Long id;
    private String nickname;
    private TokenDto token;
}
