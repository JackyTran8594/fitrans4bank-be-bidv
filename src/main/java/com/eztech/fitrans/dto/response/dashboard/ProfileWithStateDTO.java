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
public class ProfileWithStateDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    public Integer profilesInday; // => ho so theo trang thai trong ngay
    public Integer profiles; // => tong so ho so luy ke
    public List<ProfileDTO> profilesNonExpired; // => ho so theo trang thai khong het han trong ngay
    public List<ProfileDTO> profilesExpired; // => ho so theo trang thai het han trong ngay
    public List<ProfileDTO> profilesAll; // => danh sách hồ sơ theo trạng thái (bao gồm cả hết hạn và chưa hết hạn)
}

