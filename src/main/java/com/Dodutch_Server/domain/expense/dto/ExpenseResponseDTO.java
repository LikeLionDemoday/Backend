package com.Dodutch_Server.domain.expense.dto;

import com.Dodutch_Server.global.enums.ExpenseCategory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExpenseResponseDTO {
    private Integer budget;
    private int remainingCost;
    private List<CategoryCostDTO> categories;
    private List<MemberDTO> members;

    @Getter
    @Setter
    public static class CategoryCostDTO {
        private ExpenseCategory expenseCategory;
        private int cost;
    }

    @Getter
    @Setter
    public static class MemberDTO {
        private Long memberId;
        private String nickName;
    }
}
