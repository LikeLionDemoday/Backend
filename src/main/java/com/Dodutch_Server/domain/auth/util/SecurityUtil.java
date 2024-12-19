package com.Dodutch_Server.domain.auth.util;


import com.Dodutch_Server.domain.auth.model.UserPrincipal;
import com.Dodutch_Server.global.common.apiPayload.code.status.ErrorStatus;
import com.Dodutch_Server.global.common.exception.handler.ErrorHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

//SecurityContext Authentication 에 있는 유저정보를 가져와서 userId 만 추출하는 Util
public class SecurityUtil {
    private SecurityUtil() {}

    public static Long getCurrentUserId() {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new ErrorHandler(ErrorStatus.UNAUTHORIZED);
        }

        Long userId;
        if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            userId = userPrincipal.getId();
        } else {
            throw new ErrorHandler(ErrorStatus.BAD_REQUEST);
        }

        return userId;
    }
}
