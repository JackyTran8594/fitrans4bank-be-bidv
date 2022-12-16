package com.eztech.fitrans.dto.response.dashboard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DashboardDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    public String title;
    public Integer value;

    public ProfileWithStateDTO profileProcessing;
    public ProfileWithStateDTO profileToDayFinished;
    public ProfileWithStateDTO profileReturn;
    public ProfileWithStateDTO profileFinished;
    public ProfileWithStateDTO profileReceived;
    public TotalProfileDTO profileExpect; // => hồ sơ dự kiến xử lý
    public TotalProfileDTO totalProfile;
    public List<ProfileListDashBoardDTO> profileListCMExist;
    public List<ProfileListDashBoardDTO> profileListCMProcessing;
    public List<ProfileListDashBoardDTO> profileListCTExist;
    public List<ProfileListDashBoardDTO> profileListCTProcessing;

    public DashboardDTO () {
        this.title = "";
        this.value = 0;
        this.profileProcessing = new ProfileWithStateDTO();
        this.profileToDayFinished = new ProfileWithStateDTO();
        this.profileReturn = new ProfileWithStateDTO();
        this.profileFinished = new ProfileWithStateDTO();
        this.profileReceived = new ProfileWithStateDTO();
        this.profileExpect = new TotalProfileDTO();
        this.totalProfile = new TotalProfileDTO();
        this.profileListCMExist = new ArrayList<>();
        this.profileListCMProcessing = new ArrayList<>();
        this.profileListCTExist = new ArrayList<>();
        this.profileListCTProcessing = new ArrayList<>();
    }
    
}

