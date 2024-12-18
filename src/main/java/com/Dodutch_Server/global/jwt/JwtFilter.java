package com.Dodutch_Server.global.jwt;


import com.Dodutch_Server.domain.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    // 이 Filter 에서 액세스토큰이 유효한지 확인 후 SecurityContext에 계정정보 저장
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        logger.info("[JwtFilter] : " + httpServletRequest.getRequestURL().toString());

        // resolveToken 메소드를 호출하여 HTTP 요청 헤더에서 access token을 추출
        String jwt = resolveToken(httpServletRequest);

        // access token이 존재하지 않거나 토큰이 유효하지 않으면 401 코드 반환
        if(!jwtTokenProvider.validateToken(jwt)||!StringUtils.hasText(jwt)){
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        String kakaoId = jwtTokenProvider.getPayload(jwt);
        Member member = memberRepository.findByKaKaoId(kakaoId);

        //member가 null이면 디비에 존재하지 않는 멤버
        if(member == null) {
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }
        // 멤버 정보를 바탕으로 인증 토큰을 생성하고, 이를 Security Context에 저장
        UserDetails userDetails = UserPrincipal.create(member);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    // Header에서 Access Token 가져오기
    private String resolveToken(HttpServletRequest request) {
        // HTTP 요청의 헤더에서 "Bearer "를 접두어로 가진 JWT를 추출
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
