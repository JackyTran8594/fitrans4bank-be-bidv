package com.eztech.fitrans.dto.response.dashboard;

import java.io.Serializable;
import java.util.List;

import com.eztech.fitrans.dto.response.ProfileDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalProfileDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    public Integer profilesInDay;
    public Integer profiles; // => tong so ho so
    public List<ProfileDTO> profilesAll; // => danh sách hồ sơ theo trạng thái (bao gồm cả hết hạn và chưa hết hạn)
    
}

