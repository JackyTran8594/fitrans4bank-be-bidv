package com.eztech.fitrans.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class OptionSetMasterData {
    private String code;
    private Long optionSetId;
    private String name;
    private String value;
    private String description;
}
