package com.Dodutch_Server.domain.member.repository;

import com.Dodutch_Server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByKakaoId(String kakaoId);

    Member findByRefreshToken(String refreshToken);

    boolean existsByNickName(String nickName);
}
