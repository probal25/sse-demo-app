package com.probal.demoweb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMoneyRequest {
    private String fromAccountNumber;
    private String toAccountNumber;
    private Integer amount;
}
