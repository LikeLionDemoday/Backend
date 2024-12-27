package com.Dodutch_Server.domain.expense.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExpenseResponseDTO {
    private int remainingCost;
    private List<CategoryCostDTO> categories;
    private List<MemberDTO> members;

    @Getter
    @Setter
    public static class CategoryCostDTO {
        private String category;
        private int cost;
    }

    @Getter
    @Setter
    public static class MemberDTO {
        private Long memberId;
        private String nickName;
    }
}
