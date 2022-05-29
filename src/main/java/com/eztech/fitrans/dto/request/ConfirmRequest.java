package com.eztech.fitrans.dto.request;

import java.io.Serializable;
import java.util.Optional;

import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.model.Profile;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class   ConfirmRequest implements Serializable {

    public Long profileId;
    public String username;
    public String code;
    public Boolean isFinished;
    public Boolean isReturned;
    public Boolean isTranfered;
    public ProfileDTO profile;
}

