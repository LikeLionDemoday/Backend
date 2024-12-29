package com.Dodutch_Server.global.config.security;

import com.Dodutch_Server.domain.member.repository.MemberRepository;
import com.Dodutch_Server.global.jwt.JwtFilter;
import com.Dodutch_Server.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

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
                        .anyRequest().authenticated()); // 일단 임시로 허용

        http.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); // 허용할 도메인
            config.setAllowedOrigins(Collections.singletonList("https://dodutch.vercel.app")); // 허용할 도메인
            config.setAllowedOrigins(Collections.singletonList("http://dodutch.vercel.app")); // 허용할 도메인
            config.setAllowedMethods(Collections.singletonList("*")); // 모든 메서드 허용
            config.setAllowCredentials(true); // 인증 정보 허용
            config.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더 허용
            config.setMaxAge(3600L); // Preflight 요청 캐싱 시간 (초 단위)
            return config;
        }));



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


