package com.Dodutch_Server.domain.expense.dto;

import com.Dodutch_Server.global.enums.ExpenseCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseInfoDto {

    private Long payer;
    private String title;        // 내용
    private ExpenseCategory expenseCategory;     // 카테고리
    private int amount;
    @JsonFormat(pattern = "yyyy-MM-dd")// 전체 결제 금액
    private LocalDate expenseDate;      // 날짜
    private MultipartFile expenseImage;    // 이미지 URL
    private String memo;
    private List<MemberShareDTO> members; // 멤버 정보 리스트

    @Getter
    @Setter
    public static class MemberShareDTO {
        private Long memberId;   // 멤버 ID
        private Integer cost;    // 개인별 금액
    }
}
