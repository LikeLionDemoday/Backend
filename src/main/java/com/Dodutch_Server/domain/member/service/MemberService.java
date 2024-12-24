package com.Dodutch_Server.domain.member.service;


import com.Dodutch_Server.domain.member.entity.Member;
import com.Dodutch_Server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    public Optional<Member> findById(Long memberId) {
        return memberRepository.findById(memberId);
    }


    public boolean checkNickName(String nickName){

        boolean isExists = memberRepository.existsByNickName(nickName);

        if (isExists){ // 중복된 회원이 있을 경우
            return false;
        }
        else{
            return true;
        }
    }

    public boolean checkNickNameExists(Long memberId){

        Optional<Member> member = memberRepository.findById(memberId);

        if(member.get().getNickName() == null || member.get().getNickName().isBlank()){
            return false;
        }
        else{
            return true;
        }

    }

}