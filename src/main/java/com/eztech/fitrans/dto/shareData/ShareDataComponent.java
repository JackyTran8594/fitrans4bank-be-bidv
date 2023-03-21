package com.eztech.fitrans.dto.shareData;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Service
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShareDataComponent {
    private String username;

    private String password;

    private Boolean isLdap;
   
}