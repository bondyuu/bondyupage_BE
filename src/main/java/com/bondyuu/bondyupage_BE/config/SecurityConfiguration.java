package com.bondyuu.bondyupage_BE.config;

import com.bondyuu.bondyupage_BE.jwt.AccessDeniedHandlerException;
import com.bondyuu.bondyupage_BE.jwt.AuthenticationEntryPointException;
import com.bondyuu.bondyupage_BE.jwt.TokenProvider;
import com.bondyuu.bondyupage_BE.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration {

    @Value("${jwt.secret}")
    String SECRET_KEY;
    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationEntryPointException authenticationEntryPointException;
    private final AccessDeniedHandlerException accessDeniedHandlerException;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().configurationSource(corsConfigurationSource);

        http.csrf()
                .ignoringAntMatchers("/h2-console/**")
                .disable()

                .headers()
                .frameOptions()
                .disable()

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointException)
                .accessDeniedHandler(accessDeniedHandlerException)

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .requestMatchers(request -> CorsUtils.isPreFlightRequest(request)).permitAll()
                .antMatchers("/h2-console/**").permitAll()  //h2-console 해제
                .antMatchers("/profile").permitAll()
                .antMatchers("/users/signup").permitAll()   //signup, login 해제
                .antMatchers("/users/login").permitAll()
                .antMatchers("/users/check/**").permitAll()  //중복확인 해제
                .antMatchers("/users/refresh").permitAll()
                .antMatchers("/users").permitAll()
                .antMatchers("/mail/*").permitAll()   //인증 메일 보내기 해제
                .antMatchers(HttpMethod.POST,"/users/certification").permitAll()
                .antMatchers(HttpMethod.GET, "/posts").permitAll()  //게시글 목록 조회 해제
                .antMatchers(HttpMethod.GET, "/posts/**").permitAll()  //게시글 조회 해제
                .antMatchers("/subscribe/**").permitAll()
                .antMatchers("pub/chat/message").permitAll()
                .antMatchers("sub/chat/room/**").permitAll()
                .antMatchers("chat/**").permitAll()
                .antMatchers("/ws-stomp/**").permitAll()
                .antMatchers("/v3/api-docs/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/swagger-ui/**").permitAll()
//                .antMatchers("/user/**").permitAll()
                .anyRequest().authenticated()

                .and()
                .apply(new JwtSecurityConfiguration(SECRET_KEY, tokenProvider, userDetailsService));

        return http.build();
    }
}
