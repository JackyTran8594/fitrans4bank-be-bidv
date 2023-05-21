package com.eztech.fitrans.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String name;
    private String code;
    private Integer departmentId;
    private String position;
    private List<Integer> state;
    private Integer panelType;
}
