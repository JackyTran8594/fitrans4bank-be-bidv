package com.eztech.fitrans.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProfileListDashBoardDTO implements Serializable {
    public static final long serialVerisionUID = 1L;
    public Long Id;
    public String username;
    public String fullName;
    public Integer state;
    public Integer totalTimeProcessing; //=> tổng thời gian dự kiến xử lý
    public Integer remainTimeCT; //=> tổng thời gian dự kiến xử lý
    public Integer timeChecker;
    public Integer standardTimeCT;
    public Integer standardTimeCM;
    public Integer additionalTime;
    public Integer totalTimeExpired; //=> tổng thời gian quá hạn
    public Integer profileProcessed; // => tông hồ sơ đã xử lý theo username
    public Integer profileProcessing; // tổng hồ sơ đang xử lý theo username
    public Integer profileExist; // tổng hồ sơ tồn theo username
    public Integer numberOfProfile; // tổng hồ sơ tồn theo username
    public LocalDateTime endTime;

    public LocalDateTime processDate;


}
