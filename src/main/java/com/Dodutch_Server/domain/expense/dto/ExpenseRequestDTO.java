package com.Dodutch_Server.domain.expense.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ExpenseRequestDTO {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Long memberId;       // 결제자 멤버 ID
    private String title;        // 내용
    private String category;     // 카테고리
    private Integer amount;      // 전체 결제 금액
    private LocalDate date;      // 날짜
    private String payer;        // 결제자 이름
    //private String image_url;    // 이미지 URL
    private List<MemberShareDTO> members; // 멤버 정보 리스트

    @Getter
    @Setter
    public static class MemberShareDTO {
        private Long memberId;   // 멤버 ID
        private Integer cost;    // 개인별 금액
    }
}
