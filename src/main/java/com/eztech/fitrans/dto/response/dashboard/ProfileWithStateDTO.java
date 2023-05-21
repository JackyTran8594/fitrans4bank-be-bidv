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
    public Integer numberOfProfilesNonExpired; // => tong so ho so theo trạng thái chưa hết hạn
    public Integer numberOfProfilesExpired; // => tong so ho so theo trạng thái hết hạn
    // QLKH
    public Integer numberOfProfilesFinished; // => tong so ho so hoàn thành
    public Integer numberOfProfilesNotFinished; // => tong so ho so chưa hoàn thành
    public Integer profileCreatedOutOfTime_CM; // => tong so ho so đã tạo ngoài giờ QTTD
    public Integer profileCreatedOutOfTime_CT; // => tong so ho so đã tạo ngoài giờ GDKH
    public Integer profileExistInDay; // => tong so ho so cần bổ sung trong ngày
    public Integer profileExist; // => tong so ho so cần bổ sung trước đó
    // end QLKH
    public List<ProfileDTO> profilesNonExpired; // => ho so theo trang thai khong het han trong ngay
    public List<ProfileDTO> profilesExpired; // => ho so theo trang thai het han trong ngay
    public List<ProfileDTO> profilesAll; // => danh sách hồ sơ theo trạng thái (bao gồm cả hết hạn và chưa hết hạn)
}

