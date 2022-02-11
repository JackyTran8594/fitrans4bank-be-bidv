package com.eztech.fitrans.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRCodeDTO {

    private String staffId;
    private String staffId_CM;
    private String staffId_CT;
    private String transactionType;
    private BigDecimal value;
    // Trạng thái hồ sơ;
    private String stateEnum;
    private Long profileId;

    
}
