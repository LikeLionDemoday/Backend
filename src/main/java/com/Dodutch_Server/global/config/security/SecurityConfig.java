package com.Dodutch_Server.global.config.security;

import com.Dodutch_Server.domain.member.repository.MemberRepository;
import com.Dodutch_Server.global.jwt.JwtFilter;
import com.Dodutch_Server.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    private final MemberRepository memberRepository;

    @Bean
    public AuthenticationManager authenticationManager(
            final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        http
                .csrf((auth) -> auth.disable());

        http
                .formLogin((auth) -> auth.disable()); // 로그인 폼 미사용

        http
                .httpBasic((auth) -> auth.disable()); // http basic 미사용

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().permitAll()); // 일단 임시로 허용

        /*
        테스트 전 까지 JWT필터 주석처리
         */
        //JWTFilter 등록

        http
                .addFilterBefore(new JwtFilter(jwtTokenProvider, memberRepository), UsernamePasswordAuthenticationFilter.class);


        //세션 방식 미사용
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();

    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        // 아래 url은 filter 에서 제외
        return web ->
                web.ignoring()
                        .requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**", "/auth/**");

    }
}


