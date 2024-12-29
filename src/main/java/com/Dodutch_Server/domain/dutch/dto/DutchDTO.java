package com.Dodutch_Server.domain.dutch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DutchDTO {
    private Long payer;
    private Long payee;
    private Integer amountToPay;
}