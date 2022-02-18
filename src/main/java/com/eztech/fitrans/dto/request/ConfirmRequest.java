package com.eztech.fitrans.dto.request;

import java.io.Serializable;

import com.eztech.fitrans.dto.response.ProfileDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmRequest implements Serializable {

    public ProfileDTO profile;
    public Boolean isCM;
    public Boolean isCT;  
}

