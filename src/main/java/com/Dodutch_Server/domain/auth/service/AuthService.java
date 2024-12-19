package com.Dodutch_Server.domain.auth.service;

import com.Dodutch_Server.domain.auth.dto.KakaoInfoDto;
import com.Dodutch_Server.domain.auth.dto.KakaoMemberAndExistDto;
import com.Dodutch_Server.domain.auth.dto.request.SignUpRequestDto;
import com.Dodutch_Server.domain.auth.dto.response.KakaoResponseDto;
import com.Dodutch_Server.domain.member.entity.Member;
import com.Dodutch_Server.domain.member.repository.MemberRepository;
import com.Dodutch_Server.global.common.apiPayload.code.status.ErrorStatus;
import com.Dodutch_Server.global.common.exception.handler.ErrorHandler;
import com.Dodutch_Server.global.jwt.JwtTokenProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-url}")
    private String redirectUrl;



    //카카오 로그인
    @Transactional
    public KakaoResponseDto loginWithKakao(String accessCode, HttpServletResponse response) {

        String accessToken = getAccessToken(accessCode);

        KakaoMemberAndExistDto kakaoMemberAndExistDto = getUserProfileByToken(accessToken); //dto에 kakaoId 저장
        Optional<Member> findMember = memberRepository.findById(kakaoMemberAndExistDto.getMember().getId());
        return getKaKaoTokens(kakaoMemberAndExistDto.getMember().getKakaoId(),kakaoMemberAndExistDto.getExistMember(), response);
    }




    private String getAccessToken(String accessCode) {

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUrl);
        body.add("code", accessCode);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonNode.get("access_token").asText(); //토큰 전송
    }

    // 카카오Api 호출해서 AccessToken으로 유저정보 가져오기(id)
    public Map<String, Object> getUserAttributesByToken(String accessToken){
        return WebClient.create()
                .get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

    }

    // 카카오API에서 가져온 유저정보를 DB에 저장
    @Transactional
    public KakaoMemberAndExistDto getUserProfileByToken(String accessToken){
        Map<String, Object> userAttributesByToken = getUserAttributesByToken(accessToken);
        KakaoInfoDto kakaoInfoDto = new KakaoInfoDto(userAttributesByToken);
        Member member = Member.builder()
                .kakaoId(kakaoInfoDto.getKakaoId())
                .build();

        boolean existMember = false;

        if(memberRepository.findByKakaoId(member.getKakaoId()) != null) //DB에 회원정보 있으면 existMember = True
        {
            existMember = true;
        }
        else {
            memberRepository.save(member); //DB에 회원정보 없으면 저장
        }

        Member findMember = memberRepository.findByKakaoId(kakaoInfoDto.getKakaoId());
        return KakaoMemberAndExistDto.builder()
                .member(findMember)
                .existMember(existMember)
                .build();
    }

    //Access Token, Refresh Token 생성
    @Transactional
    public KakaoResponseDto getKaKaoTokens(String kakaoId, Boolean existMember, HttpServletResponse response) {
        final String accessToken = jwtTokenProvider.createAccessToken(kakaoId.toString());
        final String refreshToken = jwtTokenProvider.createRefreshToken();

        Member member = memberRepository.findByKakaoId(kakaoId);
        member.setRefreshToken(refreshToken);


        return KakaoResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .existMember(existMember)
                .build();
    }

    @Transactional
    public void signup(SignUpRequestDto signUpRequestDto){
        String nickName = signUpRequestDto.getNickName();
        String accessToken = signUpRequestDto.getAccessToken();

        if(!jwtTokenProvider.validateToken(accessToken)||!StringUtils.hasText(accessToken)){
            throw new ErrorHandler(ErrorStatus.INVALID_ACCESS_TOKEN);
        }

        String kakaoId = jwtTokenProvider.getPayload(accessToken);

        Member member = memberRepository.findByKakaoId(kakaoId);

        member.setNickName(nickName);

    }
}
