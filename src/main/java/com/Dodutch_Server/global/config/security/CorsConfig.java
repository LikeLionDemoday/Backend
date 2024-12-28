package com.Dodutch_Server.global.config.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
프론트와 연결 시 cors 요청 허용 관련 설정
 */
@Configuration
@RequiredArgsConstructor
public class CorsConfig  implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(6000); // 사전 요청 캐싱 시간 최대 10분
    }
}
